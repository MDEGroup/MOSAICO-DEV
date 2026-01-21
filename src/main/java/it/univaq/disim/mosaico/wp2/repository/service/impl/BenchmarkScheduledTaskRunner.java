package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkOrchestrator;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkRunManager;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduled task runner for automated benchmark execution.
 * Runs periodically to check for due schedules and execute them.
 */
@Component
public class BenchmarkScheduledTaskRunner {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkScheduledTaskRunner.class);

    private final BenchmarkSchedulerService schedulerService;
    private final BenchmarkRunManager runManager;
    private final BenchmarkOrchestrator orchestrator;

    public BenchmarkScheduledTaskRunner(
            BenchmarkSchedulerService schedulerService,
            BenchmarkRunManager runManager,
            BenchmarkOrchestrator orchestrator) {
        this.schedulerService = schedulerService;
        this.runManager = runManager;
        this.orchestrator = orchestrator;
    }

    /**
     * Runs every minute to check for due benchmark schedules.
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void runDueSchedules() {
        List<ScheduleConfig> dueSchedules = schedulerService.findDueSchedules();

        if (dueSchedules.isEmpty()) {
            return;
        }

        logger.info("Found {} due schedules to execute", dueSchedules.size());

        for (ScheduleConfig schedule : dueSchedules) {
            try {
                executeBenchmark(schedule);
            } catch (Exception e) {
                logger.error("Failed to execute scheduled benchmark for schedule {}: {}",
                    schedule.getId(), e.getMessage(), e);
                schedulerService.recordRunFailure(schedule.getId(), null);
            }
        }
    }

    private void executeBenchmark(ScheduleConfig schedule) {
        logger.info("Executing scheduled benchmark: schedule={}, benchmark={}, agent={}",
            schedule.getId(), schedule.getBenchmarkId(), schedule.getAgentId());

        // Create the run
        BenchmarkRun run = runManager.createRun(
            schedule.getBenchmarkId(),
            schedule.getAgentId(),
            TriggerType.SCHEDULED,
            "scheduler"
        );
        run.setScheduleConfigId(schedule.getId());

        // Update next run time before execution
        schedulerService.updateNextRunTime(schedule.getId());

        // Execute asynchronously
        orchestrator.executeBenchmarkRunAsync(run.getId());

        logger.info("Started scheduled benchmark run: {}", run.getId());
    }

    /**
     * Runs every hour to clean up stale runs (stuck in RUNNING status).
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void cleanupStaleRuns() {
        logger.debug("Running stale run cleanup");
        // TODO: Find runs stuck in RUNNING status for more than X hours and mark as failed
    }

    /**
     * Runs daily at midnight to generate summary reports.
     */
    @Scheduled(cron = "0 0 0 * * *") // Daily at midnight
    public void generateDailyReports() {
        logger.info("Generating daily benchmark reports");
        // TODO: Generate daily summary reports
    }
}
