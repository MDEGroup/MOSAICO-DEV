package it.univaq.disim.mosaico.wp2.repository.dto;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;

import java.time.Instant;
import java.util.Map;

/**
 * Lightweight summary DTO for BenchmarkRun.
 * Used to avoid serializing the full results list which can be very large.
 */
public record BenchmarkRunSummary(
    String id,
    String benchmarkId,
    String agentId,
    RunStatus status,
    TriggerType triggeredBy,
    String triggeredByUser,
    Instant startedAt,
    Instant completedAt,
    String errorMessage,
    Integer tracesProcessed,
    Integer metricsComputed,
    Integer retryCount,
    String langfuseRunName,
    long durationMillis,
    int resultCount,
    Map<String, Double> kpiValues
) {
    public static BenchmarkRunSummary from(BenchmarkRun run) {
        return new BenchmarkRunSummary(
            run.getId(),
            run.getBenchmarkId(),
            run.getAgentId(),
            run.getStatus(),
            run.getTriggeredBy(),
            run.getTriggeredByUser(),
            run.getStartedAt(),
            run.getCompletedAt(),
            run.getErrorMessage(),
            run.getTracesProcessed(),
            run.getMetricsComputed(),
            run.getRetryCount(),
            run.getLangfuseRunName(),
            run.getDurationMillis(),
            run.getResults() != null ? run.getResults().size() : 0,
            run.getResults() != null && !run.getResults().isEmpty()
                ? run.getResults().get(0).getKpiValues()
                : Map.of()
        );
    }
}
