package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;

/**
 * Orchestrates the complete benchmark execution flow.
 * Coordinates data acquisition, metric computation, KPI evaluation,
 * persistence, and alerting.
 */
public interface BenchmarkOrchestrator {

    /**
     * Executes a complete benchmark run.
     *
     * @param runId the ID of the benchmark run to execute
     * @return the completed BenchmarkRun with results
     */
    BenchmarkRun executeBenchmarkRun(String runId);

    /**
     * Executes a benchmark run asynchronously.
     *
     * @param runId the ID of the benchmark run to execute
     */
    void executeBenchmarkRunAsync(String runId);

    /**
     * Cancels a running benchmark.
     *
     * @param runId the ID of the benchmark run to cancel
     */
    void cancelBenchmarkRun(String runId);

    /**
     * Retries a failed benchmark run.
     *
     * @param runId the ID of the failed run to retry
     * @return the new run ID
     */
    String retryBenchmarkRun(String runId);
}
