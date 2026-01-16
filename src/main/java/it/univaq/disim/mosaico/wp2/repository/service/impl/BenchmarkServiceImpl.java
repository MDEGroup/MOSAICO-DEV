package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.service.MetricService;

/**
 * Implementation of BenchmarkService.
 * Uses the MetricProvider architecture for pluggable metric computation.
 */
@Service
public class BenchmarkServiceImpl implements BenchmarkService {
    @Autowired
    private LangfuseService langfuseService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private BenchmarkRepository benchmarkRepository;

    @Autowired
    private MetricProviderRegistry metricProviderRegistry;

    @Override
    public List<Benchmark> findAll() {
        return benchmarkRepository.findAll();
    }

    @Override
    public Optional<Benchmark> findById(String id) {
        return benchmarkRepository.findById(id);
    }

    @Override
    public Benchmark save(Benchmark benchmark) {
        return benchmarkRepository.save(benchmark);
    }

    @Override
    public void deleteById(String id) {
        benchmarkRepository.deleteById(id);
    }

    @Override
    public Benchmark findByDatasetRef(String datasetRef) {
        return benchmarkRepository.findByDatasetRef(datasetRef);
    }

    @Override
    public Benchmark findByProtocolVersion(String protocolVersion) {
        return benchmarkRepository.findByProtocolVersion(protocolVersion);
    }

    @Override
    public List<Benchmark> findByEvaluates_Id(String agentId) {
        return benchmarkRepository.findByEvaluates_Id(agentId);
    }

    /**
     * Computes all available metrics for a benchmark using all registered providers.
     * This method retrieves Langfuse traces for the benchmark run and applies all
     * registered MetricProviders to compute metrics.
     *
     * @param benchmark The benchmark to evaluate
     * @param agent The agent being evaluated
     * @return List of computed metrics
     */
    @Override
    public List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent) {
        if (benchmark == null || agent == null) {
            throw new IllegalArgumentException("Benchmark and agent cannot be null");
        }

        List<Metric> metrics = new ArrayList<>();

        try {
            // Retrieve traces from Langfuse for this benchmark run
            List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(
                agent,
                benchmark.getDatasetRef(),
                benchmark.getRunName()
            );

            // Apply all registered metric providers to each trace
            for (TraceWithFullDetails trace : traces) {
                String expectedText = extractExpectedText(trace);
                String generatedText = extractGeneratedText(trace);

                // Compute metrics using all available providers
                for (MetricProvider<?> provider : metricProviderRegistry.getAllProviders()) {
                    Metric metric = provider.compute(agent, expectedText, generatedText, trace);
                    if (metric != null) {
                        metrics.add(metric);
                    }
                }
            }
        } catch (Exception e) {
            // Log the error but continue - partial results are better than none
            System.err.println("Error computing benchmark metrics: " + e.getMessage());
        }

        return metrics;
    }

    /**
     * Extracts the expected/reference text from a Langfuse trace.
     */
    private String extractExpectedText(TraceWithFullDetails trace) {
        if (trace.getAdditionalProperties() != null &&
            trace.getAdditionalProperties().containsKey("expected")) {
            return trace.getAdditionalProperties().get("expected").toString();
        }
        return "";
    }

    /**
     * Extracts the generated text (output) from a Langfuse trace.
     */
    private String extractGeneratedText(TraceWithFullDetails trace) {
        return trace.getOutput().orElse("").toString();
    }







    /**
     * Computes benchmark metrics using specific metric providers.
     * This method is useful when you want to compute only specific metrics
     * rather than all available ones.
     *
     * @deprecated Use computeBenchmarkMetrics(Benchmark, Agent) instead
     * @param benchmark The benchmark to evaluate
     * @param agent The agent being evaluated
     * @param metricProviders List of specific providers to use
     * @return List of computed metrics
     */
    @Deprecated
    @Override
    public List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent, List<MetricProvider> metricProviders) {
        if (benchmark == null || agent == null) {
            throw new IllegalArgumentException("Benchmark and agent cannot be null");
        }

        if (metricProviders == null || metricProviders.isEmpty()) {
            // If no specific providers given, use all registered providers
            return computeBenchmarkMetrics(benchmark, agent);
        }

        List<Metric> metrics = new ArrayList<>();

        try {
            List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(
                agent,
                benchmark.getDatasetRef(),
                benchmark.getRunName()
            );

            for (TraceWithFullDetails trace : traces) {
                String expectedText = extractExpectedText(trace);
                String generatedText = extractGeneratedText(trace);

                for (MetricProvider<?> provider : metricProviders) {
                    Metric metric = provider.compute(agent, expectedText, generatedText, trace);
                    if (metric != null) {
                        metrics.add(metric);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error computing benchmark metrics: " + e.getMessage());
        }

        return metrics;
    }

    /**
     * Computes KPIs for a benchmark by first computing all metrics, then applying
     * the KPI formulas defined in the benchmark's measures.
     *
     * @param benchmark The benchmark containing KPI specifications
     * @param agent The agent being evaluated
     * @return The computed PerformanceKPI with evaluated values
     */
    @Override
    public PerformanceKPI computeKPIs(Benchmark benchmark, Agent agent) {
        if (benchmark == null || agent == null) {
            throw new IllegalArgumentException("Benchmark and agent cannot be null");
        }

        if (benchmark.getMeasures() == null || benchmark.getMeasures().isEmpty()) {
            return null; // No KPIs defined for this benchmark
        }

        // Compute all metrics first
        List<Metric> computedMetrics = computeBenchmarkMetrics(benchmark, agent);

        // For each PerformanceKPI in the benchmark, evaluate its formula
        // Note: Typically a benchmark would have one main KPI, but we return the first one
        for (PerformanceKPI kpi : benchmark.getMeasures()) {
            if (kpi.getIncludes() == null || kpi.getIncludes().isEmpty()) {
                continue; // Skip KPIs with no defined metrics
            }

            if (kpi.getSpecification() == null || kpi.getSpecification().getFormula() == null) {
                continue; // Skip KPIs with no formula
            }

            // Build a map of metric values by their key type
            Map<Class<? extends MetricKey>, Double> metricValues = buildMetricValueMap(
                kpi.getIncludes(),
                computedMetrics
            );

            try {
                // Evaluate the KPI formula
                double kpiValue = kpi.getSpecification().getFormula().evaluate(metricValues);

                // Store the result (you might want to persist this)
                // For now, we just return the KPI with the computed value in the description
                kpi.setDescription(kpi.getDescription() + " [Computed: " + kpiValue + "]");

                return kpi;
            } catch (Exception e) {
                System.err.println("Error computing KPI: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Builds a map of metric values keyed by their MetricKey class.
     * This map is used for KPI formula evaluation.
     */
    private Map<Class<? extends MetricKey>, Double> buildMetricValueMap(
            List<MetricKey> requiredKeys,
            List<Metric> computedMetrics) {

        Map<Class<? extends MetricKey>, Double> metricValues = new HashMap<>();

        for (MetricKey key : requiredKeys) {
            // Find the corresponding computed metric
            // Note: This is a simplified approach. In a real system, you'd want
            // more sophisticated matching (by type, name, etc.)
            for (Metric metric : computedMetrics) {
                if (metric.getFloatValue().isPresent()) {
                    metricValues.put(key.getClass(), metric.getFloatValue().get().doubleValue());
                    break;
                }
            }
        }

        return metricValues;
    }
}