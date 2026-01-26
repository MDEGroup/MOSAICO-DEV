# MOSAICO - Interactive Benchmarking Demo via Swagger API

This guide walks you through step by step in creating and executing a benchmark using the REST APIs exposed by the MOSAICO application.

## Prerequisites

1. **Start the application** with the `dev` profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Swagger UI** available at:
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Langfuse** must be reachable (default: `http://localhost:3000`)

4. **PostgreSQL Database** active and configured

---

## Flow Overview

```
┌─────────────┐    ┌─────────────┐    ┌─────────────────┐    ┌─────────────┐
│  1. Create  │───▶│  2. Create  │───▶│  3. Execute     │───▶│ 4. View     │
│   3 Agents  │    │  Benchmark  │    │  Benchmark Run  │    │  Results    │
└─────────────┘    └─────────────┘    └─────────────────┘    └─────────────┘
```

---

## Step 1: Create the 3 Agents

We create 3 summarization agents with different configurations to compare performance.

### 1.1 Baseline Agent (llama3.2:3b, temperature=0)
f714f29f-a531-42a2-b4aa-f79c00d4870b"
```
POST /api/agents
```

```json
{
  "id": "agent-baseline",
  "name": "SummarizationBot Baseline",
  "description": "Baseline agent for GitHub description generation. Uses llama3.2:3b with temperature=0 for deterministic output.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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
    "description": "Baseline agent for GitHub description generation. Uses llama3.2:3b with temperature=0 for deterministic output.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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

### 1.2 Creative Agent (llama3.1:8b, temperature=0.8)
  "id": "437ac1c8-72b1-43ef-b1a7-3523f060740a",
```json
{
  "id": "agent-variant1-creative",
  "name": "SummarizationBot Creative",
  "description": "Creative variant with llama3.1:8b and temperature=0.8 for more varied and creative output.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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
    "description": "Creative variant with llama3.1:8b and temperature=0.8 for more varied and creative output.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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

### 1.3 Deterministic Agent (mistral:7b, temperature=0.2)
  "id": "b8acccbd-e1f0-4653-8ea5-a381dcab89bb",
```json
{
  "id": "agent-variant2-deterministic",
  "name": "SummarizationBot Deterministic",
  "description": "Deterministic variant with mistral:7b and temperature=0.2 for precise and consistent output.",
  "version": "1.0",
  "role": "Summarization Agent",
  "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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
    "description": "Deterministic variant with mistral:7b and temperature=0.2 for precise and consistent output.",
    "version": "1.0",
    "role": "Summarization Agent",
    "objective": "Generate concise and accurate descriptions for GitHub repositories based on README files",
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

### Verify Created Agents

```bash
# List all agents
curl http://localhost:8080/api/agents

# Verify individual agent
curl http://localhost:8080/api/agents/agent-baseline
curl http://localhost:8080/api/agents/agent-variant1-creative
curl http://localhost:8080/api/agents/agent-variant2-deterministic
```

---

## Step 2: Create the Benchmark

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
  "taskDef": "Generate a concise description (3-6 sentences) for a GitHub repository from the README file",
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
    "taskDef": "Generate a concise description (3-6 sentences) for a GitHub repository from the README file",
    "features": "accuracy,fluency,conciseness,relevance",
    "protocolVersion": "1.0",
    "evaluates": [
      { "id": "agent-baseline" },
      { "id": "agent-variant1-creative" },
      { "id": "agent-variant2-deterministic" }
    ]
  }'
```

### Expected Response (HTTP 200)
```json
{
  "id": "bench-summarization-001",
  "metadata": "{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}",
  "datasetRef": "ause_train",
  "taskDef": "Generate a concise description (3-6 sentences) for a GitHub repository from the README file",
  "features": "accuracy,fluency,conciseness,relevance",
  "protocolVersion": "1.0",
  "evaluates": [...]
}
```

### Verify
```bash
curl http://localhost:8080/api/benchmarks/bench-summarization-001
```

---

## Step 3: Execute Benchmark Runs (one for each Agent)

### 3.1 Run for Baseline Agent

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

### 3.2 Run for Creative Agent

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

### 3.3 Run for Deterministic Agent

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

### Expected Response (HTTP 202 Accepted)
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

> **Note**: The run is executed asynchronously. Status will transition from `PENDING` → `RUNNING` → `COMPLETED` (or `FAILED`).

---

## Step 4: Monitor Run Status

### API Endpoint
```
GET /api/benchmark-runs/{runId}
```

### curl Command
```bash
# Replace {runId} with the ID received in the previous step
curl http://localhost:8080/api/benchmark-runs/{runId}
```

### Response - Run in progress (RUNNING)
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

### Response - Run completed (COMPLETED)
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

## Step 5: Get Run History

### List runs by benchmark
```bash
curl "http://localhost:8080/api/benchmark-runs?benchmarkId=bench-github-summarization-demo"
```

### List runs by agent
```bash
curl "http://localhost:8080/api/benchmark-runs?agentId=agent-demo-summarizer"
```

### Run history (last 10)
```bash
curl "http://localhost:8080/api/benchmark-runs/bench-github-summarization-demo/agent-demo-summarizer/history?limit=10"
```

---

## Additional Operations

### Cancel a Running Run
```bash
curl -X POST http://localhost:8080/api/benchmark-runs/{runId}/cancel
```

### Retry a Failed Run
```bash
curl -X POST http://localhost:8080/api/benchmark-runs/{runId}/retry
```

### Retry Response
```json
{
  "runId": "new-generated-run-id"
}
```

---

## Demo with BenchmarkDemoPersistenceRunner

To run the complete automatic demo that shows all 7 steps of the benchmarking process:

### Start with demo profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,demo-benchmark-persistence
```

### What the Runner Does

The `BenchmarkDemoPersistenceRunner` automatically executes:

| Step | Description |
|------|-------------|
| **1** | Creates and persists 3 Agents (Baseline, Creative, Deterministic) |
| **2** | Creates and persists the Benchmark with reference to the `ause_train` dataset |
| **3** | Retrieves real traces from Langfuse for each agent |
| **4** | Calculates aggregated metrics (ROUGE, BLEU, ACCURACY, etc.) |
| **5** | Evaluates KPIs via DSL (Overall Quality, Text Similarity, etc.) |
| **6** | Executes and persists BenchmarkRuns for each agent |
| **7** | Generates the final comparative report |

### Expected Output

```
════════════════════════════════════════════════════════════════════════════════
  MOSAICO BENCHMARKING SYSTEM - DEMO WITH PERSISTENCE
════════════════════════════════════════════════════════════════════════════════

Execution date: 2026-01-23T...
Dataset: ause_train

════════════════════════════════════════════════════════════════════════════════
  STEP 1: AGENT CREATION & PERSISTENCE
════════════════════════════════════════════════════════════════════════════════

Persisted agents: 3
  - SummarizationBot Baseline [agent-baseline]
  - SummarizationBot Creative [agent-variant1-creative]
  - SummarizationBot Deterministic [agent-variant2-deterministic]

...

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    BENCHMARK RESULTS - AGENT COMPARISON
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Metric/KPI                          | Baseline        | Creative        | Deterministic
──────────────────────────────────────────────────────────────────────────────────────────

METRICS:
ROUGE                               | 0.3842          | 0.3521          | 0.4102
ROUGE1_F                            | 0.4012          | 0.3689          | 0.4287
...

KPI:
Overall Quality                     | 0.4215          | 0.3987          | 0.4532
Text Similarity                     | 0.3756          | 0.3412          | 0.3987
...

COMPARATIVE ANALYSIS:
  Best Overall Quality: Deterministic (mistral:7b) with score 0.4532
```

---

## Agent Configuration - Summary

| Agent ID | Name | Model | Temperature | Characteristics |
|----------|------|-------|-------------|-----------------|
| `agent-baseline` | SummarizationBot Baseline | llama3.2:3b | 0.0 | Deterministic and reproducible output |
| `agent-variant1-creative` | SummarizationBot Creative | llama3.1:8b | 0.8 | Varied and creative output |
| `agent-variant2-deterministic` | SummarizationBot Deterministic | mistral:7b | 0.2 | Balance between consistency and quality |

---

## Langfuse Run Names for the Demo

These are the run names configured to retrieve traces from Langfuse:

| Agent ID | Langfuse Run Name |
|----------|-------------------|
| `agent-baseline` | `experiment_00 - 2026-01-22T10:44:46.144492Z` |
| `agent-variant1-creative` | `experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z` |
| `agent-variant2-deterministic` | `experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z` |

---

## Calculated KPIs and Metrics

The system calculates the following metrics for each agent:

### Base Metrics (from Langfuse)
- `ROUGE` / `ROUGE1_F` / `ROUGEL_F` - Similarity with gold standard
- `BLEU` - Generation quality
- `ACCURACY` - General accuracy
- `COSINE_PRED_GOLD` - Cosine similarity prediction vs gold
- `COSINE_PRED_SOURCE` - Cosine similarity prediction vs source
- `LEN_RATIO` - Output/input length ratio

### Calculated KPIs (via DSL)
```
Overall Quality:    WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)
Text Similarity:    AVERAGE(ROUGE, BLEU)
Min Performance:    MIN(ROUGE, BLEU, ACCURACY)
Quality Threshold:  THRESHOLD(ROUGE, 0.3)
```

---

## Troubleshooting

### Error: Agent not found
```json
{"status": 404, "error": "Not Found"}
```
**Solution**: Verify that the agent was created correctly in Step 1.

### Error: Benchmark not found
**Solution**: Verify that the benchmark was created and contains the agent in the `evaluates` list.

### Run stuck in PENDING
**Possible causes**:
- Langfuse not reachable
- Invalid Langfuse credentials
- Dataset not found

**Check the logs**:
```bash
tail -f logs/app.log | grep -i benchmark
```

### Metrics at zero
**Cause**: Langfuse traces don't contain scores.
**Solution**: The system will use simulated metrics for the demo.

---

## Complete API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/agents` | List all agents |
| `POST` | `/api/agents` | Create a new agent |
| `GET` | `/api/agents/{id}` | Agent details |
| `PUT` | `/api/agents/{id}` | Update agent |
| `DELETE` | `/api/agents/{id}` | Delete agent |
| `GET` | `/api/benchmarks` | List all benchmarks |
| `POST` | `/api/benchmarks` | Create a new benchmark |
| `GET` | `/api/benchmarks/{id}` | Benchmark details |
| `PUT` | `/api/benchmarks/{id}` | Update benchmark |
| `DELETE` | `/api/benchmarks/{id}` | Delete benchmark |
| `POST` | `/api/benchmark-runs` | Start a benchmark run |
| `GET` | `/api/benchmark-runs/{id}` | Run status |
| `GET` | `/api/benchmark-runs?benchmarkId=X` | Runs by benchmark |
| `GET` | `/api/benchmark-runs?agentId=X` | Runs by agent |
| `POST` | `/api/benchmark-runs/{id}/cancel` | Cancel run |
| `POST` | `/api/benchmark-runs/{id}/retry` | Retry failed run |
| `POST` | `/api/schedules` | Create schedule |
| `GET` | `/api/schedules/{id}` | Schedule details |
| `GET` | `/api/schedules` | List schedules |
| `PUT` | `/api/schedules/{id}` | Update schedule |
| `DELETE` | `/api/schedules/{id}` | Delete schedule |
| `POST` | `/api/schedules/{id}/enable` | Enable schedule |
| `POST` | `/api/schedules/{id}/disable` | Disable schedule |
| `GET` | `/api/schedules/due` | Schedules due for execution |

