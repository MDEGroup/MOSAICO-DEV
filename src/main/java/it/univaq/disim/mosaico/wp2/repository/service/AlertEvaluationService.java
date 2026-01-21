package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.KPIHistory;

import java.util.List;
import java.util.Optional;

/**
 * Service for evaluating alert conditions and triggering notifications.
 */
public interface AlertEvaluationService {

    /**
     * Evaluates all active alerts for a completed benchmark run.
     */
    void evaluateAlertsForRun(String runId);

    /**
     * Evaluates a specific KPI value against configured alerts.
     */
    List<AlertConfig> evaluateKpiValue(String benchmarkId, String kpiName, double value);

    /**
     * Finds all alert configs for a benchmark.
     */
    List<AlertConfig> findAlertsByBenchmarkId(String benchmarkId);

    /**
     * Creates a new alert configuration.
     */
    AlertConfig createAlert(AlertConfig config);

    /**
     * Updates an alert configuration.
     */
    AlertConfig updateAlert(AlertConfig config);

    /**
     * Deletes an alert configuration.
     */
    void deleteAlert(String alertId);

    /**
     * Enables an alert.
     */
    void enableAlert(String alertId);

    /**
     * Disables an alert.
     */
    void disableAlert(String alertId);

    /**
     * Finds an alert by ID.
     */
    Optional<AlertConfig> findById(String alertId);
}
