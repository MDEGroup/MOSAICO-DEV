#!/usr/bin/env python3
import os
import json
import argparse
from dataclasses import dataclass
from typing import Any, Dict, List

from langfuse import Langfuse
from ollama import Client as OllamaClient

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

    return RunCfg(
        lf_host=resolve_env(lf.get("host","https://cloud.langfuse.com"), "LANGFUSE_HOST", ignore_env),
        lf_pk=resolve_env(lf.get("public_key",""), "LANGFUSE_PUBLIC_KEY", ignore_env),
        lf_sk=resolve_env(lf.get("secret_key",""), "LANGFUSE_SECRET_KEY", ignore_env),
        lf_ignore_env=ignore_env,
        trace_name=tr.get("name","agentse.app.summarization"),
        tags=tr.get("tags",[]),
        provider=mdl.get("provider","ollama"),
        model_name=mdl.get("name","llama3.2:3b"),
        ollama_host=(mdl.get("ollama",{})).get("host","http://localhost:11434"),
        ollama_options=(mdl.get("ollama",{})).get("options",{}),
        prompt_template=pr.get("template","{{DESCRIPTION}}"),
        dataset_name=ds.get("dataset_name","dataset"),
        limit=int(ds.get("limit",0) or 0),
        start=int(ds.get("start",0) or 0),
    )


def make_prompt(tmpl: str, description_text: str) -> str:
    return tmpl.replace("{{DESCRIPTION}}", description_text)


def build_ollama_client(cfg: RunCfg) -> OllamaClient:
    return OllamaClient(host=cfg.ollama_host)


def llm_agent(*, item, cfg: RunCfg, client: OllamaClient):
    prompt_text = make_prompt(cfg.prompt_template, str(item.input))
    resp = client.generate(
        model=cfg.model_name,
        prompt=prompt_text,
        options=cfg.ollama_options,
        stream=False,
    )
    return resp.get("response") or resp.get("output") or ""


def main():
    ap = argparse.ArgumentParser(description="Langfuse dataset validation runner")
    ap.add_argument("--config", default="config.bench.json")
    ap.add_argument("--split", default="train", choices=["train","test"])
    args = ap.parse_args()

    J = load_config(args.config)
    CFG = make_cfg(J, args.split)

    if not CFG.lf_pk or not CFG.lf_sk:
        raise RuntimeError("Langfuse keys missing. Put them in config.bench.json or export env vars.")

    lf = Langfuse(public_key=CFG.lf_pk, secret_key=CFG.lf_sk, host=CFG.lf_host)

    if CFG.provider != "ollama":
        raise NotImplementedError("Only provider=ollama is supported in this template.")

    dataset = lf.get_dataset(CFG.dataset_name)
    client = build_ollama_client(CFG)

    if CFG.start:
        print("[runner] NOTE: Dataset.run_experiment does not support start offsets; all items before start will still be processed.")
    max_items = CFG.limit if CFG.limit else None

    dataset.run_experiment(
        name="run test",
        description="Validation run",
        task=llm_agent,
        task_kwargs={"cfg": CFG, "client": client},
        max_items=max_items,
    )
    print("[runner] done.")


+if __name__ == "__main__":
+    main()
