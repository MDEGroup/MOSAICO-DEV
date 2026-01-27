package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.RecallMetric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;

/**
 * MetricProvider for computing Recall score.
 * Recall measures the proportion of reference tokens that were captured
 * in the generated text.
 * Recall = |generated ∩ reference| / |reference|
 */
@Service
public class RecallMetricProvider implements MetricProvider<RecallMetric> {

    @Override
    public Metric compute(Agent agent, String referenceText, String generatedText, TraceData trace) {
        float recallScore = computeRecall(referenceText, generatedText);
        Metric metric = new Metric();
        metric.setName("Recall Score");
        metric.setType(MetricType.RECALL);
        metric.setFloatValue(recallScore);
        metric.setUnit("score");
        return metric;
    }

    /**
     * Computes recall as the ratio of captured reference tokens to total reference tokens.
     * High recall means most reference tokens were included in the generated text.
     */
    private float computeRecall(String referenceText, String generatedText) {
        List<String> referenceTokens = tokenize(referenceText);
        List<String> generatedTokens = tokenize(generatedText);

        if (referenceTokens.isEmpty()) {
            return generatedTokens.isEmpty() ? 1.0f : 0f;
        }

        Set<String> referenceSet = new HashSet<>(referenceTokens);
        Set<String> generatedSet = new HashSet<>(generatedTokens);

        // Count how many reference tokens appear in generated
        Set<String> intersection = new HashSet<>(referenceSet);
        intersection.retainAll(generatedSet);

        return (float) intersection.size() / referenceSet.size();
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
    public Class<RecallMetric> key() {
        return RecallMetric.class;
    }
}
