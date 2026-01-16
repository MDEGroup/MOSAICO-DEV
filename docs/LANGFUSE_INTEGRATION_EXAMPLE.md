# Langfuse Integration with Metric Provider Architecture

## Your Langfuse Setup

Based on your Langfuse instance at `http://localhost:3000`:

- **Project ID**: `cmiop1ce90006kx07cgn1sh7l`
- **Dataset ID**: `cmiq2blfa0001lk07zja0etrc`
- **Run ID**: `20635f24-5320-4023-a643-33ee0609d418`
- **Langfuse URL**: `http://localhost:3000`
- **Public Key**: `pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015`
- **Secret Key**: `sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee`

---

## How the Integration Works

The metric provider architecture integrates with Langfuse through the `BenchmarkServiceImpl.computeBenchmarkMetrics()` method:

```
Langfuse Dataset Run
  ↓
  ↓ getRunBenchmarkTraces()
  ↓
List<TraceWithFullDetails>
  ↓
  ↓ For each trace
  ↓
Extract: expected text, generated text
  ↓
  ↓ Apply all MetricProviders
  ↓
Computed Metrics (ROUGE, BLEU, etc.)
  ↓
  ↓ Apply KPIFormula
  ↓
PerformanceKPI Result
```

---

## Step-by-Step Example

### Step 1: Create an Agent with Langfuse Credentials

```java
Agent agent = new Agent();
agent.setName("My Coding Agent");
agent.setLlangfuseUrl("http://localhost:3000");
agent.setLlangfusePublicKey("pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015");
agent.setLlangfuseSecretKey("sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee");
agent.setLlangfuseProjectName("my-project");

// Save to database
agentRepository.save(agent);
```

### Step 2: Create a Benchmark Linked to Your Dataset Run

```java
Benchmark benchmark = new Benchmark();
benchmark.setId(UUID.randomUUID().toString());

// Link to your Langfuse dataset
benchmark.setDatasetRef("cmiq2blfa0001lk07zja0etrc");  // Your dataset ID
benchmark.setRunName("20635f24-5320-4023-a643-33ee0609d418");  // Your run ID

// Add the agent to evaluate
benchmark.getEvaluates().add(agent);

// Optionally define KPI measures
PerformanceKPI qualityKPI = new PerformanceKPI();
qualityKPI.setDescription("Overall Quality Score");
qualityKPI.setIncludes(Arrays.asList(new BlueMetric(), new RougeMetric()));

// Define a weighted formula: 60% BLEU + 40% ROUGE
Map<Class<? extends MetricKey>, Double> weights = new HashMap<>();
weights.put(BlueMetric.class, 0.6);
weights.put(RougeMetric.class, 0.4);

KPISpecification spec = new KPISpecification();
spec.setFormulaType("WEIGHTED_SUM");
spec.setFormula(new WeightedSumFormula(weights));
qualityKPI.setSpecification(spec);

benchmark.setMeasures(Arrays.asList(qualityKPI));

// Save benchmark
benchmarkRepository.save(benchmark);
```

### Step 3: Compute Metrics from Your Dataset Run

```java
@Autowired
private BenchmarkService benchmarkService;

// This will:
// 1. Retrieve all traces from run "20635f24-5320-4023-a643-33ee0609d418"
// 2. For each trace, extract expected vs generated output
// 3. Apply ROUGE and BLEU metric providers
// 4. Return computed metrics
List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(benchmark, agent);

// Display results
for (Metric metric : metrics) {
    System.out.println(metric.getName() + ": " + metric.getFloatValue().orElse(0f));
}

// Output example:
// ROUGE Score: 0.85
// ROUGE Score: 0.78
// ROUGE Score: 0.92
// (one per trace in your dataset run)
```

### Step 4: Compute KPI

```java
// Compute the overall KPI by applying the formula
PerformanceKPI result = benchmarkService.computeKPIs(benchmark, agent);

System.out.println("Overall Quality Score: " + result.getDescription());
// Output: "Overall Quality Score [Computed: 0.84]"
```

---

## What Happens Behind the Scenes

### 1. Trace Retrieval (LangfuseService)

