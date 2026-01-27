package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.AccuracyMetric;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;

/**
 * MetricProvider for computing Accuracy score.
 * Accuracy measures the proportion of correct predictions (exact token matches)
 * relative to the total number of tokens.
 */
@Service
public class AccuracyMetricProvider implements MetricProvider<AccuracyMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText, String generatedText, TraceData trace) {
        float accuracyScore = computeAccuracy(referenceText, generatedText);
        Metric metric = new Metric();
        metric.setName("Accuracy Score");
        metric.setType(MetricType.ACCURACY);
        metric.setFloatValue(accuracyScore);
        metric.setUnit("score");
        return metric;
    }

    /**
     * Computes accuracy as the ratio of matching tokens to total unique tokens.
     * This is a token-level accuracy that measures how well the generated text
     * captures the vocabulary of the reference text.
     */
    private float computeAccuracy(String referenceText, String generatedText) {
        List<String> referenceTokens = tokenize(referenceText);
        List<String> generatedTokens = tokenize(generatedText);

        if (referenceTokens.isEmpty() && generatedTokens.isEmpty()) {
            return 1.0f; // Both empty = perfect match
        }
        if (referenceTokens.isEmpty() || generatedTokens.isEmpty()) {
            return 0f;
        }

        Set<String> referenceSet = new HashSet<>(referenceTokens);
        Set<String> generatedSet = new HashSet<>(generatedTokens);

        // Count matching tokens
        Set<String> intersection = new HashSet<>(referenceSet);
        intersection.retainAll(generatedSet);

        // Union of both sets
        Set<String> union = new HashSet<>(referenceSet);
        union.addAll(generatedSet);

        return (float) intersection.size() / union.size();
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
    public Class<AccuracyMetric> key() {
        return AccuracyMetric.class;
    }
}
