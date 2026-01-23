package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkOrchestrator;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for benchmark run operations.
 */
@RestController
@RequestMapping("/api/benchmark-runs")
public class BenchmarkRunController {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunController.class);

    private final BenchmarkRunManager runManager;
    private final BenchmarkOrchestrator orchestrator;

    public BenchmarkRunController(BenchmarkRunManager runManager, BenchmarkOrchestrator orchestrator) {
        this.runManager = runManager;
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<BenchmarkRun> triggerRun(@RequestBody Map<String, String> request) {
        String benchmarkId = request.get("benchmarkId");
        String agentId = request.get("agentId");
        String triggeredBy = request.getOrDefault("triggeredBy", "api");
        String langfuseRunName = request.get("langfuseRunName");

        if (benchmarkId == null || agentId == null) {
            return ResponseEntity.badRequest().build();
        }

        BenchmarkRun run = runManager.createRun(benchmarkId, agentId, TriggerType.MANUAL, triggeredBy, langfuseRunName);
        logger.info("Created benchmark run: {}", run.getId());

        // Execute asynchronously
        orchestrator.executeBenchmarkRunAsync(run.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(run);
    }

    @GetMapping("/{runId}")
    public ResponseEntity<BenchmarkRun> getRun(@PathVariable String runId) {
        return runManager.findById(runId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BenchmarkRun>> getRunsByBenchmark(
            @RequestParam(required = false) String benchmarkId,
            @RequestParam(required = false) String agentId,
            @RequestParam(required = false) RunStatus status) {

        List<BenchmarkRun> runs;
        if (benchmarkId != null) {
            runs = runManager.findByBenchmarkId(benchmarkId);
        } else if (agentId != null) {
            runs = runManager.findByAgentId(agentId);
        } else if (status != null) {
            runs = runManager.findByStatus(status);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(runs);
    }

    @GetMapping("/{benchmarkId}/{agentId}/history")
    public ResponseEntity<List<BenchmarkRun>> getRunHistory(
            @PathVariable String benchmarkId,
            @PathVariable String agentId,
            @RequestParam(defaultValue = "10") int limit) {

        List<BenchmarkRun> history = runManager.getRunHistory(benchmarkId, agentId, limit);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{runId}/cancel")
    public ResponseEntity<BenchmarkRun> cancelRun(@PathVariable String runId) {
        try {
            orchestrator.cancelBenchmarkRun(runId);
            return runManager.findById(runId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Failed to cancel run: {}", runId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{runId}/retry")
    public ResponseEntity<Map<String, String>> retryRun(@PathVariable String runId) {
        try {
            String newRunId = orchestrator.retryBenchmarkRun(runId);
            orchestrator.executeBenchmarkRunAsync(newRunId);
            return ResponseEntity.accepted().body(Map.of("runId", newRunId));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to retry run: {}", runId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
