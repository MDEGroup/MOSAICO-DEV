package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricRepository;
import it.univaq.disim.mosaico.wp2.repository.service.MetricService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the Metric entity in the repository.
 * It provides methods to perform CRUD operations on the Metric entity.
 * It uses the MetricRepository to interact with the persistence store.
 */
@Service
public class MetricServiceImpl implements MetricService {

    /*
     * The metricRepository is used to perform CRUD operations on the Metric entity. It is autowired by Spring.
    * The MetricRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final MetricRepository metricRepository;

    /*
     * This constructor is used to inject the MetricRepository dependency into the MetricServiceImpl class.
     * The @Autowired annotation is used to indicate that the MetricRepository bean should be injected.
     */
    public MetricServiceImpl(@Autowired MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @Override
    /*
     * Retrieves all metrics from the repository.
     * @return a list of all metrics
     */
    public List<Metric> findAll() {
        return metricRepository.findAll();
    }

    @Override
    /*
     * Retrieves a metric by its id from the repository.
     * @param id - the id of the metric to be retrieved
     * @return the metric with the given id
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric findById(String id) {
        return metricRepository.findById(id)
                .orElseThrow(() -> new MetricNotFoundException(id));
    }

    @Override
    /*
     * Saves the given metric to the persistence store. If the metric already exists, it is updated.
     * @param metric - the metric to be saved
     * @return the saved metric
     */
    public Metric save(Metric metric) {
        return metricRepository.save(metric);
    }

    @Override
    /*
     * Deletes the metric with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the metric to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        metricRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given metric in the persistence store. If the metric does not exist, it throws a MetricNotFoundException.
     * @param metric - the metric to be updated
     * @return the updated metric
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric update(Metric metric) {
        if (!metricRepository.existsById(metric.getId())) {
            throw new MetricNotFoundException(metric.getId());
        }
        // Update the metric in the repository
        return metricRepository.save(metric); 
    }
    @Override
    public Metric computeBleuScoreMetric(String referenceText, String generatedText) {
        float bleuScore = computeBleuScore(referenceText, generatedText);
        Metric bleuMetric = new Metric();
        bleuMetric.setName("BLEU Score");
        bleuMetric.setType(MetricType.BLEU);
        bleuMetric.setFloatValue(bleuScore);
        bleuMetric.setUnit("score");

        return metricRepository.save(bleuMetric);
    }

  

    @Override
    public Metric computeRougeScoreMetric(String referenceText, String generatedText) {
        float rougeScore = computeRougeLScore(referenceText, generatedText);
        Metric rougeMetric = new Metric();
        rougeMetric.setName("ROUGE Score");
        rougeMetric.setType(MetricType.ROUGE);
        rougeMetric.setFloatValue(rougeScore);
        rougeMetric.setUnit("score");

        return metricRepository.save(rougeMetric);
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

    private float computeBleuScore(String referenceText, String generatedText) {
        List<String> referenceTokens = tokenize(referenceText);
        List<String> generatedTokens = tokenize(generatedText);
        if (referenceTokens.isEmpty() || generatedTokens.isEmpty()) {
            return 0f;
        }

        int maxOrder = Math.min(4, generatedTokens.size());
        double sumLogPrecision = 0d;
        int validOrders = 0;
        for (int order = 1; order <= maxOrder; order++) {
            int possible = generatedTokens.size() - order + 1;
            if (possible <= 0) {
                continue;
            }
            int overlap = countOverlapNgrams(referenceTokens, generatedTokens, order);
            if (overlap == 0) {
                return 0f;
            }
            double precision = (double) overlap / possible;
            sumLogPrecision += Math.log(precision);
            validOrders++;
        }

        if (validOrders == 0) {
            return 0f;
        }

        double geometricMean = Math.exp(sumLogPrecision / validOrders);
        double referenceLength = referenceTokens.size();
        double candidateLength = generatedTokens.size();
        double brevityPenalty = candidateLength > referenceLength
                ? 1d
                : Math.exp(1d - (referenceLength / candidateLength));

        return (float) (brevityPenalty * geometricMean);
    }

    private int countOverlapNgrams(List<String> referenceTokens, List<String> generatedTokens, int order) {
        Map<String, Integer> referenceCounts = buildNgramCounts(referenceTokens, order);
        Map<String, Integer> candidateCounts = buildNgramCounts(generatedTokens, order);
        int overlap = 0;
        for (Map.Entry<String, Integer> entry : candidateCounts.entrySet()) {
            int matched = Math.min(entry.getValue(), referenceCounts.getOrDefault(entry.getKey(), 0));
            overlap += matched;
        }
        return overlap;
    }

    private Map<String, Integer> buildNgramCounts(List<String> tokens, int order) {
        Map<String, Integer> counts = new HashMap<>();
        if (tokens.size() < order) {
            return counts;
        }
        for (int i = 0; i <= tokens.size() - order; i++) {
            String ngram = String.join(" ", tokens.subList(i, i + order));
            counts.merge(ngram, 1, Integer::sum);
        }
        return counts;
    }
}
