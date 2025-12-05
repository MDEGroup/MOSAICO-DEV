"""Ollama-based summarization agent callable from Langfuse datasets."""
from dataclasses import dataclass
from typing import Any, Dict, Callable

from ollama import Client as OllamaClient


@dataclass
class AgentConfig:
    model: str
    host: str
    options: Dict[str, Any]
    prompt_template: str


def _build_prompt(template: str, description: str) -> str:
    return template.replace("{{DESCRIPTION}}", description)


def build_agent(config: AgentConfig) -> Callable[..., str]:
    """Return a callable that matches Langfuse task signature."""
    client = OllamaClient(host=config.host)
    options = config.options or {}

    def _agent(*, item):
        prompt_text = _build_prompt(config.prompt_template, str(item.input))
        resp = client.generate(
            model=config.model,
            prompt=prompt_text,
            options=options,
            stream=False,
        )
        return resp.get("response") or resp.get("output") or ""

    return _agent
