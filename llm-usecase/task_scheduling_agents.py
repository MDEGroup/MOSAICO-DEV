import os
import sys
from langchain_openai import ChatOpenAI
from langchain.agents import create_agent
from langchain.tools import tool
from langchain_core.messages import HumanMessage
from datetime import datetime
from langfuse import get_client, propagate_attributes
from langfuse.langchain import CallbackHandler
from langchain_core.messages import HumanMessage

from todoist_client import get_upcoming_tasks, get_completed_tasks_last_months
from stats_learner import learn_duration_stats
from duration_model import train_duration_model_from_completed,load_duration_model, predict_duration_minutes

from dotenv import load_dotenv; load_dotenv() 


OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    sys.exit("Error: OPENAI_API_KEY non impostata. Impostala come variabile d'ambiente.")

LANGFUSE_PUBLIC_KEY = os.getenv("LANGFUSE_PUBLIC_KEY")
LANGFUSE_BASE_URL = os.getenv("LANGFUSE_BASE_URL")
LANGFUSE_SECRET_KEY = os.getenv("LANGFUSE_SECRET_KEY")

TODOIST_API_TOKEN = os.getenv("TODOIST_API_TOKEN")
if not TODOIST_API_TOKEN:
    sys.exit("Error: TODOIST_API_TOKEN non impostata. Impostala come variabile d'ambiente.")




# Client globale
langfuse = get_client()

# Callback per LangChain
langfuse_handler = CallbackHandler(
    # opzionale: aggiorna sempre la trace col risultato finale
    update_trace=True
)

llm = ChatOpenAI(
    model="gpt-4.1-mini",
    temperature=0.2,
    openai_api_key=OPENAI_API_KEY
)

def update_model_from_todoist():
    completed = get_completed_tasks_last_months(months=3)
    print(f"[main] Retrieved {len(completed)} completed items from Todoist.")
    train_duration_model_from_completed(completed)

@tool
def todoist_upcoming_tool(days: int = 3) -> list:
    """Return upcoming Todoist tasks (open) for the next `days` days."""
    return get_upcoming_tasks(days=days)


@tool
def todoist_stats_tool(months: int = 3) -> dict:
    """Compute duration statistics from completed Todoist tasks for the last `months` months."""
    completed = get_completed_tasks_last_months(months=months)
    return learn_duration_stats(completed)


@tool
def estimate_task_durations(upcoming_tasks: list) -> list:
    """
    Enrich upcoming tasks with 'estimated_duration_minutes' using
    the trained duration model. If the model is missing, default
    to 30 minutes per task.
    """
    model = load_duration_model()
    default_minutes = 30

    enriched = []
    for t in upcoming_tasks:
        minutes = predict_duration_minutes(model, t) if model else None
        if minutes is None:
            minutes = default_minutes

        t2 = dict(t)
        t2["estimated_duration_minutes"] = int(minutes)
        enriched.append(t2)

    return enriched


def _load_workday_config():
    return {
        "workday_start": os.getenv("WORKDAY_START", "09:00"),
        "workday_end": os.getenv("WORKDAY_END", "18:00"),
        "lunch_start": os.getenv("LUNCH_START", "13:00"),
        "lunch_end": os.getenv("LUNCH_END", "14:00"),
    }


def make_scheduler_agent():
    cfg = _load_workday_config()
    system_prompt = f"""
You are a time-blocking assistant for a busy professor in computer science.
Your goal is to schedule tasks over today and the next two days using realistic time blocks.

Constraints:
- Workday: {cfg['workday_start']}-{cfg['workday_end']} local time.
- Lunch break (no tasks): {cfg['lunch_start']}-{cfg['lunch_end']}.
- Prefer deep work blocks in the morning, shallow tasks (email/admin) later.
- Do NOT exceed 6-7 hours of scheduled work per day.
- Output a JSON array of days, each with time blocks:
  [
    {{
      "date": "YYYY-MM-DD",
      "blocks": [
        {{
          "start": "HH:MM",
          "end": "HH:MM",
          "task_id": "1234567890",
          "title": "...",
          "project": "Project name",
          "labels": ["deep-work", "research"],
          "notes": "short rationale"
        }},
        ...
      ]
    }},
    ...
  ]
"""

    tools = []  # no tools, just planning logic

    return create_agent(
        model=llm,
        tools=tools,
        system_prompt=system_prompt,
    )


