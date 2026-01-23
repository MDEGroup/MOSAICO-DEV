package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import org.springframework.stereotype.Service;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.BlueMetric;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
@Service
public class BlueMetricProvider implements MetricProvider<BlueMetric> {
    @Override
    public Metric compute(Agent agent, String referenceText, String generatedText, TraceWithFullDetails trace) {
        float rougeScore = computeRougeLScore(referenceText, generatedText);
        Metric rougeMetric = new Metric();
        rougeMetric.setName("BLEU Score");
        rougeMetric.setType(MetricType.BLEU);
        rougeMetric.setFloatValue(rougeScore);
        rougeMetric.setUnit("score");
        return rougeMetric;
    }

    private float computeRougeLScore(String referenceText, String generatedText) {
        List<String> referenceTokens = tokenize(referenceText);
        List<String> generatedTokens = tokenize(generatedText);
        if (referenceTokens.isEmpty() || generatedTokens.isEmpty()) {
            return 0f;
        }
        int lcsLength = longestCommonSubsequence(referenceTokens, generatedTokens);
        float precision = (float) lcsLength / generatedTokens.size();
        float recall = (float) lcsLength / referenceTokens.size();
        if (precision + recall == 0f) {
            return 0f;
        }
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

    private int longestCommonSubsequence(List<String> referenceTokens, List<String> generatedTokens) {
        int[][] dp = new int[referenceTokens.size() + 1][generatedTokens.size() + 1];
        for (int i = 1; i <= referenceTokens.size(); i++) {
            for (int j = 1; j <= generatedTokens.size(); j++) {
                if (Objects.equals(referenceTokens.get(i - 1), generatedTokens.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[referenceTokens.size()][generatedTokens.size()];
    }

    
    @Override
    public Class<BlueMetric> key() {
        return BlueMetric.class;
    }
}
