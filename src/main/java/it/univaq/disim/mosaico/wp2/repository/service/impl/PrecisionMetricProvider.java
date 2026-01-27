package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.PrecisionMetric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;

/**
 * MetricProvider for computing Precision score.
 * Precision measures the proportion of generated tokens that are relevant
 * (i.e., appear in the reference text).
 * Precision = |generated ∩ reference| / |generated|
 */
@Service
public class PrecisionMetricProvider implements MetricProvider<PrecisionMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText, String generatedText, TraceData trace) {
        float precisionScore = computePrecision(referenceText, generatedText);
        Metric metric = new Metric();
        metric.setName("Precision Score");
        metric.setType(MetricType.PRECISION);
        metric.setFloatValue(precisionScore);
        metric.setUnit("score");
        return metric;
    }

    /**
     * Computes precision as the ratio of correct generated tokens to total generated tokens.
     * High precision means most generated tokens are relevant.
     */
    private float computePrecision(String referenceText, String generatedText) {
        List<String> referenceTokens = tokenize(referenceText);
        List<String> generatedTokens = tokenize(generatedText);

        if (generatedTokens.isEmpty()) {
            return referenceTokens.isEmpty() ? 1.0f : 0f;
        }

        Set<String> referenceSet = new HashSet<>(referenceTokens);
        Set<String> generatedSet = new HashSet<>(generatedTokens);

        // Count how many generated tokens appear in reference
        Set<String> intersection = new HashSet<>(generatedSet);
        intersection.retainAll(referenceSet);

        return (float) intersection.size() / generatedSet.size();
    }

    private List<String> tokenize(String text) {
        if (text == null) {
            return List.of();
        }
        return Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(token -> token.replaceAll("[^a-z0-9]", ""))
                .filter(token -> !token.isBlank())
                .toList();
    }

    @Override
    public Class<PrecisionMetric> key() {
        return PrecisionMetric.class;
    }
}