---

## Step 6: Create a Scheduled BenchmarkRun

In addition to manual BenchmarkRun execution, you can configure automatic executions through the **Scheduler**. This allows running benchmarks periodically (e.g., every day, every week) without manual intervention.

### How the Scheduler Works

1. Create a `ScheduleConfig` that defines:
   - Which benchmark to execute
   - On which agent
   - At what frequency (cron expression)
   - Retry options and auto-disable on failure

2. The system automatically executes a `BenchmarkRun` at the defined times

3. The created `BenchmarkRun` will have:
   - `triggeredBy: SCHEDULED` (instead of `MANUAL`)
   - `scheduleConfigId` populated with the schedule ID

### 6.1 Create a Schedule for the Baseline Agent (every day at 06:00)

```
POST /api/schedules
```

```json
{
  "name": "Daily Baseline Benchmark",
  "description": "Executes the benchmark on the baseline agent every day at 06:00 UTC",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-baseline",
  "cronExpression": "0 0 6 * * ?",//"0 */2 * * * ?"
  "timezone": "UTC",
  "enabled": true,
  "langfuseRunName": "scheduled_baseline_daily",
  "maxConsecutiveFailures": 3,
  "autoDisableOnFailure": true
}
```

```bash
curl -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Baseline Benchmark",
    "description": "Executes the benchmark on the baseline agent every day at 06:00 UTC",
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-baseline",
    "cronExpression": "0 0 6 * * ?",
    "timezone": "UTC",
    "enabled": true,
    "langfuseRunName": "scheduled_baseline_daily",
    "maxConsecutiveFailures": 3,
    "autoDisableOnFailure": true
  }'
```

