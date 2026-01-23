# MOSAICO - Demo Interattiva Benchmarking via Swagger API

Questa guida ti accompagna passo passo nella creazione e esecuzione di un benchmark utilizzando le API REST esposte dall'applicazione MOSAICO.

## Prerequisiti

1. **Avviare l'applicazione** con il profilo `dev`:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Swagger UI** disponibile all'indirizzo:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Langfuse** deve essere raggiungibile (default: `http://localhost:3000`)

4. **Database PostgreSQL** attivo e configurato

---

## Panoramica del Flusso

```
┌─────────────┐    ┌─────────────┐    ┌─────────────────┐    ┌─────────────┐
│  1. Creare  │───▶│  2. Creare  │───▶│  3. Eseguire    │───▶│ 4. Vedere   │
│   3 Agent   │    │  Benchmark  │    │  Benchmark Run  │    │  Risultati  │
└─────────────┘    └─────────────┘    └─────────────────┘    └─────────────┘
```

---

## Step 1: Creare i 3 Agent

Creiamo 3 agenti di summarization con configurazioni diverse per confrontare le performance.

### 1.1 Agent Baseline (llama3.2:3b, temperature=0)
f714f29f-a531-42a2-b4aa-f79c00d4870b"
```
POST /api/agents
```

```json
{
  "id": "agent-baseline",
  "name": "SummarizationBot Baseline",
  "description": "Agente baseline per generazione descrizioni GitHub. Usa llama3.2:3b con temperature=0 per output deterministici.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
  "llangfuseUrl": "http://localhost:3000",
  "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
  "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
  "llangfuseProjectName": "mosaic-test-project",
  "deployment": {
    "mode": "DOCKER",
    "endpoint": "ghcr.io/mosaico/llama32-3b:latest"
  }
}
```

```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "agent-baseline",
    "name": "SummarizationBot Baseline",
    "description": "Agente baseline per generazione descrizioni GitHub. Usa llama3.2:3b con temperature=0 per output deterministici.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
    "llangfuseUrl": "http://localhost:3000",
    "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
    "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
    "llangfuseProjectName": "mosaic-test-project",
    "deployment": {
      "mode": "DOCKER",
      "endpoint": "ghcr.io/mosaico/llama32-3b:latest"
    }
  }'
```

### 1.2 Agent Creative (llama3.1:8b, temperature=0.8)
  "id": "437ac1c8-72b1-43ef-b1a7-3523f060740a",
```json
{
  "id": "agent-variant1-creative",
  "name": "SummarizationBot Creative",
  "description": "Variante creativa con llama3.1:8b e temperature=0.8 per output piu' variati e creativi.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
  "llangfuseUrl": "http://localhost:3000",
  "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
  "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
  "llangfuseProjectName": "mosaic-test-project",
  "deployment": {
    "mode": "DOCKER",
    "endpoint": "ghcr.io/mosaico/llama31-8b:latest"
  }
}
```

```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "agent-variant1-creative",
    "name": "SummarizationBot Creative",
    "description": "Variante creativa con llama3.1:8b e temperature=0.8 per output piu variati e creativi.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
    "llangfuseUrl": "http://localhost:3000",
    "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
    "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
    "llangfuseProjectName": "mosaic-test-project",
    "deployment": {
      "mode": "DOCKER",
      "endpoint": "ghcr.io/mosaico/llama31-8b:latest"
    }
  }'
```

### 1.3 Agent Deterministic (mistral:7b, temperature=0.2)
  "id": "b8acccbd-e1f0-4653-8ea5-a381dcab89bb",
```json
{
  "id": "agent-variant2-deterministic",
  "name": "SummarizationBot Deterministic",
  "description": "Variante deterministica con mistral:7b e temperature=0.2 per output precisi e consistenti.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
  "llangfuseUrl": "http://localhost:3000",
  "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
  "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
  "llangfuseProjectName": "mosaic-test-project",
  "deployment": {
    "mode": "DOCKER",
    "endpoint": "ghcr.io/mosaico/mistral-7b:latest"
  }
}
```

