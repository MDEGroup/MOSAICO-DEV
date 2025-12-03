#!/usr/bin/env python3
import os, json, time, argparse
from dataclasses import dataclass
from typing import Dict, Any, List, Optional
from datetime import datetime, UTC

import pandas as pd
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
    csv_path: str
    input_col: str
    gold_col: str
    create_dataset: bool
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
        csv_path=ds.get("csv_path","train_data.csv"),
        input_col=ds.get("input_col","description_html_clean"),
        gold_col=ds.get("gold_col","description_short"),
        create_dataset=bool(ds.get("create_dataset", True)),
        limit=int(ds.get("limit",0) or 0),
        start=int(ds.get("start",0) or 0),
    )

def make_prompt(tmpl: str, description_text: str) -> str:
    return tmpl.replace("{{DESCRIPTION}}", description_text)

def ensure_dataset_items(lf: Langfuse, cfg: RunCfg, df: pd.DataFrame):
    if not cfg.create_dataset:
        return
    try:
        lf.create_dataset(name=cfg.dataset_name, description=f"Auto dataset {cfg.dataset_name}")
    except Exception:
        pass
    for i, row in df.iterrows():
        try:
            lf.create_dataset_item(
                dataset_name=cfg.dataset_name,
                input={"description": str(row[cfg.input_col])},
                expected_output=str(row[cfg.gold_col]),
                metadata={"source": "csv", "row_index": int(i)},
            )
        except Exception:
            pass

def main():
    ap = argparse.ArgumentParser(description="agentSEBench runner (config-driven)")
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
    ollama = OllamaClient(host=CFG.ollama_host)

    df = pd.read_csv(CFG.csv_path)
    for c in (CFG.input_col, CFG.gold_col):
        if c not in df.columns:
            raise ValueError(f"Missing column '{c}' in {CFG.csv_path}. found: {list(df.columns)}")
    print(f"[runner] loaded {len(df)} rows from {CFG.csv_path}")
    ensure_dataset_items(lf, CFG, df)
    print(f"[runner] ensured dataset items in Langfuse dataset '{CFG.dataset_name}'")
    begin = CFG.start
    end = min(len(df), begin + CFG.limit) if CFG.limit and CFG.limit > 0 else len(df)
    print(f"[runner] split={args.split} rows={begin}..{end-1} model={CFG.model_name} dataset={CFG.dataset_name}")

    for idx in range(begin, end):
        print(f"[runner] processing row {idx}...")
        row = df.iloc[idx]
        source = str(row[CFG.input_col])
        gold = str(row[CFG.gold_col]) if pd.notna(row[CFG.gold_col]) else ""
        prompt_text = make_prompt(CFG.prompt_template, source)

        trace = lf.trace(
            name=CFG.trace_name,
            input={"split": args.split, "gold_len": len(gold), "source_len": len(source)},
            metadata={
                "dataset": CFG.dataset_name,
                "csv_path": CFG.csv_path,
                "row_index": int(idx),
                "input_col": CFG.input_col,
                "gold_col": CFG.gold_col,
                "model": CFG.model_name,
                "provider": CFG.provider,
                "tags": CFG.tags,
                "run_id": datetime.now(UTC).strftime("%Y%m%dT%H%M%SZ")
            },
            tags=CFG.tags
        )
        gen = trace.generation(name="model.generate", model=CFG.model_name, input=prompt_text)

        t0 = time.time()
        try:
            resp = ollama.generate(
                model=CFG.model_name,
                prompt=prompt_text,
                options=CFG.ollama_options,
                stream=False
            )
            pred = resp.get("response") or resp.get("output") or ""
            latency = round(time.time() - t0, 3)

            gen.update(output=pred)
            gen.score(name="latency_sec", value=latency)
            gen.score(name="success", value=True)

            # store all texts on the trace output so evaluator can read locally
            trace.update(output={
                "gold_summary": gold,
                "pred_summary": pred,
                "source_text": source
            })
        except Exception as e:
            gen.update(output=str(e))
            gen.score(name="success", value=False)
            trace.update(output={"error": str(e)})

        lf.flush()

    print("[runner] done.")

if __name__ == "__main__":
    main()