def make_supervisor_agent():
    system_prompt = """
You coordinate a team of tools to produce a 3-day time-blocking plan.

Workflow:
1) Call todoist_upcoming_tool(days=3) to get upcoming tasks.
2) Call todoist_stats_tool(months=3) to learn duration statistics.
3) Call estimate_task_durations(...) to enrich tasks with estimated_duration_minutes.
4) Call the scheduler agent (via scheduling_tool tool) to produce the final JSON schedule.

Return ONLY the final JSON schedule.
"""

    # esponiamo lo scheduler come tool
    scheduler_agent = make_scheduler_agent()

    @tool
    def scheduling_tool(tasks_with_estimates: list) -> str:
        """Create a 3-day time-blocking JSON schedule from tasks_with_estimates."""
        msg = HumanMessage(content=(
            "You receive the following upcoming tasks with estimated durations "
            "in minutes. Plan time blocks over today + next two days. Please, keep in the target JSON, for each task the corresponding Todoist link to open directly the task. \n\n"
            f"{tasks_with_estimates}"
        ))
        res = scheduler_agent.invoke({"messages": [msg]})
        return res["messages"][-1].content

    tools = [todoist_upcoming_tool, todoist_stats_tool, estimate_task_durations, scheduling_tool]

    return create_agent(
        model=llm,
        tools=tools,
        system_prompt=system_prompt,
    )


@tool
def estimate_task_durations(upcoming_tasks: list, stats: dict | None = None) -> list:
    """
    Enrich upcoming tasks with 'estimated_duration_minutes' using a learned model.
    Fallback to stats if model not available or prediction fails.
    """
    model = load_duration_model()
    # opzionale: se stats è None, calcola al volo da completed
    if stats is None:
        stats = {"global_avg": 30.0, "by_label": {}, "by_project": {}}

    global_avg = stats.get("global_avg", 30)
    by_label = stats.get("by_label", {})
    by_project = stats.get("by_project", {})

    enriched = []
    for t in upcoming_tasks:
        minutes = None

        # 1) se Todoist ha già durata, prendila
        if t.get("duration") and t.get("duration_unit"):
            from duration_model import _duration_field_to_minutes
            minutes = _duration_field_to_minutes(t)

        # 2) prova modello
        if minutes is None and model is not None:
            minutes = predict_duration_minutes(model, t)

        # 3) fallback heuristico se serve
        if minutes is None:
            labels = t.get("labels", [])
            label_minutes = [by_label[l] for l in labels if l in by_label]
            if label_minutes:
                minutes = sum(label_minutes) / len(label_minutes)
            else:
                proj_id = t.get("project_id")
                if proj_id in by_project:
                    minutes = by_project[proj_id]
                else:
                    minutes = global_avg

        t2 = dict(t)
        t2["estimated_duration_minutes"] = int(minutes)
        enriched.append(t2)

    return enriched


def build_schedule(session_id: str | None = None):
    supervisor = make_supervisor_agent()

    print("Building 3-day time-block schedule from Todoist...")

    with langfuse.start_as_current_observation(
        as_type="agent",
        name="todoist-time-blocking-run",
        metadata={"session_id": session_id},
    ) as trace:
        # Propaga attributi (tag, user ecc.)
        with propagate_attributes(
            tags=["todoist-planner", "time-blocking"],
            # opzionale: user/session
            session_id=session_id or "local-cli"
        ):
            result = supervisor.invoke(
                {"messages": [HumanMessage(content="Plan my next 3 days.")]},
                config={
                    "callbacks": [langfuse_handler],
                    "metadata": {
                        "langfuse_session_id": session_id or "local-cli",
                        "langfuse_tags": ["todoist-planner", "time-blocking"],
                    },
                },
            )
            schedule_json = result["messages"][-1].content
            return schedule_json


def main():
    schedule = build_schedule()
    with open("out/schedule.json", "w", encoding="utf-8") as f:
        f.write(schedule)
    print("\n[Generated 3-day schedule]\n")
    print(schedule)
    langfuse.flush()


if __name__ == "__main__":
    update_model_from_todoist()
    main()