```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "id": "agent-variant2-deterministic",
    "name": "SummarizationBot Deterministic",
    "description": "Variante deterministica con mistral:7b e temperature=0.2 per output precisi e consistenti.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generare descrizioni concise e accurate per repository GitHub basandosi sui README files",
    "llangfuseUrl": "http://localhost:3000",
    "llangfusePublicKey": "pk-lf-ac9f73f8-88b5-4dd8-bea8-2f45ecd186ae",
    "llangfuseSecretKey": "sk-lf-23527443-2406-42cb-b8a2-d745bdad1f08",
    "llangfuseProjectName": "mosaic-test-project",
    "deployment": {
      "mode": "DOCKER",
      "endpoint": "ghcr.io/mosaico/mistral-7b:latest"
    }
  }'
```

### Verifica Agent Creati

```bash
# Lista tutti gli agent
curl http://localhost:8080/api/agents

# Verifica singolo agent
curl http://localhost:8080/api/agents/agent-baseline
curl http://localhost:8080/api/agents/agent-variant1-creative
curl http://localhost:8080/api/agents/agent-variant2-deterministic
```

---

## Step 2: Creare il Benchmark

### API Endpoint
```
POST /api/benchmarks
```

### Request Body
```json
{
  "id": "bench-summarization-001",
  "metadata": "{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}",
  "datasetRef": "ause_train",
  "taskDef": "Genera una descrizione concisa (3-6 frasi) per un repository GitHub partendo dal README file",
  "features": "accuracy,fluency,conciseness,relevance",
  "protocolVersion": "1.0",
  "evaluates": [
    { "id": "agent-baseline" },
    { "id": "agent-variant1-creative" },
    { "id": "agent-variant2-deterministic" }
  ]
}
```

### curl Command
```bash
curl -X POST http://localhost:8080/api/benchmarks \
  -H "Content-Type: application/json" \
  -d '{
    "id": "bench-summarization-001",
    "metadata": "{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}",
    "datasetRef": "ause_train",
    "taskDef": "Genera una descrizione concisa (3-6 frasi) per un repository GitHub partendo dal README file",
    "features": "accuracy,fluency,conciseness,relevance",
    "protocolVersion": "1.0",
    "evaluates": [
      { "id": "agent-baseline" },
      { "id": "agent-variant1-creative" },
      { "id": "agent-variant2-deterministic" }
    ]
  }'
```

### Response Attesa (HTTP 200)
```json
{
  "id": "bench-summarization-001",
  "metadata": "{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}",
  "datasetRef": "ause_train",
  "taskDef": "Genera una descrizione concisa (3-6 frasi) per un repository GitHub partendo dal README file",
  "features": "accuracy,fluency,conciseness,relevance",
  "protocolVersion": "1.0",
  "evaluates": [...]
}
```

### Verifica
```bash
curl http://localhost:8080/api/benchmarks/bench-summarization-001
```

---

## Step 3: Eseguire i Benchmark Run (uno per ogni Agent)

### 3.1 Run per Agent Baseline

```
POST /api/benchmark-runs
```

```json
{
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-baseline",
  "triggeredBy": "api-demo",
  "langfuseRunName": "experiment_00 - 2026-01-22T10:44:46.144492Z"
}
```

```bash
curl -X POST http://localhost:8080/api/benchmark-runs \
  -H "Content-Type: application/json" \
  -d '{
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-baseline",
    "triggeredBy": "api-demo",
    "langfuseRunName": "experiment_00 - 2026-01-22T10:44:46.144492Z"
  }'
```

### 3.2 Run per Agent Creative

```json
{
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-variant1-creative",
  "triggeredBy": "api-demo",
  "langfuseRunName": "experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z"
}
```

```bash
curl -X POST http://localhost:8080/api/benchmark-runs \
  -H "Content-Type: application/json" \
  -d '{
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-variant1-creative",
    "triggeredBy": "api-demo",
    "langfuseRunName": "experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z"
  }'
```

### 3.3 Run per Agent Deterministic

```json
{
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-variant2-deterministic",
  "triggeredBy": "api-demo",
  "langfuseRunName": "experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z"
}
```

```bash
curl -X POST http://localhost:8080/api/benchmark-runs \
  -H "Content-Type: application/json" \
  -d '{
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-variant2-deterministic",
    "triggeredBy": "api-demo",
    "langfuseRunName": "experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z"
  }'
```

