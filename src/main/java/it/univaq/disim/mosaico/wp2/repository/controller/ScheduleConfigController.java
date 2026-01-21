package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for schedule configuration operations.
 */
@RestController
@RequestMapping("/api/schedules")
public class ScheduleConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleConfigController.class);

    private final BenchmarkSchedulerService schedulerService;

    public ScheduleConfigController(BenchmarkSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping
    public ResponseEntity<ScheduleConfig> createSchedule(@RequestBody ScheduleConfig config) {
        try {
            ScheduleConfig created = schedulerService.createSchedule(config);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleConfig> getSchedule(@PathVariable String scheduleId) {
        return schedulerService.findById(scheduleId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ScheduleConfig>> getSchedules(
            @RequestParam(required = false) String benchmarkId,
            @RequestParam(required = false) Boolean enabled) {

        List<ScheduleConfig> schedules;
        if (benchmarkId != null) {
            schedules = schedulerService.findByBenchmarkId(benchmarkId);
        } else if (enabled != null && enabled) {
            schedules = schedulerService.findEnabledSchedules();
        } else {
            schedules = schedulerService.findEnabledSchedules();
        }

        return ResponseEntity.ok(schedules);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleConfig> updateSchedule(
            @PathVariable String scheduleId,
            @RequestBody ScheduleConfig config) {
        config.setId(scheduleId);
        try {
            ScheduleConfig updated = schedulerService.updateSchedule(config);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String scheduleId) {
        schedulerService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{scheduleId}/enable")
    public ResponseEntity<Void> enableSchedule(@PathVariable String scheduleId) {
        schedulerService.enableSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/disable")
    public ResponseEntity<Void> disableSchedule(@PathVariable String scheduleId) {
        schedulerService.disableSchedule(scheduleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/due")
    public ResponseEntity<List<ScheduleConfig>> getDueSchedules() {
        return ResponseEntity.ok(schedulerService.findDueSchedules());
    }
}
