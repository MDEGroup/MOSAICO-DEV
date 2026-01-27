package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.F1ScoreMetric;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;

/**
 * MetricProvider for computing F1 Score.
 * F1 Score is the harmonic mean of Precision and Recall.
 * F1 = 2 * (Precision * Recall) / (Precision + Recall)
 */
@Service
public class F1ScoreMetricProvider implements MetricProvider<F1ScoreMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText, String generatedText, TraceData trace) {
        float f1Score = computeF1Score(referenceText, generatedText);
        Metric metric = new Metric();
        metric.setName("F1 Score");
        metric.setType(MetricType.F1_SCORE);
        metric.setFloatValue(f1Score);
        metric.setUnit("score");
        return metric;
    }

    /**
     * Computes F1 score as the harmonic mean of precision and recall.
     * F1 balances both precision and recall into a single metric.
     */
    private float computeF1Score(String referenceText, String generatedText) {
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

        // Intersection
        Set<String> intersection = new HashSet<>(generatedSet);
        intersection.retainAll(referenceSet);

        float precision = (float) intersection.size() / generatedSet.size();
        float recall = (float) intersection.size() / referenceSet.size();

        if (precision + recall == 0f) {
            return 0f;
        }

        // F1 = 2 * (precision * recall) / (precision + recall)
        return (2 * precision * recall) / (precision + recall);
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
    public Class<F1ScoreMetric> key() {
        return F1ScoreMetric.class;
    }
}