### Response Attesa (HTTP 202 Accepted)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-baseline",
  "status": "PENDING",
  "triggeredBy": "MANUAL",
  "triggeredByUser": "api-demo",
  "langfuseRunName": "experiment_00 - 2026-01-22T10:44:46.144492Z",
  "retryCount": 0
}
```

> **Nota**: Il run viene eseguito in modo asincrono. Lo status passerà da `PENDING` → `RUNNING` → `COMPLETED` (o `FAILED`).

---

## Step 4: Monitorare lo Stato del Run

### API Endpoint
```
GET /api/benchmark-runs/{runId}
```

### curl Command
```bash
# Sostituire {runId} con l'ID ricevuto nello step precedente
curl http://localhost:8080/api/benchmark-runs/{runId}
```

### Response - Run in corso (RUNNING)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "benchmarkId": "bench-github-summarization-demo",
  "agentId": "agent-demo-summarizer",
  "status": "RUNNING",
  "startedAt": "2026-01-23T10:30:00Z",
  ...
}
```

### Response - Run completato (COMPLETED)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "benchmarkId": "bench-github-summarization-demo",
  "agentId": "agent-demo-summarizer",
  "status": "COMPLETED",
  "startedAt": "2026-01-23T10:30:00Z",
  "completedAt": "2026-01-23T10:35:42Z",
  "tracesProcessed": 150,
  "metricsComputed": 8,
  "retryCount": 0
}
```

---

## Step 5: Ottenere lo Storico dei Run

### Lista run per benchmark
```bash
curl "http://localhost:8080/api/benchmark-runs?benchmarkId=bench-github-summarization-demo"
```

### Lista run per agent
```bash
curl "http://localhost:8080/api/benchmark-runs?agentId=agent-demo-summarizer"
```

### Storico run (ultimi 10)
```bash
curl "http://localhost:8080/api/benchmark-runs/bench-github-summarization-demo/agent-demo-summarizer/history?limit=10"
```

---

## Operazioni Aggiuntive

### Cancellare un Run in Corso
```bash
curl -X POST http://localhost:8080/api/benchmark-runs/{runId}/cancel
```

### Ripetere un Run Fallito
```bash
curl -X POST http://localhost:8080/api/benchmark-runs/{runId}/retry
```

### Response del Retry
```json
{
  "runId": "nuovo-run-id-generato"
}
```

---

## Demo con BenchmarkDemoPersistenceRunner

Per eseguire la demo completa automatica che mostra tutti i 7 step del processo di benchmarking:

### Avvio con profilo demo
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,demo-benchmark-persistence
```

### Cosa fa il Runner

Il `BenchmarkDemoPersistenceRunner` esegue automaticamente:

| Step | Descrizione |
|------|-------------|
| **1** | Crea e persiste 3 Agent (Baseline, Creative, Deterministic) |
| **2** | Crea e persiste il Benchmark con riferimento al dataset `ause_train` |
| **3** | Recupera le traces reali da Langfuse per ogni agent |
| **4** | Calcola le metriche aggregate (ROUGE, BLEU, ACCURACY, etc.) |
| **5** | Valuta i KPI tramite DSL (Overall Quality, Text Similarity, etc.) |
| **6** | Esegue e persiste i BenchmarkRun per ogni agent |
| **7** | Genera il report comparativo finale |

### Output Atteso

```
════════════════════════════════════════════════════════════════════════════════
  MOSAICO BENCHMARKING SYSTEM - DEMO CON PERSISTENZA
════════════════════════════════════════════════════════════════════════════════

Data esecuzione: 2026-01-23T...
Dataset: ause_train

════════════════════════════════════════════════════════════════════════════════
  STEP 1: CREAZIONE & PERSISTENZA AGENT
════════════════════════════════════════════════════════════════════════════════

Agent persistiti: 3
  - SummarizationBot Baseline [agent-baseline]
  - SummarizationBot Creative [agent-variant1-creative]
  - SummarizationBot Deterministic [agent-variant2-deterministic]

...

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    RISULTATI BENCHMARK - CONFRONTO AGENTI
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Metrica/KPI                         | Baseline        | Creative        | Deterministic
──────────────────────────────────────────────────────────────────────────────────────────

METRICHE:
ROUGE                               | 0.3842          | 0.3521          | 0.4102
ROUGE1_F                            | 0.4012          | 0.3689          | 0.4287
...

KPI:
Overall Quality                     | 0.4215          | 0.3987          | 0.4532
Text Similarity                     | 0.3756          | 0.3412          | 0.3987
...

ANALISI COMPARATIVA:
  Migliore Overall Quality: Deterministic (mistral:7b) con score 0.4532
```

