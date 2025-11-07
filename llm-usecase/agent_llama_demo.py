import os, time
from langfuse import Langfuse
from ollama import Client

# --- Configuration: Langfuse keys ---
# Langfuse keys are read from environment variables. This avoids storing secrets in
# source code. Set the following environment variables in your shell before running:
# LANGFUSE_PUBLIC_KEY and LANGFUSE_SECRET_KEY. Optionally set LANGFUSE_HOST.
LANGFUSE_HOST = os.environ.get("LANGFUSE_HOST", "http://localhost:3000")
LANGFUSE_PUBLIC_KEY = os.environ.get("LANGFUSE_PUBLIC_KEY")
LANGFUSE_SECRET_KEY = os.environ.get("LANGFUSE_SECRET_KEY")

if not LANGFUSE_PUBLIC_KEY or not LANGFUSE_SECRET_KEY:
    raise RuntimeError(
        "Environment variables LANGFUSE_PUBLIC_KEY and LANGFUSE_SECRET_KEY must be set"
    )

lf = Langfuse(
    public_key=LANGFUSE_PUBLIC_KEY,
    secret_key=LANGFUSE_SECRET_KEY,
    host=LANGFUSE_HOST,
)

ollama_client = Client(host="http://localhost:11434")
MODEL = "llama3.2:3b"  # model

def se_task(prompt: str):
    trace = lf.trace(
        name="agentse.prompt.llama",
        input={"prompt": prompt},
        metadata={"task": "code_reasoning", "model": MODEL}
    )
    gen = trace.generation(name="llama_generation", model=MODEL, input=prompt)

    t0 = time.time()
    try:
        response = ollama_client.generate(model=MODEL, prompt=prompt, stream=False)
        answer = response.get("response") or response.get("output") or str(response)
        gen.update(output=answer)
        print("Model output:\n", answer)
        gen.score(name="latency_sec", value=round(time.time() - t0, 3))
        gen.score(name="success", value=1.0)
        trace.score(name="task_success", value=1.0)
        print("OK")
    except Exception as e:
        gen.update(output=str(e))
        gen.score(name="success", value=0.0)
        trace.score(name="task_success", value=0.0)
        print("Error during generation:", str(e))
    lf.flush()
    print("Trace URL:", trace.get_trace_url())

if __name__ == "__main__":
    se_task("Explain why this Python unit test might fail:\nassert add(2,2)==5")