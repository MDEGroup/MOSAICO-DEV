#!/usr/bin/env python3
"""
Variante 1: Utilizza llama3.2:1b - modello ultra leggero e veloce
"""
import os
import json
import argparse
from dataclasses import dataclass
from typing import Any, Dict, List

from langfuse import Langfuse

from ollama_agent import AgentConfig, build_agent

# Configurazione specifica per questa variante
VARIANT_NAME = "variant1_llama32_1b_fast"
VARIANT_MODEL = "llama3.2:1b"
VARIANT_OPTIONS = {
    "temperature": 0.3,
    "top_p": 0.9,
    "top_k": 40,
    "num_predict": 256,
}

@dataclass
class RunCfg:
    lf_host: str
    lf_pk: str
    lf_sk: str
    lf_ignore_env: bool
    trace_name: str
    tags: List[str]
    provider: str
    model_name: str
    ollama_host: str
    ollama_options: Dict[str, Any]
    prompt_template: str
    dataset_name: str
    limit: int
    start: int

def load_config(path: str) -> Dict[str, Any]:
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def resolve_env(default: str, env_key: str, ignore_env: bool = False) -> str:
    """Prefer config value unless env overrides are enabled."""
    if ignore_env:
        return default
    env_val = os.environ.get(env_key)
    return env_val or default

def make_cfg(j: Dict[str,Any], split: str) -> RunCfg:
    lf = j["langfuse"]
    tr = j["trace"]
    mdl = j["model"]
    pr = j["prompt"]
    ds = j["datasets"][split]

    ignore_env = bool(lf.get("ignore_env", False))

    # Override con i parametri specifici della variante
    base_options = (mdl.get("ollama",{})).get("options",{})
    merged_options = {**base_options, **VARIANT_OPTIONS}

    return RunCfg(
        lf_host=resolve_env(lf.get("host","https://cloud.langfuse.com"), "LANGFUSE_HOST", ignore_env),
        lf_pk=resolve_env(lf.get("public_key",""), "LANGFUSE_PUBLIC_KEY", ignore_env),
        lf_sk=resolve_env(lf.get("secret_key",""), "LANGFUSE_SECRET_KEY", ignore_env),
        lf_ignore_env=ignore_env,
        trace_name=tr.get("name","agentse.app.summarization") + f".{VARIANT_NAME}",
        tags=tr.get("tags",[]) + [VARIANT_NAME, VARIANT_MODEL],
        provider=mdl.get("provider","ollama"),
        model_name=VARIANT_MODEL,  # Override del modello
        ollama_host=(mdl.get("ollama",{})).get("host","http://localhost:11434"),
        ollama_options=merged_options,  # Opzioni merged
        prompt_template=pr.get("template","{{DESCRIPTION}}"),
        dataset_name=ds.get("dataset_name","dataset"),
        limit=int(ds.get("limit",0) or 0),
        start=int(ds.get("start",0) or 0),
    )

def main():
    ap = argparse.ArgumentParser(description=f"agentSEBench runner - {VARIANT_NAME}")
    ap.add_argument("--config", default="config.bench.json")
    ap.add_argument("--split", default="train", choices=["train","test"])
    args = ap.parse_args()

    J = load_config(args.config)
    CFG = make_cfg(J, args.split)

    if not CFG.lf_pk or not CFG.lf_sk:
        raise RuntimeError("Langfuse keys missing. Put them in config.bench.json or export env vars.")

   
    if CFG.provider != "ollama":
        raise NotImplementedError("Only provider=ollama is supported in this template.")


    
    agent_config = AgentConfig(
        model=CFG.model_name,
        host=CFG.ollama_host,
        options=CFG.ollama_options,
        prompt_template=CFG.prompt_template,
    ) 
    agent = build_agent(agent_config)
    lf = Langfuse(public_key=CFG.lf_pk, secret_key=CFG.lf_sk, host=CFG.lf_host)
    dataset = lf.get_dataset(CFG.dataset_name)
    begin = CFG.start if CFG.start >= 0 else 0
    end = 50 #begin + CFG.limit if CFG.limit > 0 else None
    selected_items = dataset.items[begin:end]
    if not selected_items:
        raise RuntimeError("Selected dataset slice returned no items")
    # Nome esperimento specifico per questa variante
    experiment_name = f"experiment_{VARIANT_NAME}_{args.split}"
    
    result = lf.run_experiment(
        name=experiment_name,
        description=f"Experiment using {VARIANT_MODEL} - ultra lightweight and fast model (temp=0.3)",
        data=selected_items,
        task=agent,
        metadata={
            "model": CFG.model_name,
            "provider": CFG.provider,
            "variant": VARIANT_NAME,
            "options": CFG.ollama_options,
        },
    )
    if getattr(result, "dataset_run_url", None):
        print(f"[{VARIANT_NAME}] view results: {result.dataset_run_url}")
    print(f"[{VARIANT_NAME}] done.")

if __name__ == "__main__":
    main()
