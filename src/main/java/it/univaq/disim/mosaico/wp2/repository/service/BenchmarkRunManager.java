package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;

import java.util.List;
import java.util.Optional;

/**
 * Manages benchmark run lifecycle and state transitions.
 */
public interface BenchmarkRunManager {

    /**
     * Creates a new benchmark run.
     */
    default BenchmarkRun createRun(String benchmarkId, String agentId, TriggerType triggerType, String triggeredBy) {
        return createRun(benchmarkId, agentId, triggerType, triggeredBy, null);
    }

    BenchmarkRun createRun(String benchmarkId, String agentId, TriggerType triggerType, String triggeredBy,
                           String langfuseRunName);

    /**
     * Finds a run by ID.
     */
    Optional<BenchmarkRun> findById(String runId);

    /**
     * Finds runs by benchmark ID.
     */
    List<BenchmarkRun> findByBenchmarkId(String benchmarkId);

    /**
     * Finds runs by agent ID.
     */
    List<BenchmarkRun> findByAgentId(String agentId);

    /**
     * Finds runs by status.
     */
    List<BenchmarkRun> findByStatus(RunStatus status);

    /**
     * Gets the run history for a benchmark and agent.
     */
    List<BenchmarkRun> getRunHistory(String benchmarkId, String agentId, int limit);

    /**
     * Starts a run (transitions from PENDING to RUNNING).
     */
    BenchmarkRun startRun(String runId);

    /**
     * Completes a run successfully.
     */
    BenchmarkRun completeRun(String runId, int tracesProcessed, int metricsComputed);

    /**
     * Fails a run with an error message.
     */
    BenchmarkRun failRun(String runId, String errorMessage);

    /**
     * Cancels a run.
     */
    BenchmarkRun cancelRun(String runId);

    /**
     * Updates run progress metrics.
     */
    void updateProgress(String runId, int tracesProcessed, int metricsComputed);
}
