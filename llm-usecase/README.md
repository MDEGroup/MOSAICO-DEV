# MOSAICO- UmarUeCase- agentSEBench

This repository contains a **config-driven benchmarking framework** (`agentSEBench`) that integrates **Langfuse Cloud** and **Ollama** to evaluate LLM-based agents.  
It enables running any agent configuration (prompt, dataset, metrics, and model) entirely from a single JSON file — no code modification required.

This README explains how to set up the environment, connect Langfuse Cloud, run the agent, and evaluate results.

---

## Quick start (one-liners)
If you want to try the benchmark quickly, from zsh you can run:

```bash
# activate your environment (if created)
source .venv/bin/activate

# install dependencies
pip install -r requirements.txt

# set Langfuse credentials (temporary for this shell)
export LANGFUSE_PUBLIC_KEY="pk-..." \
	LANGFUSE_SECRET_KEY="sk-..." \
	LANGFUSE_HOST="https://cloud.langfuse.com" \
	&& python runner.py --config config.bench.json
```

Notes:
- `runner.py` reads all configurations from `config.bench.json` — you don’t need to edit the code.
- `requirements.txt` includes compatible versions for `langfuse`, `ollama`, `pandas`, `scikit-learn`, and `rouge-score`.
- Results are logged in **Langfuse Cloud** under the project specified in your API keys.

---

## Requirements
- macOS or Linux with **Ollama** installed (the daemon runs at `http://localhost:11434`).
- Python 3.9+ environment (use venv or Conda).
- A **Langfuse Cloud account** and API keys (from [cloud.langfuse.com](https://cloud.langfuse.com)).
- Internet connection (for Langfuse API communication).

---

## 1) Create / activate the Python environment (recommended)
If you don’t already have one:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

or, with Conda:

```bash
conda create -y -n agentsebench python=3.11
conda activate agentsebench
```

---

## 2) Install Python dependencies
Install all required packages:

```bash
pip install -r requirements.txt
```

This installs:
- `langfuse` – for trace logging and evaluation
- `ollama` – for local model inference
- `pandas`, `scikit-learn`, `rouge-score` – for metrics computation

---

## 3) Start Ollama and pull a model
Make sure **Ollama Desktop** or the daemon is running locally:

```bash
brew install ollama
ollama serve
ollama pull llama3.2:3b
```

Verify installation:

```bash
ollama list
```

---

## 4) Configure Langfuse
Before running, export your Langfuse credentials:

```bash
export LANGFUSE_PUBLIC_KEY="pk-..."
export LANGFUSE_SECRET_KEY="sk-..."
export LANGFUSE_HOST="https://cloud.langfuse.com"
```

You can also configure them permanently in your shell profile.

---

## 5) Update or inspect your configuration file
All benchmark parameters live in `config.bench.json`, including:
- **Model setup** (provider, Ollama host, model name)
- **Prompt template**
- **Dataset path and columns**
- **Langfuse host**
- **Metrics definitions**

Example snippet:

```json
{
  "langfuse": { "host": "https://cloud.langfuse.com" },
  "trace": { "name": "agentse.app.summarization" },
  "model": { "provider": "ollama", "name": "llama3.2:3b" },
  "prompt": {
    "template": "You are a senior software engineer. Read the input product description and summarize it for a technical audience.\n\nDESCRIPTION:\n{{DESCRIPTION}}\n\nWrite the summary now."
  },
  "dataset": {
    "dataset_name": "ause_train",
    "csv_path": "train_data.csv",
    "input_col": "description_html_clean",
    "gold_col": "description_short"
  }
}
```

---

## 6) Run the benchmark agent
With the environment activated, run:

```bash
python runner.py --config config.bench.json
```

This will:
- Load the dataset from CSV  
- Build prompts dynamically from the template  
- Call the Ollama model locally  
- Log all traces, inputs, and outputs to Langfuse Cloud  
- Print trace URLs to the terminal  

Example output:
```
[agent] rows 0..59 | model=llama3.2:3b | dataset=ause_train
[agent] [0] https://cloud.langfuse.com/project/xyz/traces/1234abcd
```

---

## 7) Run evaluation
After generating traces, evaluate them with the metrics defined in your configuration:

```bash
python evaluator.py --config config.bench.json --export-csv eval_report.csv
```

This step:
- Fetches all traces for your dataset from Langfuse  
- Computes ROUGE, cosine similarity, and other metrics locally  
- Pushes metric scores back to Langfuse  
- Saves a CSV report (`eval_report.csv`) with all computed values  

Example result snippet:

| trace_id | rouge1_f | cosine_pred_gold | len_ratio | model |
|-----------|-----------|------------------|------------|--------|
| 1234abcd  | 0.63      | 0.82             | 1.05       | llama3.2:3b |

---

## Quick debugging
- **401 Unauthorized** → Wrong Langfuse keys or host  
- **model not found** → Run `ollama pull llama3.2:3b`  
- **Empty CSV** → No predictions or outputs found in traces  
- **SSL warning (LibreSSL)** → Safe to ignore on macOS  

---

## Security notes
- Do **not** hardcode API keys in scripts or config files.
- Always use environment variables or `.env` (excluded from git).
- Remove sensitive data before committing.

---

## Summary
### What this framework does:
✅ Runs any LLM agent via Ollama  
✅ Logs all traces automatically to Langfuse Cloud  
✅ Computes evaluation metrics defined in JSON  
✅ Generates a local CSV report for reproducible results  

### What you can modify:
- Change the model (`model.name`)  
- Edit prompt templates  
- Replace dataset paths  
- Add or remove metrics in config  

---

## Example end-to-end run

```bash
# install dependencies
pip install -r requirements.txt

# set environment keys
export LANGFUSE_PUBLIC_KEY="pk-..."
export LANGFUSE_SECRET_KEY="sk-..."
export LANGFUSE_HOST="https://cloud.langfuse.com"

# run agent
python runner.py --config config.bench.json

# evaluate
python evaluator.py --config config.bench.json --export-csv eval_report.csv
```

Once complete, open **Langfuse Cloud** → “Traces” → “agentse.app.summarization” to visualize results.

---

## Maintainer
**Umar Zeshan**  
📧 muhammadumarzeshan@gmail.com  
🔗 [Langfuse Cloud](https://cloud.langfuse.com) | [MDEGroup/MOSAICO-DEV](https://github.com/MDEGroup/MOSAICO-DEV)