### Expected Response (HTTP 200)
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Daily Baseline Benchmark",
  "description": "Executes the benchmark on the baseline agent every day at 06:00 UTC",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-baseline",
  "cronExpression": "0 0 6 * * ?",
  "timezone": "UTC",
  "enabled": true,
  "nextRunAt": "2026-01-24T06:00:00Z",
  "runCount": 0,
  "failureCount": 0,
  "consecutiveFailures": 0,
  "maxConsecutiveFailures": 3,
  "autoDisableOnFailure": true,
  "createdAt": "2026-01-23T12:00:00Z"
}
```

### 6.2 Schedule for the Creative Agent (every Monday at 08:00)

```json
{
  "name": "Weekly Creative Benchmark",
  "description": "Executes the benchmark on the creative agent every Monday at 08:00",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-variant1-creative",
  "cronExpression": "0 0 8 ? * MON",
  "timezone": "Europe/Rome",
  "enabled": true,
  "langfuseRunName": "scheduled_creative_weekly",
  "maxConsecutiveFailures": 5,
  "autoDisableOnFailure": true
}
```

```bash
curl -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekly Creative Benchmark",
    "description": "Executes the benchmark on the creative agent every Monday at 08:00",
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-variant1-creative",
    "cronExpression": "0 0 8 ? * MON",
    "timezone": "Europe/Rome",
    "enabled": true,
    "langfuseRunName": "scheduled_creative_weekly",
    "maxConsecutiveFailures": 5,
    "autoDisableOnFailure": true
  }'
