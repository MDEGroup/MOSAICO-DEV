#!/usr/bin/env python3
import os, json, base64, time, argparse
from typing import Dict, Any, List
import requests, pandas as pd
from langfuse import Langfuse
from metrics_lib import REGISTRY

def load_config(path: str) -> Dict[str,Any]:
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def lf_host_from(J: Dict[str,Any]) -> str:
    return os.environ.get("LANGFUSE_HOST", J["langfuse"]["host"])

def lf_keys(J: Dict[str,Any]) -> Dict[str,str]:
    return {
        "pk": os.environ.get("LANGFUSE_PUBLIC_KEY", J["langfuse"]["public_key"]),
        "sk": os.environ.get("LANGFUSE_SECRET_KEY", J["langfuse"]["secret_key"]),
    }

def auth_hdr(pk: str, sk: str) -> Dict[str,str]:
    tok = base64.b64encode(f"{pk}:{sk}".encode()).decode()
    return {"Authorization": f"Basic {tok}"}

def get_traces_page(lf_host: str, pk: str, sk: str, trace_name: str, dataset_name: str, page: int, limit: int=100) -> Dict[str,Any]:
    url = f"{lf_host}/api/public/traces"
    params = {"name": trace_name, "page": page, "limit": min(limit,100)}
    r = requests.get(url, headers={"Accept":"application/json", **auth_hdr(pk,sk)}, params=params, timeout=60)
    if r.status_code == 429:
        time.sleep(int(r.headers.get("Retry-After","2")))
        r = requests.get(url, headers={"Accept":"application/json", **auth_hdr(pk,sk)}, params=params, timeout=60)
    r.raise_for_status()
    payload = r.json()
    data = [t for t in payload.get("data",[]) if (t.get("metadata") or {}).get("dataset")==dataset_name]
    total_pages = int((payload.get("meta") or {}).get("totalPages", page))
    return {"data": data, "total_pages": total_pages}

def ensure_score_configs(lf: Langfuse, mcfg: Dict[str,Any]):
    for sc in mcfg.get("score_configs", []):
        try:
            lf.create_score_config(**sc)
        except Exception:
            pass

def compute_from_config(pred: str, gold: str, source: str, comp_cfg: List[Dict[str,Any]]) -> Dict[str,Any]:
    texts = {"pred": pred or "", "gold": gold or "", "source": source or ""}
    out: Dict[str,Any] = {}
    for block in comp_cfg:
        kind = block.get("kind")
        params = block.get("params", {})
        fn = REGISTRY.get(kind)
        if not fn: 
            continue
        out.update(fn(texts, params))
    return out

def main():
    ap = argparse.ArgumentParser(description="agentSEBench evaluator (config-driven)")
    ap.add_argument("--config", default="config.bench.json")
    ap.add_argument("--split", default="train", choices=["train","test"])
    args = ap.parse_args()

    J = load_config(args.config)
    lf_host = lf_host_from(J)
    keys = lf_keys(J)
    if not keys["pk"] or not keys["sk"]:
        raise RuntimeError("Langfuse keys missing. Put them in config.bench.json or export env vars.")

    trace_name = J["trace"]["name"]
    ds_cfg = J["datasets"][args.split]
    dataset_name = ds_cfg["dataset_name"]

    mcfg = J["metrics"]
    lf = Langfuse(public_key=keys["pk"], secret_key=keys["sk"], host=lf_host)
    ensure_score_configs(lf, mcfg)

    export_csv = (J["eval"]["export_csv_train"] if args.split=="train" else J["eval"]["export_csv_test"])
    page_sleep = float(J["eval"].get("page_sleep_sec", 0.4))

    all_rows: List[Dict[str,Any]] = []
    page = 1
    while True:
        batch = get_traces_page(lf_host, keys["pk"], keys["sk"], trace_name, dataset_name, page=page)
        data = batch["data"]
        if not data:
            break
        for t in data:
            tid = t["id"]
            out = t.get("output") or {}
            meta = t.get("metadata") or {}
            inp = t.get("input") or {}

            gold = str(out.get("gold_summary") or "")
            pred = str(out.get("pred_summary") or "")
            source_text = str(out.get("source_text") or "")

            if not gold or not pred:
                continue

            metrics = compute_from_config(pred, gold, source_text, mcfg.get("compute", []))

            tr = lf.trace(id=tid)
            for k, v in metrics.items():
                tr.score(name=k, value=float(v) if isinstance(v,(int,float)) else (1.0 if v else 0.0))

            all_rows.append({
                "trace_id": tid,
                "gold_len": len(gold),
                "pred_len": len(pred),
                **metrics,
                "trace_url": f"{lf_host}{t.get('htmlPath','')}",
                "model": (meta.get("model") or ""),
                "split": inp.get("split","")
            })

        lf.flush()
        page += 1
        if page > batch["total_pages"]:
            break
        time.sleep(page_sleep)

    pd.DataFrame(all_rows).to_csv(export_csv, index=False)
    print(f"[evaluator] wrote {export_csv} with {len(all_rows)} rows")

if __name__ == "__main__":
    main()