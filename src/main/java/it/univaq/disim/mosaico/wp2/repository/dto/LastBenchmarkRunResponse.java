package it.univaq.disim.mosaico.wp2.repository.dto;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;

/**
 * Response DTO containing the last BenchmarkRun and its summary for an agent.
 */
public record LastBenchmarkRunResponse(
    BenchmarkRun benchmarkRun,
    BenchmarkRunSummary summary
) {
    public static LastBenchmarkRunResponse from(BenchmarkRun run) {
        if (run == null) {
            return null;
        }
        return new LastBenchmarkRunResponse(run, BenchmarkRunSummary.from(run));
    }
}