```java
// In BenchmarkServiceImpl.computeBenchmarkMetrics()
List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(
    agent,
    "cmiq2blfa0001lk07zja0etrc",  // your dataset
    "20635f24-5320-4023-a643-33ee0609d418"  // your run
);
```

This calls Langfuse API:
```
GET http://localhost:3000/api/public/datasets/{datasetName}/runs/{runName}
```

### 2. Data Extraction

For each trace, the system extracts:

```java
// Expected output from dataset item
String expectedText = trace.getAdditionalProperties().get("expected").toString();

// Generated output from trace
String generatedText = trace.getOutput().orElse("").toString();
```

### 3. Metric Computation

All registered providers are applied:

```java
for (MetricProvider<?> provider : metricProviderRegistry.getAllProviders()) {
    Metric metric = provider.compute(agent, expectedText, generatedText, trace);
    metrics.add(metric);
}
```

Currently registered:
- ✅ **RougeMetricProvider** - ROUGE-L score
- ✅ **BlueMetricProvider** - BLEU-like score

### 4. KPI Aggregation

```java
// Build metric value map
Map<Class<? extends MetricKey>, Double> metricValues = {
    BlueMetric.class -> 0.88,
    RougeMetric.class -> 0.82
};

// Apply formula (60% BLEU + 40% ROUGE)
double kpi = 0.88 * 0.6 + 0.82 * 0.4 = 0.528 + 0.328 = 0.856
```

---

## Complete REST API Example

### Create and Evaluate a Benchmark

```bash
# 1. Create benchmark via REST API
curl -X POST http://localhost:8080/api/benchmarks \
  -H "Content-Type: application/json" \
  -d '{
    "datasetRef": "cmiq2blfa0001lk07zja0etrc",
    "runName": "20635f24-5320-4023-a643-33ee0609d418",
    "evaluates": [
      {
        "id": "agent-123",
        "langfuseUrl": "http://localhost:3000",
        "langfusePublicKey": "pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015",
        "langfuseSecretKey": "sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee"
      }
    ]
  }'

# 2. Compute metrics
curl -X POST http://localhost:8080/api/benchmarks/{benchmarkId}/compute-metrics/{agentId}

# 3. Get results
curl http://localhost:8080/api/benchmarks/{benchmarkId}
```

---

## Adding Custom Metrics from Langfuse Scores

If you already have custom scores in Langfuse (e.g., "hallucination_score", "coherence"), you can create providers for them:

### Example: Hallucination Score Provider

```java
// 1. Create MetricKey
package it.univaq.disim.mosaico.wp2.repository.data;

public class HallucinationMetric implements MetricKey {
}
```

```java
// 2. Create Provider
package it.univaq.disim.mosaico.wp2.repository.service.impl;

import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.data.HallucinationMetric;
import com.langfuse.client.resources.commons.types.TraceWithFullDetails;

@Service
public class HallucinationMetricProvider implements MetricProvider<HallucinationMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText,
                         String generatedText, TraceWithFullDetails trace) {

        // Extract hallucination score from Langfuse trace scores
        float hallucinationScore = 0f;

        if (trace != null && trace.getScores() != null) {
            // Find "hallucination" score in trace
            for (var scoreId : trace.getScores()) {
                // Score lookup logic here
                // hallucinationScore = ...
            }
        }

        Metric metric = new Metric();
        metric.setName("Hallucination Score");
        metric.setType(MetricType.HALLUCINATION);
        metric.setFloatValue(hallucinationScore);
        metric.setUnit("score");
        return metric;
    }

    @Override
    public Class<HallucinationMetric> key() {
        return HallucinationMetric.class;
    }
}
```

**That's it!** The provider is automatically registered and will be applied to all benchmark evaluations.

---

## Viewing Results in Langfuse

After computing metrics, you can optionally push them back to Langfuse as scores:

```java
@Autowired
private LangfuseClient langfuseClient;

public void pushMetricsToLangfuse(List<Metric> metrics, String traceId) {
    for (Metric metric : metrics) {
        langfuseClient.scoreV2().create(
            CreateScoreRequest.builder()
                .traceId(traceId)
                .name(metric.getName())
                .value(metric.getFloatValue().orElse(0f).doubleValue())
                .build()
        );
    }
}
```

