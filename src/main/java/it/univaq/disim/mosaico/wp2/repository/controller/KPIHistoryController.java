package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.KPIHistory;
import it.univaq.disim.mosaico.wp2.repository.repository.KPIHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for KPI history and trends.
 */
@RestController
@RequestMapping("/api/kpi-history")
public class KPIHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(KPIHistoryController.class);

    private final KPIHistoryRepository kpiHistoryRepository;

    public KPIHistoryController(KPIHistoryRepository kpiHistoryRepository) {
        this.kpiHistoryRepository = kpiHistoryRepository;
    }

    @GetMapping
    public ResponseEntity<List<KPIHistory>> getKpiHistory(
            @RequestParam String benchmarkId,
            @RequestParam String agentId,
            @RequestParam(required = false) String kpiName) {

        List<KPIHistory> history;
        if (kpiName != null) {
            history = kpiHistoryRepository.findHistoryForKpi(benchmarkId, agentId, kpiName);
        } else {
            history = kpiHistoryRepository.findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc(benchmarkId, agentId);
        }

        return ResponseEntity.ok(history);
    }

    @GetMapping("/latest")
    public ResponseEntity<KPIHistory> getLatestKpi(
            @RequestParam String benchmarkId,
            @RequestParam String agentId,
            @RequestParam String kpiName) {

        return kpiHistoryRepository.findLatestForKpi(benchmarkId, agentId, kpiName)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/average")
    public ResponseEntity<Map<String, Double>> getAverageKpi(
            @RequestParam String benchmarkId,
            @RequestParam String agentId,
            @RequestParam String kpiName) {

        Double average = kpiHistoryRepository.findAverageValueForKpi(benchmarkId, agentId, kpiName);
        if (average == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of("average", average, "kpiName", kpiName.hashCode() * 1.0));
    }

    @GetMapping("/trend")
    public ResponseEntity<List<KPIHistory>> getKpiTrend(
            @RequestParam String benchmarkId,
            @RequestParam String agentId,
            @RequestParam String kpiName,
            @RequestParam(defaultValue = "30") int days) {

        // Get historical data for trend analysis
        List<KPIHistory> history = kpiHistoryRepository.findHistoryForKpi(benchmarkId, agentId, kpiName);
        return ResponseEntity.ok(history);
    }
}
