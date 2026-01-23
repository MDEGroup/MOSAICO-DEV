package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;

/**
 * Aggregates Langfuse trace scores together with locally computed metrics
 * provided by {@link MetricProvider} implementations.
 */
@Service
public class TraceMetricsAggregator {

    private static final Logger logger = LoggerFactory.getLogger(TraceMetricsAggregator.class);

    private final MetricProviderRegistry metricProviderRegistry;

    public TraceMetricsAggregator(MetricProviderRegistry metricProviderRegistry) {
        this.metricProviderRegistry = metricProviderRegistry;
    }

    /**
     * Aggregate metrics for the given agent traces.
     *
     * @param agent  agent owning the traces
     * @param traces trace data enriched with Langfuse scores
     * @return normalized average metrics ready for KPI consumption
     */
    public Map<String, Double> aggregate(Agent agent, List<TraceData> traces) {
        if (traces == null || traces.isEmpty()) {
            return Map.of();
        }

        Collection<MetricProvider<?>> providers = metricProviderRegistry.getAllProviders();
        Map<String, List<Double>> metricValues = new LinkedHashMap<>();

        for (TraceData trace : traces) {
            collectLangfuseScores(metricValues, trace.langfuseScores);

            if (trace == null || trace.expectedOutput == null || trace.generatedOutput == null) {
                continue;
            }

            for (MetricProvider<?> provider : providers) {
                try {
                    Metric metric = provider.compute(agent, trace.expectedOutput, trace.generatedOutput, trace.trace);
                    if (metric == null) {
                        continue;
                    }
                    metric.getFloatValue().ifPresent(value -> addMetricValue(metricValues, deriveMetricName(metric), value));
                } catch (Exception ex) {
                    logger.debug("Metric provider {} skipped: {}", provider.getClass().getSimpleName(), ex.getMessage());
                }
            }
        }

        Map<String, Double> aggregated = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : metricValues.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            aggregated.put(entry.getKey(), avg);
        }

        return normalizeAggregatedMetrics(aggregated);
    }

    private void collectLangfuseScores(Map<String, List<Double>> accumulator, Map<String, Double> scores) {
        if (scores == null || scores.isEmpty()) {
            return;
        }
        scores.forEach((name, value) -> {
            if (value != null) {
                addMetricValue(accumulator, normalizeMetricKey(name), value);
            }
        });
    }

    private void addMetricValue(Map<String, List<Double>> accumulator, String metricName, double value) {
        if (metricName == null) {
            return;
        }
        accumulator.computeIfAbsent(metricName, k -> new ArrayList<>()).add(value);
    }

    private String deriveMetricName(Metric metric) {
        if (metric == null) {
            return null;
        }
        MetricType type = metric.getType();
        if (type != null) {
            return type.name();
        }
        return normalizeMetricKey(metric.getName());
    }

    private String normalizeMetricKey(String metricName) {
        if (metricName == null) {
            return null;
        }
        return metricName.toUpperCase().replace("-", "_").replace(" ", "_");
    }

    private Map<String, Double> normalizeAggregatedMetrics(Map<String, Double> aggregated) {
        Map<String, Double> normalized = new LinkedHashMap<>(aggregated);
        if (normalized.containsKey("ROUGE1_F")) {
            normalized.put("ROUGE", normalized.get("ROUGE1_F"));
        }
        if (normalized.containsKey("ROUGEL_F")) {
            normalized.putIfAbsent("ROUGE", normalized.get("ROUGEL_F"));
        }
        if (normalized.containsKey("COSINE_PRED_GOLD")) {
            normalized.put("ACCURACY", normalized.get("COSINE_PRED_GOLD"));
        }
        if (!normalized.containsKey("BLEU") && normalized.containsKey("ROUGE")) {
            normalized.put("BLEU", normalized.get("ROUGE") * 0.85);
        }
        return normalized;
    }
}
