# Metric Provider Architecture

## Overview

The MOSAICO Repository implements a **pluggable metric provider architecture** that allows for flexible computation and evaluation of agent performance metrics. This architecture follows the **Strategy Pattern** and **Registry Pattern** to enable easy extensibility and maintainability.

## Architecture Components

### 1. Core Interfaces and Classes

#### `MetricKey` Interface
```java
public interface MetricKey { }
```
- Marker interface for type-safe metric identification
- Each metric type implements this interface as a unique key
- Examples: `BlueMetric`, `RougeMetric`

#### `MetricProvider<K extends MetricKey>` Interface
```java
public interface MetricProvider<K extends MetricKey> {
    Metric compute(Agent agent, String referenceText, String generatedText, TraceWithFullDetails trace);
    Class<K> key();
}
```
- Generic interface for all metric computation strategies
- Type parameter `K` ensures type safety
- Implementations compute specific metrics (ROUGE, BLEU, etc.)

#### `MetricProviderRegistry` Service
```java
@Service
public class MetricProviderRegistry {
    public <K extends MetricKey> MetricProvider<K> providerFor(Class<K> key);
    public Collection<MetricProvider<?>> getAllProviders();
    public Set<Class<? extends MetricKey>> getRegisteredKeys();
    public boolean hasProvider(Class<? extends MetricKey> key);
}
```
- Central registry for all metric providers
- Auto-discovers providers via Spring dependency injection
- Provides type-safe lookup by MetricKey class

### 2. KPI Formula System

#### `KPIFormula` Interface
```java
@FunctionalInterface
public interface KPIFormula {
    double evaluate(Map<Class<? extends MetricKey>, Double> metricValues);
}
```
- Functional interface for KPI computation
- Takes computed metrics and produces a single KPI value

#### Formula Implementations

**AverageFormula**
- Computes the average of all metric values
- Use case: Overall performance assessment

**WeightedSumFormula**
- Computes weighted sum: `w1*m1 + w2*m2 + ...`
- Use case: Prioritizing certain metrics (e.g., 60% accuracy, 40% speed)

**ThresholdFormula**
- Binary check: returns 1.0 if threshold met, 0.0 otherwise
- Use case: Pass/fail criteria, quality gates

### 3. Data Model

```
Benchmark
  ├─ List<PerformanceKPI> measures
  │    ├─ List<MetricKey> includes
  │    └─ KPISpecification specification
  │         ├─ String formulaType
  │         ├─ String formulaConfig
  │         └─ KPIFormula formula (transient)
  └─ List<Agent> evaluates
```

## How It Works

### Metric Computation Flow

```
1. BenchmarkService.computeBenchmarkMetrics(benchmark, agent)
   │
   ├─> Retrieve Langfuse traces for benchmark run
   │
   ├─> For each trace:
   │   ├─> Extract expected and generated text
   │   └─> For each registered MetricProvider:
   │       └─> Compute metric value
   │
   └─> Return List<Metric>
```

### KPI Evaluation Flow

```
1. BenchmarkService.computeKPIs(benchmark, agent)
   │
   ├─> Compute all metrics using computeBenchmarkMetrics()
   │
   ├─> For each PerformanceKPI in benchmark.measures:
   │   ├─> Build map of metric values by MetricKey
   │   ├─> Get KPIFormula from specification
   │   └─> Evaluate formula with metric values
   │
   └─> Return PerformanceKPI with computed value
```

## Adding New Metrics

### Step 1: Create MetricKey

```java
package it.univaq.disim.mosaico.wp2.repository.data;

public class AccuracyMetric implements MetricKey {
    // Empty marker class
}
```

### Step 2: Implement MetricProvider

```java
package it.univaq.disim.mosaico.wp2.repository.service.impl;

import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.data.AccuracyMetric;

@Service
public class AccuracyMetricProvider implements MetricProvider<AccuracyMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText,
                         String generatedText, TraceWithFullDetails trace) {
        // Your computation logic here
        float accuracy = computeAccuracy(referenceText, generatedText);

        Metric metric = new Metric();
        metric.setName("Accuracy");
        metric.setType(MetricType.ACCURACY);
        metric.setFloatValue(accuracy);
        metric.setUnit("percentage");
        return metric;
    }

    @Override
    public Class<AccuracyMetric> key() {
        return AccuracyMetric.class;
    }

    private float computeAccuracy(String reference, String generated) {
        // Implementation details...
        return 0.95f;
    }
}
```

### Step 3: Use the Metric

The provider is automatically registered at startup via Spring's dependency injection. No configuration needed!

```java
// Automatic usage via BenchmarkService
List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(benchmark, agent);

// Manual usage via Registry
MetricProvider<AccuracyMetric> provider = registry.providerFor(AccuracyMetric.class);
Metric metric = provider.compute(agent, reference, generated, trace);
```

## Adding Custom KPI Formulas

### Example: F1 Score Formula

```java
public class F1ScoreFormula implements KPIFormula {
    @Override
    public double evaluate(Map<Class<? extends MetricKey>, Double> metricValues) {
        double precision = metricValues.get(PrecisionMetric.class);
        double recall = metricValues.get(RecallMetric.class);

        if (precision + recall == 0) return 0.0;

        return 2 * (precision * recall) / (precision + recall);
    }
}
```

### Usage