```

### 6.3 Schedule for the Deterministic Agent (every 6 hours)

```json
{
  "name": "Frequent Deterministic Benchmark",
  "description": "Executes the benchmark on the deterministic agent every 6 hours for continuous monitoring",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-variant2-deterministic",
  "cronExpression": "0 0 */6 * * ?",
  "timezone": "UTC",
  "enabled": true,
  "langfuseRunName": "scheduled_deterministic_6h",
  "maxConsecutiveFailures": 2,
  "autoDisableOnFailure": false
}
```

```bash
curl -X POST http://localhost:8080/api/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Frequent Deterministic Benchmark",
    "description": "Executes the benchmark on the deterministic agent every 6 hours for continuous monitoring",
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-variant2-deterministic",
    "cronExpression": "0 0 */6 * * ?",
    "timezone": "UTC",
    "enabled": true,
    "langfuseRunName": "scheduled_deterministic_6h",
    "maxConsecutiveFailures": 2,
    "autoDisableOnFailure": false
  }'
```

---

## Schedule Management

### Verify Active Schedules

```bash
# List all enabled schedules
curl "http://localhost:8080/api/schedules?enabled=true"

# Schedules for a specific benchmark
curl "http://localhost:8080/api/schedules?benchmarkId=bench-summarization-001"

