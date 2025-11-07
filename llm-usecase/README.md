# MOSAICO-UMAR Use Case

This repository contains a small demo script (`agent_llama_demo.py`) that integrates Langfuse and Ollama to trace and invoke a local LLM (via Ollama).

This README explains how to set up the Python environment, ensure Ollama Desktop is running and has a model installed, and how to run the use case.

## Quick start (one-liners)
If you want to try the demo quickly, from zsh you can run:

```bash
# activate the conda env (if created)
conda activate mosaico-umar

# install deps (only needed once)
pip install -r requirements.txt

# set Langfuse keys (temporary for this shell) and run the demo
export LANGFUSE_PUBLIC_KEY="pk-..." \
	LANGFUSE_SECRET_KEY="sk-..." \
	LANGFUSE_HOST="http://localhost:3000" \
	&& python agent_llama_demo.py
```

Notes:
- `agent_llama_demo.py` now requires `LANGFUSE_PUBLIC_KEY` and `LANGFUSE_SECRET_KEY` to be set as environment variables. The script will fail early with a clear error if they are missing.
- `requirements.txt` is included in the repo (generated from the environment used here).

## Requirements
- macOS with Ollama Desktop installed and running (the daemon exposes an API at `http://localhost:11434`).
- Conda / Miniconda (optional but recommended to isolate the Python environment).
- Enough disk space for the model you want to download (models can be many GBs).

## 1) Create / activate the Python environment (recommended)
If you haven't created the conda environment yet, run (zsh):

```bash
conda create -y -n mosaico-umar python=3.11
conda activate mosaico-umar
```

If you already have the `mosaico-umar` environment, activate it:

```bash
conda activate mosaico-umar
```

## 2) Install Python dependencies
A `requirements.txt` has been generated from the environment used here. Install dependencies with:

```bash
pip install -r requirements.txt
```

Note: `requirements.txt` includes the `langfuse` and `ollama` versions used in this workspace. Modify the file if you want different versions.

## 3) Start Ollama Desktop and install a model
1. Open Ollama Desktop.
2. In the UI, search for the model you want (the screenshot shows `gpt-oss:20b-cloud` as an example).
3. Click the model and choose Download / Pull / Install in the UI. The app will download the model for you.

Alternative (CLI): if you prefer the shell, install the `ollama` CLI and then download the model:

```bash
# install via Homebrew (if you prefer the CLI)
brew install ollama

# list installed models
ollama list

# pull a model (WARNING: may be very large)
ollama pull gpt-oss:20b-cloud
# or
ollama pull llama3.2:3b
```

After the download, `ollama list` will show the installed model.

## 4) Configure Langfuse
`agent_llama_demo.py` reads Langfuse keys from environment variables or falls back to placeholders in the file. It's recommended to export the variables in your shell (avoid placing keys in code):

```bash
export LANGFUSE_PUBLIC_KEY=pk-...
export LANGFUSE_SECRET_KEY=sk-...
# optional, if using a different host
export LANGFUSE_HOST=https://app.langfuse.io
```

If you prefer not to export variables, you can edit `agent_llama_demo.py` and set `LANGFUSE_PUBLIC_KEY` and `LANGFUSE_SECRET_KEY` directly (not recommended for security reasons).

## 5) Update the model name in code
Open `agent_llama_demo.py` and set `MODEL` to the exact name of the model installed in Ollama, for example:

```python
MODEL = "gpt-oss:20b-cloud"
# or
MODEL = "llama3.2:3b"
```

Use the exact model name shown in the Ollama UI or `ollama list`.

## 6) Run the use case
With the environment activated and dependencies installed, run the script:

```bash
python agent_llama_demo.py
```

Or explicitly using the conda environment's Python:

```bash
/Users/juridirocco/miniconda3/envs/mosaico-umar/bin/python agent_llama_demo.py
```

The script will attempt to:
- initialize Langfuse (using the provided keys),
- start a trace/generation in Langfuse,
- call the model on Ollama via HTTP,
- update the trace with outputs and metrics,
- print the trace URL (if Langfuse is reachable and credentials are valid).

## Quick debugging
- If you see `ModuleNotFoundError: No module named 'ollama'` or `langfuse`, ensure dependencies are installed in the active environment (see step 2).
- If you get `model '...' not found` from Ollama: the model is not installed locally; run `ollama list` or download it through the UI.
- If Langfuse returns 401/404 errors: credentials or host are likely incorrect; verify `LANGFUSE_PUBLIC_KEY`, `LANGFUSE_SECRET_KEY`, and `LANGFUSE_HOST`.

## Security notes
- Do not commit secrets to the repository.
- Prefer environment variables or a secrets manager for Langfuse keys.

## FAQ / next steps
- Want me to update `MODEL` in `agent_llama_demo.py` to the model name you see in the UI (e.g. `gpt-oss:20b-cloud`) and try to run the script here? I can, but the run will only succeed if that model is actually installed and Ollama Desktop is running.
- Want me to add a short `test_ollama_ping.py` script that calls the client and prints the raw response for quick debugging?

---

If you'd like, I can:
- update `MODEL` in `agent_llama_demo.py` to the name you provide, and/or
- add a small test script for Ollama connectivity.