```java
PerformanceKPI kpi = new PerformanceKPI();
kpi.setDescription("F1 Score");
kpi.setIncludes(Arrays.asList(new PrecisionMetric(), new RecallMetric()));

KPISpecification spec = new KPISpecification();
spec.setFormulaType("F1_SCORE");
spec.setFormula(new F1ScoreFormula());
kpi.setSpecification(spec);

benchmark.setMeasures(Arrays.asList(kpi));
```

## Best Practices

### 1. Keep MetricProviders Stateless
```java
@Service
public class MyMetricProvider implements MetricProvider<MyMetric> {
    // ✅ Good: Injected dependencies
    @Autowired
    private SomeExternalService service;

    // ❌ Bad: Mutable state
    // private int counter = 0;
}
```

### 2. Use Type-Safe Lookups
```java
// ✅ Good: Type-safe
MetricProvider<BlueMetric> provider = registry.providerFor(BlueMetric.class);

// ❌ Bad: Casting
MetricProvider provider = registry.getAllProviders().iterator().next();
```

### 3. Handle Missing Metrics Gracefully
```java
Map<Class<? extends MetricKey>, Double> values = buildMetricValueMap(...);

if (!values.containsKey(RequiredMetric.class)) {
    throw new IllegalArgumentException("Missing required metric: RequiredMetric");
}
```

### 4. Document Metric Assumptions
```java
/**
 * Computes ROUGE-L score using longest common subsequence.
 *
 * Assumptions:
 * - Text is tokenized by whitespace
 * - Case-insensitive matching
 * - Punctuation is removed
 *
 * @param referenceText Ground truth text
 * @param generatedText Agent-generated text
 * @return F1 score combining precision and recall
 */
@Override
public Metric compute(...) { ... }
```

## Exception Handling

### Custom Exceptions

- `MetricProviderNotFoundException`: Thrown when a provider for a MetricKey is not registered
- `MetricComputationException`: Thrown when metric computation fails
- `KPIEvaluationException`: Thrown when KPI formula evaluation fails

### Example
```java
try {
    MetricProvider<CustomMetric> provider = registry.providerFor(CustomMetric.class);
} catch (MetricProviderNotFoundException e) {
    System.err.println("No provider for: " + e.getMetricKey().getSimpleName());
}
```

## Testing

### Unit Test Example

```java
@Test
void testMetricProvider() {
    BlueMetricProvider provider = new BlueMetricProvider();

    Agent agent = new Agent();
    String reference = "The quick brown fox jumps over the lazy dog";
    String generated = "The quick brown fox jumped over a lazy dog";

    Metric metric = provider.compute(agent, reference, generated, null);

    assertNotNull(metric);
    assertEquals("ROUGE Score", metric.getName());
    assertTrue(metric.getFloatValue().isPresent());
    assertTrue(metric.getFloatValue().get() > 0.7f);
}
```

## Integration with Langfuse

The metric provider architecture integrates with Langfuse for observability:

```java
List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(
    agent,
    benchmark.getDatasetRef(),
    benchmark.getRunName()
);

for (TraceWithFullDetails trace : traces) {
    String expected = trace.getAdditionalProperties().get("expected").toString();
    String generated = trace.getOutput().orElse("").toString();

    // Compute metrics using the trace data
    for (MetricProvider<?> provider : registry.getAllProviders()) {
        Metric metric = provider.compute(agent, expected, generated, trace);
        // Store or aggregate metrics...
    }
}
```

## Architecture Benefits

### ✅ Extensibility
Add new metrics without modifying existing code

### ✅ Type Safety
Generic types prevent runtime casting errors

### ✅ Separation of Concerns
- Metrics compute individual values
- Formulas combine metrics into KPIs
- Registry manages provider lifecycle

### ✅ Testability
Each component can be tested in isolation

### ✅ Spring Integration
Automatic discovery via dependency injection

### ✅ Flexibility
Support for any metric type and any KPI formula

## Migration Guide

### Before (Old Approach)
```java
// Heavy, monolithic MetricService
public class MetricService {
    public List<Metric> computeAllMetrics(Agent agent, Trace trace) {
        // 200+ lines of coupled metric computation
        float rouge = computeRouge(...);
        float bleu = computeBleu(...);
        // ... many more metrics
    }
}
```

### After (New Approach)
```java
// Clean, pluggable architecture
@Service
public class BenchmarkServiceImpl {
    @Autowired
    private MetricProviderRegistry registry;

    public List<Metric> computeBenchmarkMetrics(Benchmark b, Agent a) {
        List<Metric> metrics = new ArrayList<>();
        for (MetricProvider<?> provider : registry.getAllProviders()) {
            metrics.add(provider.compute(a, ref, gen, trace));
        }
        return metrics;
    }
}
```

## Future Enhancements

1. **Async Metric Computation**: Use `CompletableFuture` for parallel computation
2. **Metric Caching**: Cache computed metrics to avoid recomputation
3. **Metric Versioning**: Support multiple versions of the same metric
4. **Metric Dependencies**: Allow metrics to depend on other metrics
5. **Dynamic Formula Loading**: Load KPI formulas from configuration files

## Conclusion

The metric provider architecture provides a robust, extensible foundation for agent evaluation in the MOSAICO project. By following the patterns and practices outlined in this document, developers can easily add new metrics and KPIs while maintaining code quality and maintainability.

For questions or contributions, please refer to the project's contribution guidelines.