---

## Configurazione Agenti - Riepilogo

| Agent ID | Nome | Modello | Temperature | Caratteristiche |
|----------|------|---------|-------------|-----------------|
| `agent-baseline` | SummarizationBot Baseline | llama3.2:3b | 0.0 | Output deterministici e riproducibili |
| `agent-variant1-creative` | SummarizationBot Creative | llama3.1:8b | 0.8 | Output variati e creativi |
| `agent-variant2-deterministic` | SummarizationBot Deterministic | mistral:7b | 0.2 | Bilanciamento tra consistenza e qualità |

---

## Run Name Langfuse per la Demo

Questi sono i run name configurati per recuperare le traces da Langfuse:

| Agent ID | Langfuse Run Name |
|----------|-------------------|
| `agent-baseline` | `experiment_00 - 2026-01-22T10:44:46.144492Z` |
| `agent-variant1-creative` | `experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z` |
| `agent-variant2-deterministic` | `experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z` |

---

## KPI e Metriche Calcolate

Il sistema calcola le seguenti metriche per ogni agent:

### Metriche Base (da Langfuse)
- `ROUGE` / `ROUGE1_F` / `ROUGEL_F` - Similarità con gold standard
- `BLEU` - Qualità della generazione
- `ACCURACY` - Accuratezza generale
- `COSINE_PRED_GOLD` - Similarità coseno predizione vs gold
- `COSINE_PRED_SOURCE` - Similarità coseno predizione vs source
- `LEN_RATIO` - Rapporto lunghezza output/input

### KPI Calcolati (via DSL)
```
Overall Quality:    WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)
Text Similarity:    AVERAGE(ROUGE, BLEU)
Min Performance:    MIN(ROUGE, BLEU, ACCURACY)
Quality Threshold:  THRESHOLD(ROUGE, 0.3)
```

---

## Troubleshooting

### Errore: Agent non trovato
```json
{"status": 404, "error": "Not Found"}
```
**Soluzione**: Verificare che l'agent sia stato creato correttamente allo Step 1.

### Errore: Benchmark non trovato
**Soluzione**: Verificare che il benchmark sia stato creato e che contenga l'agent nella lista `evaluates`.

### Run bloccato in PENDING
**Possibili cause**:
- Langfuse non raggiungibile
- Credenziali Langfuse non valide
- Dataset non trovato

**Verifica i log**:
```bash
tail -f logs/app.log | grep -i benchmark
```

### Metriche a zero
**Causa**: Le traces di Langfuse non contengono scores.
**Soluzione**: Il sistema userà metriche simulate per la demo.

---

## Riferimenti API Completi

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| `GET` | `/api/agents` | Lista tutti gli agent |
| `POST` | `/api/agents` | Crea un nuovo agent |
| `GET` | `/api/agents/{id}` | Dettaglio agent |
| `PUT` | `/api/agents/{id}` | Aggiorna agent |
| `DELETE` | `/api/agents/{id}` | Elimina agent |
| `GET` | `/api/benchmarks` | Lista tutti i benchmark |
| `POST` | `/api/benchmarks` | Crea un nuovo benchmark |
| `GET` | `/api/benchmarks/{id}` | Dettaglio benchmark |
| `PUT` | `/api/benchmarks/{id}` | Aggiorna benchmark |
| `DELETE` | `/api/benchmarks/{id}` | Elimina benchmark |
| `POST` | `/api/benchmark-runs` | Avvia un benchmark run |
| `GET` | `/api/benchmark-runs/{id}` | Stato del run |
| `GET` | `/api/benchmark-runs?benchmarkId=X` | Run per benchmark |
| `GET` | `/api/benchmark-runs?agentId=X` | Run per agent |
| `POST` | `/api/benchmark-runs/{id}/cancel` | Cancella run |
| `POST` | `/api/benchmark-runs/{id}/retry` | Riprova run fallito |
