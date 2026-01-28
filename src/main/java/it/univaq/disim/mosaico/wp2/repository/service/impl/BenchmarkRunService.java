package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkResult;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.MetricSnapshot;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;

/**
 * Service responsible for orchestrating benchmark run lifecycle operations.
 */
@Service
public class BenchmarkRunService {

    private static final int DEFAULT_TEXT_MAX_LENGTH = 100;

    /**
     * Creates a new {@link BenchmarkRun} for the provided benchmark and agent.
     */
    public BenchmarkRun createRun(Benchmark benchmark, Agent agent, String langfuseRunName) {
        BenchmarkRun run = new BenchmarkRun(benchmark.getId(), agent.getId(), TriggerType.MANUAL);
        run.setId("run-" + UUID.randomUUID().toString().substring(0, 8));
        run.setLangfuseRunName(langfuseRunName);
        return run;
    }

    /**
     * Moves the run to RUNNING state, capturing the start timestamp.
     */
    public void startRun(BenchmarkRun run) {
        if (run != null) {
            run.start();
        }
    }

    /**
     * Builds {@link BenchmarkResult} entries for every trace that participated in the run.
     */
    public List<BenchmarkResult> buildBenchmarkResults(BenchmarkRun run,
                                                       List<TraceData> traces,
                                                       Map<String, Double> kpiValues,
                                                       int maxTextLength) {
        List<BenchmarkResult> results = new ArrayList<>();
        if (traces == null || traces.isEmpty()) {
            return results;
        }

        int clamp = maxTextLength > 0 ? maxTextLength : DEFAULT_TEXT_MAX_LENGTH;
        for (TraceData trace : traces) {
            BenchmarkResult result = new BenchmarkResult(run, trace.traceId);
            result.setExpectedText(truncate(trace.expectedOutput, clamp));
            result.setGeneratedText(truncate(trace.generatedOutput, clamp));
            result.setKpiValues(kpiValues);
            results.add(result);
        }
        return results;
    }

    /**
     * Completes the run and stores processing statistics.
     */
    public void completeRun(BenchmarkRun run, int tracesProcessed, int metricsComputed) {
        if (run == null) {
            return;
        }
        run.setTracesProcessed(tracesProcessed);
        run.setMetricsComputed(metricsComputed);
        run.complete();
    }

    /**
     * Retrieves a metric value, defaulting to 0 if missing.
     */
    public double getMetricValue(Map<String, Double> aggregatedMetrics, String metricName) {
        if (aggregatedMetrics == null || metricName == null) {
            return 0.0;
        }
        return aggregatedMetrics.getOrDefault(metricName, 0.0);
    }

    /**
     * Evaluates a simple threshold alert condition.
     */
    public boolean isAlertTriggered(double metricValue, double threshold) {
        return metricValue < threshold;
    }

    /**
     * Convenience overload that clamps text using the service default.
     */
    public List<BenchmarkResult> buildBenchmarkResults(BenchmarkRun run,
                                                       List<TraceData> traces,
                                                       Map<String, Double> kpiValues) {
        return buildBenchmarkResults(run, traces, kpiValues, DEFAULT_TEXT_MAX_LENGTH);
    }

    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