# Schedule details
curl http://localhost:8080/api/schedules/{scheduleId}
```

### View Schedules Ready for Execution

```bash
# Schedules that need to be executed (nextRunAt <= now)
curl http://localhost:8080/api/schedules/due
```

### Enable/Disable a Schedule

```bash
# Temporarily disable a schedule
curl -X POST http://localhost:8080/api/schedules/{scheduleId}/disable

# Re-enable the schedule
curl -X POST http://localhost:8080/api/schedules/{scheduleId}/enable
```

### Update a Schedule

```bash
# Change execution frequency
curl -X PUT http://localhost:8080/api/schedules/{scheduleId} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Daily Baseline Benchmark",
    "benchmarkId": "bench-summarization-001",
    "agentId": "agent-baseline",
    "cronExpression": "0 0 7 * * ?",
    "timezone": "Europe/Rome",
    "enabled": true
  }'
```

### Delete a Schedule

```bash
curl -X DELETE http://localhost:8080/api/schedules/{scheduleId}
```

---

## Cron Expression Reference

Cron expressions use the standard 6-field format:

```
┌───────────── seconds (0-59)
│ ┌───────────── minutes (0-59)
│ │ ┌───────────── hours (0-23)
│ │ │ ┌───────────── day of month (1-31)
│ │ │ │ ┌───────────── month (1-12)
│ │ │ │ │ ┌───────────── day of week (0-7, 0 or 7 = Sunday)
│ │ │ │ │ │
* * * * * *
```

### Common Examples

| Cron Expression | Description |
|-----------------|-------------|
| `0 0 6 * * ?` | Every day at 06:00 |
| `0 0 */6 * * ?` | Every 6 hours |
| `0 0 8 ? * MON` | Every Monday at 08:00 |
| `0 0 0 1 * ?` | First of every month at midnight |
| `0 30 9 ? * MON-FRI` | Every weekday at 09:30 |
| `0 0 */2 * * ?` | Every 2 hours |
| `0 0 12 ? * SUN` | Every Sunday at noon |

---

## Monitor Scheduled Runs

When a schedule is executed, it creates a `BenchmarkRun` with `triggeredBy: SCHEDULED`:

```bash
# Filter runs to see only scheduled ones
curl "http://localhost:8080/api/benchmark-runs?benchmarkId=bench-summarization-001" | jq '.[] | select(.triggeredBy == "SCHEDULED")'
```

### Scheduled Run Response
```json
{
  "id": "run-abc123",
  "benchmarkId": "bench-summarization-001",
  "agentId": "agent-baseline",
  "status": "COMPLETED",
  "triggeredBy": "SCHEDULED",
  "scheduleConfigId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "langfuseRunName": "scheduled_baseline_daily",
  "startedAt": "2026-01-24T06:00:00Z",
  "completedAt": "2026-01-24T06:05:32Z",
  "tracesProcessed": 150,
  "metricsComputed": 8
}
```

### Schedule Statistics

The schedule tracks executions:

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Daily Baseline Benchmark",
  "runCount": 15,
  "failureCount": 1,
  "consecutiveFailures": 0,
  "lastRunAt": "2026-01-24T06:00:00Z",
  "lastRunId": "run-abc123",
  "lastRunStatus": "COMPLETED",
  "nextRunAt": "2026-01-25T06:00:00Z"
}
