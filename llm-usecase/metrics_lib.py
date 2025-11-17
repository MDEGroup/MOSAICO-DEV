from typing import Dict, Any, Tuple, List
from rouge_score import rouge_scorer
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def _get(texts: Dict[str, str], key: str) -> str:
    v = texts.get(key, "")
    if isinstance(v, dict):
        # If someone accidentally passed a dict, stringify gracefully
        return str(v)
    return v or ""

def m_rouge(texts: Dict[str,str], params: Dict[str,Any]) -> Dict[str, float]:
    use_stemmer = bool(params.get("use_stemmer", True))
    pairs: List[Tuple[str,str]] = [tuple(p) for p in params.get("pairs", [])]
    out: Dict[str,float] = {}
    rs = rouge_scorer.RougeScorer(["rouge1","rougeL"], use_stemmer=use_stemmer)
    for a, b in pairs:
        pred = _get(texts, a); gold = _get(texts, b)
        s = rs.score(gold, pred)
        out["rouge1_f"] = float(s["rouge1"].fmeasure)
        out["rougeL_f"] = float(s["rougeL"].fmeasure)
    return out

def m_cosine(texts: Dict[str,str], params: Dict[str,Any]) -> Dict[str,float]:
    pairs: List[Tuple[str,str]] = [tuple(p) for p in params.get("pairs", [])]
    uniq = set()
    for a,b in pairs:
        uniq.update([a,b])
    corpus = [_get(texts,k) for k in uniq]
    vec = TfidfVectorizer().fit(corpus if corpus else ["",""])
    emb_map = {k: vec.transform([_get(texts,k)]) for k in uniq}
    out: Dict[str,float] = {}
    for a,b in pairs:
        out_name = "cosine_"+a+"_"+b
        if (a,b)==("pred","gold"):   out_name = "cosine_pred_gold"
        if (a,b)==("pred","source"): out_name = "cosine_pred_source"
        out[out_name] = float(cosine_similarity(emb_map[a], emb_map[b])[0,0])
    return out

def m_length_ratio(texts: Dict[str,str], params: Dict[str,Any]) -> Dict[str,float]:
    num = _get(texts, params.get("num","pred"))
    den = _get(texts, params.get("den","gold"))
    out_name = params.get("out","len_ratio")
    num_len = max(1e-6, len(num.strip()))
    den_len = max(1, len(den.strip()))
    return {out_name: round(num_len/den_len, 3)}

def m_substring(texts: Dict[str,str], params: Dict[str,Any]) -> Dict[str,float]:
    needle = _get(texts, params.get("needle","gold")).strip()
    hay = _get(texts, params.get("haystack","pred"))
    out_name = params.get("out","exact_contains")
    return {out_name: 1.0 if (needle and needle in hay) else 0.0}

REGISTRY = {
    "rouge": m_rouge,
    "cosine": m_cosine,
    "length_ratio": m_length_ratio,
    "substring": m_substring,
}