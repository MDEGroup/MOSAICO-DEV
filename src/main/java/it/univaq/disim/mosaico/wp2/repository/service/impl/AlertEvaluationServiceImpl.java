package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkResult;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.KPIHistory;
import it.univaq.disim.mosaico.wp2.repository.data.enums.AlertCondition;
import it.univaq.disim.mosaico.wp2.repository.repository.AlertConfigRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.KPIHistoryRepository;
import it.univaq.disim.mosaico.wp2.repository.service.AlertEvaluationService;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;
import it.univaq.disim.mosaico.wp2.repository.service.NotificationDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AlertEvaluationServiceImpl implements AlertEvaluationService {

    private static final Logger logger = LoggerFactory.getLogger(AlertEvaluationServiceImpl.class);

    private final AlertConfigRepository alertConfigRepository;
    private final KPIHistoryRepository kpiHistoryRepository;
    private final BenchmarkRunRepository benchmarkRunRepository;
    private final NotificationDispatcher notificationDispatcher;
    private final BenchmarkService benchmarkService;

    public AlertEvaluationServiceImpl(
            AlertConfigRepository alertConfigRepository,
            KPIHistoryRepository kpiHistoryRepository,
            BenchmarkRunRepository benchmarkRunRepository,
            NotificationDispatcher notificationDispatcher,
            BenchmarkService benchmarkService) {
        this.alertConfigRepository = alertConfigRepository;
        this.kpiHistoryRepository = kpiHistoryRepository;
        this.benchmarkRunRepository = benchmarkRunRepository;
        this.notificationDispatcher = notificationDispatcher;
        this.benchmarkService = benchmarkService;
    }

    @Override
    public void evaluateAlertsForRun(String runId) {
        logger.info("Evaluating alerts for run: {}", runId);

        BenchmarkRun run = benchmarkRunRepository.findById(runId).orElse(null);
        if (run == null) {
            logger.warn("Run not found: {}", runId);
            return;
        }

        // Get KPI history for this run
        List<KPIHistory> kpiHistories = kpiHistoryRepository
            .findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc(run.getBenchmarkId(), run.getAgentId());

        // Get active alerts for this benchmark
        List<AlertConfig> alerts = alertConfigRepository.findByBenchmarkIdAndEnabled(run.getBenchmarkId(), true);

        for (KPIHistory kpiHistory : kpiHistories) {
            if (!runId.equals(kpiHistory.getRunId())) {
                continue; // Only evaluate KPIs from this run
            }

            for (AlertConfig alert : alerts) {
                if (alert.getKpiName().equals(kpiHistory.getKpiName())) {
                    evaluateAlert(alert, kpiHistory);
                }
            }
        }
    }

    @Override
    public List<AlertConfig> evaluateKpiValue(String benchmarkId, String kpiName, double value) {
        List<AlertConfig> triggeredAlerts = new ArrayList<>();
        List<AlertConfig> alerts = alertConfigRepository.findActiveAlertsForKpi(benchmarkId, kpiName);

        for (AlertConfig alert : alerts) {
            if (checkAlertCondition(alert, value)) {
                triggeredAlerts.add(alert);
                triggerAlert(alert, kpiName, value);
            }
        }

        return triggeredAlerts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertConfig> findAlertsByBenchmarkId(String benchmarkId) {
        return alertConfigRepository.findByBenchmarkId(benchmarkId);
    }

    @Override
    public AlertConfig createAlert(AlertConfig config) {
        logger.info("Creating alert: {}", config.getName());
        return alertConfigRepository.save(config);
    }

    @Override
    public AlertConfig updateAlert(AlertConfig config) {
        logger.info("Updating alert: {}", config.getId());
        return alertConfigRepository.save(config);
    }

    @Override
    public void deleteAlert(String alertId) {
        logger.info("Deleting alert: {}", alertId);
        alertConfigRepository.deleteById(alertId);
    }

    @Override
    public void enableAlert(String alertId) {
        alertConfigRepository.findById(alertId).ifPresent(alert -> {
            alert.setEnabled(true);
            alertConfigRepository.save(alert);
            logger.info("Enabled alert: {}", alertId);
        });
    }

    @Override
    public void disableAlert(String alertId) {
        alertConfigRepository.findById(alertId).ifPresent(alert -> {
            alert.setEnabled(false);
            alertConfigRepository.save(alert);
            logger.info("Disabled alert: {}", alertId);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlertConfig> findById(String alertId) {
        return alertConfigRepository.findById(alertId);
    }

    private void evaluateAlert(AlertConfig alert, KPIHistory kpiHistory) {
        if (alert.isInCooldown()) {
            logger.debug("Alert {} is in cooldown, skipping", alert.getId());
            return;
        }

        if (checkAlertCondition(alert, kpiHistory.getValue())) {
            triggerAlert(alert, kpiHistory.getKpiName(), kpiHistory.getValue());
        }
    }

    @Override
    public boolean checkAlertCondition(AlertConfig alert, double value) {
        AlertCondition condition = alert.getCondition();
        double threshold = alert.getThreshold();

        return switch (condition) {
            case LESS_THAN -> value < threshold;
            case GREATER_THAN -> value > threshold;
            case EQUALS -> Math.abs(value - threshold) < 0.0001;
            case NOT_EQUALS -> Math.abs(value - threshold) >= 0.0001;
            case PERCENTAGE_DROP, PERCENTAGE_RISE, ANOMALY_DETECTED -> false; // Require historical data
        };
    }

    private void triggerAlert(AlertConfig alert, String kpiName, double value) {
        logger.warn("Alert triggered: {} - KPI {} = {} (threshold: {})",
            alert.getName(), kpiName, value, alert.getThreshold());

        alert.markTriggered();
        alertConfigRepository.save(alert);

        // Dispatch notification
        notificationDispatcher.dispatch(alert, kpiName, value);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agent> findAgentsWithNonAlertingKpiBySkill(String skillNameOrId) {
        if (skillNameOrId == null || skillNameOrId.isBlank()) {
            return new ArrayList<>();
        }

        logger.info("Fetching agents with non-alerting KPIs for skill: {}", skillNameOrId);

        List<Benchmark> benchmarks = benchmarkService.findBySkill(skillNameOrId);

        if (benchmarks.isEmpty()) {
            logger.info("No benchmarks found for skill: {}", skillNameOrId);
            return new ArrayList<>();
        }

        List<Agent> nonAlertingAgents = new ArrayList<>();

        for (Benchmark benchmark : benchmarks) {
            String benchmarkId = benchmark.getId();

            // Get all active alert configs for this benchmark
            List<AlertConfig> alertConfigs = alertConfigRepository.findByBenchmarkIdAndEnabled(benchmarkId, true);

            // Get all agents evaluated by this benchmark
            for (Agent agent : benchmark.getEvaluates()) {
                // Get the latest benchmark run for this agent and benchmark
                List<BenchmarkRun> runs = benchmarkRunRepository
                        .findByBenchmarkIdAndAgentIdOrderByStartedAtDesc(benchmarkId, agent.getId());

                if (runs.isEmpty()) {
                    // No runs, consider agent as non-alerting (no data to alert on)
                    if (!nonAlertingAgents.contains(agent)) {
                        nonAlertingAgents.add(agent);
                    }
                    continue;
                }

                BenchmarkRun latestRun = runs.get(0);
                List<BenchmarkResult> results = latestRun.getResults();

                boolean hasAlert = false;

                // Check each result's KPI values against alert configs
                for (BenchmarkResult result : results) {
                    Map<String, Double> kpiValues = result.getKpiValues();

                    for (AlertConfig alertConfig : alertConfigs) {
                        String kpiName = alertConfig.getKpiName();
                        Double kpiValue = kpiValues.get(kpiName);

                        if (kpiValue != null && checkAlertCondition(alertConfig, kpiValue)) {
                            hasAlert = true;
                            break;
                        }
                    }

                    if (hasAlert) {
                        break;
                    }
                }

                if (!hasAlert && !nonAlertingAgents.contains(agent)) {
                    nonAlertingAgents.add(agent);
                }
            }
        }

        logger.info("Found {} agents with non-alerting KPIs for skill: {}", nonAlertingAgents.size(), skillNameOrId);
        return nonAlertingAgents;
    }
}
