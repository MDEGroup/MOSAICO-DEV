package it.univaq.disim.mosaico.wp2.repository.service.impl;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;
import it.univaq.disim.mosaico.wp2.repository.data.*;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaDslService;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkResultRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.KPIHistoryRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricSnapshotRepository;
import it.univaq.disim.mosaico.wp2.repository.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Implementation of BenchmarkOrchestrator.
 * Coordinates the complete benchmark execution flow.
 */
@Service
@Transactional
public class BenchmarkOrchestratorImpl implements BenchmarkOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkOrchestratorImpl.class);
    private static final int MAX_RETRIES = 3;

    private final BenchmarkRunManager runManager;
    private final BenchmarkService benchmarkService;
    private final AgentService agentService;
    private final LangfuseService langfuseService;
    private final MetricProviderRegistry metricProviderRegistry;
    private final KPIFormulaDslService kpiFormulaDslService;
    private final AlertEvaluationService alertEvaluationService;
    private final BenchmarkResultRepository resultRepository;
    private final MetricSnapshotRepository metricSnapshotRepository;
    private final KPIHistoryRepository kpiHistoryRepository;

    public BenchmarkOrchestratorImpl(
            BenchmarkRunManager runManager,
            BenchmarkService benchmarkService,
            AgentService agentService,
            LangfuseService langfuseService,
            MetricProviderRegistry metricProviderRegistry,
            KPIFormulaDslService kpiFormulaDslService,
            AlertEvaluationService alertEvaluationService,
            BenchmarkResultRepository resultRepository,
            MetricSnapshotRepository metricSnapshotRepository,
            KPIHistoryRepository kpiHistoryRepository) {
        this.runManager = runManager;
        this.benchmarkService = benchmarkService;
        this.agentService = agentService;
        this.langfuseService = langfuseService;
        this.metricProviderRegistry = metricProviderRegistry;
        this.kpiFormulaDslService = kpiFormulaDslService;
        this.alertEvaluationService = alertEvaluationService;
        this.resultRepository = resultRepository;
        this.metricSnapshotRepository = metricSnapshotRepository;
        this.kpiHistoryRepository = kpiHistoryRepository;
    }

    @Override
    public BenchmarkRun executeBenchmarkRun(String runId) {
        logger.info("Starting benchmark execution for run: {}", runId);

        BenchmarkRun run = runManager.startRun(runId);

        try {
            // 1. Load benchmark and agent
            Benchmark benchmark = benchmarkService.findById(run.getBenchmarkId())
                .orElseThrow(() -> new IllegalStateException("Benchmark not found: " + run.getBenchmarkId()));
            Agent agent = agentService.findById(run.getAgentId())
                .orElseThrow(() -> new IllegalStateException("Agent not found: " + run.getAgentId()));

            // 2. Fetch traces from Langfuse
            String langfuseRunName = resolveLangfuseRunName(run, benchmark);
            logger.debug("Fetching traces for benchmark run {} using dataset={} runName={}",
                runId, benchmark.getDatasetRef(), langfuseRunName);
            List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(
                agent, benchmark.getDatasetRef(), langfuseRunName);

            if (traces.isEmpty()) {
                logger.warn("No traces found for benchmark run: {}", runId);
            }

            int tracesProcessed = 0;
            int metricsComputed = 0;

            // 3. Process each trace
            for (TraceWithFullDetails trace : traces) {
                BenchmarkResult result = processTrace(run, benchmark, agent, trace);
                resultRepository.save(result);
                tracesProcessed++;
                metricsComputed += result.getMetricSnapshots().size();
                runManager.updateProgress(runId, tracesProcessed, metricsComputed);
            }

            // 4. Compute and persist KPIs
            computeAndPersistKPIs(run, benchmark, agent);

            // 5. Evaluate alerts
            alertEvaluationService.evaluateAlertsForRun(runId);

            // 6. Complete the run
            return runManager.completeRun(runId, tracesProcessed, metricsComputed);

        } catch (Exception e) {
            logger.error("Benchmark execution failed for run: {}", runId, e);
            return runManager.failRun(runId, e.getMessage());
        }
    }

    @Override
    @Async
    public void executeBenchmarkRunAsync(String runId) {
        executeBenchmarkRun(runId);
    }

    @Override
    public void cancelBenchmarkRun(String runId) {
        logger.info("Cancelling benchmark run: {}", runId);
        runManager.cancelRun(runId);
    }

    @Override
    public String retryBenchmarkRun(String runId) {
        BenchmarkRun failedRun = runManager.findById(runId)
            .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));

        if (failedRun.getRetryCount() >= MAX_RETRIES) {
            throw new IllegalStateException("Max retries exceeded for run: " + runId);
        }

        BenchmarkRun newRun = runManager.createRun(
            failedRun.getBenchmarkId(),
            failedRun.getAgentId(),
            TriggerType.MANUAL,
            "retry",
            failedRun.getLangfuseRunName()
        );
        newRun.setRetryCount(failedRun.getRetryCount() + 1);

        logger.info("Created retry run {} for failed run {}", newRun.getId(), runId);
        return newRun.getId();
    }

    private BenchmarkResult processTrace(BenchmarkRun run, Benchmark benchmark, Agent agent, TraceWithFullDetails trace) {
        BenchmarkResult result = new BenchmarkResult(run, trace.getId());

        String expectedText = extractExpectedText(trace);
        String generatedText = extractGeneratedText(trace);
        result.setExpectedText(expectedText);
        result.setGeneratedText(generatedText);

        // Compute metrics for this trace
        for (MetricProvider<?> provider : metricProviderRegistry.getAllProviders()) {
            try {
                Metric metric = provider.compute(agent, expectedText, generatedText, trace);
                MetricSnapshot snapshot = MetricSnapshot.fromMetric(run.getId(), metric, trace.getId());
                result.addMetricSnapshot(snapshot);
            } catch (Exception e) {
                logger.warn("Failed to compute metric {} for trace {}: {}",
                    provider.getClass().getSimpleName(), trace.getId(), e.getMessage());
            }
        }

        return result;
    }

    private void computeAndPersistKPIs(BenchmarkRun run, Benchmark benchmark, Agent agent) {
        List<PerformanceKPI> kpis = benchmark.getMeasures();
        if (kpis == null || kpis.isEmpty()) {
            return;
        }

        // Get aggregated metrics for KPI computation
        Map<String, Double> aggregatedMetrics = aggregateMetricsForRun(run.getId());

        for (PerformanceKPI kpi : kpis) {
            try {
                KPIFormula formula = kpiFormulaDslService.buildFromSpecification(kpi.getSpecification());
                // Convert string keys to class keys if needed
                double kpiValue = evaluateKpiFormula(formula, aggregatedMetrics);

                KPIHistory history = new KPIHistory(
                    benchmark.getId(),
                    agent.getId(),
                    kpi.getDescription(),
                    kpiValue
                );
                history.setRunId(run.getId());
                history.setKpiId(kpi.getId());
                history.evaluateStatus();
                kpiHistoryRepository.save(history);

                logger.debug("Computed KPI {} = {} for run {}", kpi.getDescription(), kpiValue, run.getId());
            } catch (Exception e) {
                logger.warn("Failed to compute KPI {}: {}", kpi.getDescription(), e.getMessage());
            }
        }
    }

    private Map<String, Double> aggregateMetricsForRun(String runId) {
        List<MetricSnapshot> snapshots = metricSnapshotRepository.findByRunId(runId);
        return snapshots.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                MetricSnapshot::getMetricKey,
                java.util.stream.Collectors.averagingDouble(MetricSnapshot::getValue)
            ));
    }

    private String resolveLangfuseRunName(BenchmarkRun run, Benchmark benchmark) {
        String runName = run.getLangfuseRunName();
        if (runName == null || runName.isBlank()) {
            runName = benchmark.getRunName();
        }
        if (runName == null || runName.isBlank()) {
            throw new IllegalStateException("No Langfuse run name configured for benchmark " + benchmark.getId());
        }
        return runName;
    }

    @SuppressWarnings("unchecked")
    private double evaluateKpiFormula(KPIFormula formula, Map<String, Double> metricValues) {
        // Convert String keys to Class<MetricKey> for the formula
        Map<Class<? extends MetricKey>, Double> classKeyMap = new java.util.HashMap<>();
        for (Map.Entry<String, Double> entry : metricValues.entrySet()) {
            // Try to find matching MetricKey class
            String key = entry.getKey();
            if ("ROUGE".equalsIgnoreCase(key)) {
                classKeyMap.put(RougeMetric.class, entry.getValue());
            } else if ("BLEU".equalsIgnoreCase(key)) {
                classKeyMap.put(BlueMetric.class, entry.getValue());
            }
            // Add more mappings as needed
        }
        return formula.evaluate(classKeyMap);
    }

    private String extractExpectedText(TraceWithFullDetails trace) {
        Object expected = trace.getAdditionalProperties().get("expected");
        return expected != null ? expected.toString() : "";
    }

    private String extractGeneratedText(TraceWithFullDetails trace) {
        return trace.getOutput().orElse("").toString();
    }
}