Then view in Langfuse UI:
```
http://localhost:3000/project/cmiop1ce90006kx07cgn1sh7l/traces/{traceId}
```

---

## Example: Full Evaluation Workflow

```java
@Service
public class AgentEvaluationService {

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private BenchmarkRepository benchmarkRepository;

    /**
     * Evaluates an agent using a Langfuse dataset run.
     */
    public Map<String, Object> evaluateAgent(String agentId, String datasetId, String runId) {

        // 1. Load agent
        Agent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new RuntimeException("Agent not found"));

        // 2. Create benchmark
        Benchmark benchmark = new Benchmark();
        benchmark.setDatasetRef(datasetId);
        benchmark.setRunName(runId);
        benchmark.getEvaluates().add(agent);

        // Define KPI
        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setDescription("Quality Score");
        kpi.setIncludes(Arrays.asList(new BlueMetric(), new RougeMetric()));

        KPISpecification spec = new KPISpecification();
        spec.setFormula(new AverageFormula());
        kpi.setSpecification(spec);

        benchmark.setMeasures(Arrays.asList(kpi));
        benchmarkRepository.save(benchmark);

        // 3. Compute metrics
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(benchmark, agent);

        // 4. Compute KPI
        PerformanceKPI result = benchmarkService.computeKPIs(benchmark, agent);

        // 5. Return results
        Map<String, Object> results = new HashMap<>();
        results.put("agent", agent.getName());
        results.put("dataset", datasetId);
        results.put("run", runId);
        results.put("metrics", metrics);
        results.put("kpi", result);
        results.put("metricCount", metrics.size());

        return results;
    }
}
```

### Usage

```java
Map<String, Object> results = evaluationService.evaluateAgent(
    "my-agent-id",
    "cmiq2blfa0001lk07zja0etrc",
    "20635f24-5320-4023-a643-33ee0609d418"
);

System.out.println("Evaluated " + results.get("metricCount") + " traces");
System.out.println("KPI: " + results.get("kpi"));
```

---

## Troubleshooting

### Issue: "No traces found"

**Check:**
1. Agent Langfuse credentials are correct
2. Dataset ID and Run ID are valid
3. Langfuse instance is running on `http://localhost:3000`
4. Run actually has traces (check in Langfuse UI)

```bash
# Verify dataset run exists
curl http://localhost:3000/api/public/datasets/cmiq2blfa0001lk07zja0etrc/runs/20635f24-5320-4023-a643-33ee0609d418 \
  -H "Authorization: Bearer pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015:sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee"
```

### Issue: "Missing expected output"

The metric computation expects dataset items to have `expectedOutput` field:

```java
// In LangfuseService.getRunBenchmarkTraces()
DatasetItem datasetItem = client.datasetItems().get(item.getDatasetItemId());
trace.getAdditionalProperties().put("expected", datasetItem.getExpectedOutput());
```

**Ensure your dataset items have `expectedOutput` set in Langfuse.**

### Issue: "Metrics are all 0.0"

Check if your traces have actual output:

```java
String generatedText = trace.getOutput().orElse("").toString();
// If empty, metrics will be 0
```

---

## Next Steps

1. **Run your first evaluation**:
   ```java
   List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(benchmark, agent);
   ```

2. **Visualize results** in your application dashboard

3. **Add custom metrics** specific to your domain (hallucination, factuality, etc.)

4. **Define KPIs** that combine metrics according to your business logic

5. **Integrate with CI/CD** to automatically evaluate agents on code changes

---

## Summary

The metric provider architecture seamlessly integrates with your Langfuse setup:

✅ **Automatic trace retrieval** from dataset runs
✅ **Pluggable metric computation** (ROUGE, BLEU, custom)
✅ **Flexible KPI formulas** (average, weighted, threshold)
✅ **Type-safe provider registry**
✅ **Spring auto-discovery** of new providers
✅ **Production-ready** error handling

**Your dataset run is ready to be evaluated!** 🚀

Use the examples above to start computing metrics on:
```
http://localhost:3000/project/cmiop1ce90006kx07cgn1sh7l/datasets/cmiq2blfa0001lk07zja0etrc/runs/20635f24-5320-4023-a643-33ee0609d418
```
