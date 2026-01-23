package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.service.AlertEvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for alert configuration operations.
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertConfigController {

    private static final Logger logger = LoggerFactory.getLogger(AlertConfigController.class);

    private final AlertEvaluationService alertService;

    public AlertConfigController(AlertEvaluationService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertConfig> createAlert(@RequestBody AlertConfig config) {
        AlertConfig created = alertService.createAlert(config);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{alertId}")
    public ResponseEntity<AlertConfig> getAlert(@PathVariable String alertId) {
        return alertService.findById(alertId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AlertConfig>> getAlerts(@RequestParam(required = false) String benchmarkId) {
        if (benchmarkId != null) {
            return ResponseEntity.ok(alertService.findAlertsByBenchmarkId(benchmarkId));
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{alertId}")
    public ResponseEntity<AlertConfig> updateAlert(
            @PathVariable String alertId,
            @RequestBody AlertConfig config) {
        config.setId(alertId);
        AlertConfig updated = alertService.updateAlert(config);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable String alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{alertId}/enable")
    public ResponseEntity<Void> enableAlert(@PathVariable String alertId) {
        alertService.enableAlert(alertId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{alertId}/disable")
    public ResponseEntity<Void> disableAlert(@PathVariable String alertId) {
        alertService.disableAlert(alertId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/evaluate")
    public ResponseEntity<List<AlertConfig>> evaluateKpi(
            @RequestParam String benchmarkId,
            @RequestParam String kpiName,
            @RequestParam double value) {

        List<AlertConfig> triggered = alertService.evaluateKpiValue(benchmarkId, kpiName, value);
        return ResponseEntity.ok(triggered);
    }
}
