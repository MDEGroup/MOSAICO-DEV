package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing benchmark scheduling.
 * Handles CRUD operations for schedule configurations and scheduling logic.
 */
public interface BenchmarkSchedulerService {

    /**
     * Creates a new schedule configuration.
     */
    ScheduleConfig createSchedule(ScheduleConfig config);

    /**
     * Updates an existing schedule configuration.
     */
    ScheduleConfig updateSchedule(ScheduleConfig config);

    /**
     * Finds a schedule by ID.
     */
    Optional<ScheduleConfig> findById(String id);

    /**
     * Finds all schedules for a benchmark.
     */
    List<ScheduleConfig> findByBenchmarkId(String benchmarkId);

    /**
     * Finds all enabled schedules.
     */
    List<ScheduleConfig> findEnabledSchedules();

    /**
     * Finds schedules that are due to run.
     */
    List<ScheduleConfig> findDueSchedules();

    /**
     * Enables a schedule.
     */
    void enableSchedule(String scheduleId);

    /**
     * Disables a schedule.
     */
    void disableSchedule(String scheduleId);

    /**
     * Deletes a schedule.
     */
    void deleteSchedule(String scheduleId);

    /**
     * Calculates and updates the next run time for a schedule.
     */
    void updateNextRunTime(String scheduleId);

    /**
     * Records a successful run for a schedule.
     */
    void recordRunSuccess(String scheduleId, String runId);

    /**
     * Records a failed run for a schedule.
     */
    void recordRunFailure(String scheduleId, String runId);
}
