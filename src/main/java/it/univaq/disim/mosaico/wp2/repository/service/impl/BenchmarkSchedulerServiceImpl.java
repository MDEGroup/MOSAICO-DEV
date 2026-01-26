package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import it.univaq.disim.mosaico.wp2.repository.repository.ScheduleConfigRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of BenchmarkSchedulerService.
 */
@Service
@Transactional
public class BenchmarkSchedulerServiceImpl implements BenchmarkSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkSchedulerServiceImpl.class);

    private final ScheduleConfigRepository repository;

    public BenchmarkSchedulerServiceImpl(ScheduleConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public ScheduleConfig createSchedule(ScheduleConfig config) {
        logger.info("Creating schedule for benchmark {} and agent {}", config.getBenchmarkId(), config.getAgentId());
        validateCronExpression(config.getCronExpression());

        // Initialize default values if not set (when created via REST API)
        if (config.getCreatedAt() == null) {
            config.setCreatedAt(Instant.now());
        }
        if (config.getTimezone() == null) {
            config.setTimezone("UTC");
        }
        if (config.getEnabled() == null) {
            config.setEnabled(true);
        }
        if (config.getRunCount() == null) {
            config.setRunCount(0);
        }
        if (config.getFailureCount() == null) {
            config.setFailureCount(0);
        }
        if (config.getConsecutiveFailures() == null) {
            config.setConsecutiveFailures(0);
        }
        if (config.getMaxConsecutiveFailures() == null) {
            config.setMaxConsecutiveFailures(3);
        }
        if (config.getAutoDisableOnFailure() == null) {
            config.setAutoDisableOnFailure(true);
        }

        calculateNextRunTime(config);
        return repository.save(config);
    }

    @Override
    public ScheduleConfig updateSchedule(ScheduleConfig config) {
        logger.info("Updating schedule {}", config.getId());
        if (config.getCronExpression() != null) {
            validateCronExpression(config.getCronExpression());
            calculateNextRunTime(config);
        }
        return repository.save(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleConfig> findById(String id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleConfig> findByBenchmarkId(String benchmarkId) {
        return repository.findByBenchmarkId(benchmarkId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleConfig> findEnabledSchedules() {
        return repository.findByEnabled(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleConfig> findDueSchedules() {
        return repository.findDueSchedules(Instant.now());
    }

    @Override
    public void enableSchedule(String scheduleId) {
        repository.findById(scheduleId).ifPresent(config -> {
            config.setEnabled(true);
            calculateNextRunTime(config);
            repository.save(config);
            logger.info("Enabled schedule {}", scheduleId);
        });
    }

    @Override
    public void disableSchedule(String scheduleId) {
        repository.findById(scheduleId).ifPresent(config -> {
            config.setEnabled(false);
            repository.save(config);
            logger.info("Disabled schedule {}", scheduleId);
        });
    }

    @Override
    public void deleteSchedule(String scheduleId) {
        repository.deleteById(scheduleId);
        logger.info("Deleted schedule {}", scheduleId);
    }

    @Override
    public void updateNextRunTime(String scheduleId) {
        repository.findById(scheduleId).ifPresent(config -> {
            calculateNextRunTime(config);
            repository.save(config);
        });
    }

    @Override
    public void recordRunSuccess(String scheduleId, String runId) {
        repository.findById(scheduleId).ifPresent(config -> {
            config.recordRunSuccess(runId);
            calculateNextRunTime(config);
            repository.save(config);
            logger.info("Recorded successful run {} for schedule {}", runId, scheduleId);
        });
    }

    @Override
    public void recordRunFailure(String scheduleId, String runId) {
        repository.findById(scheduleId).ifPresent(config -> {
            config.recordRunFailure(runId);
            calculateNextRunTime(config);
            repository.save(config);
            logger.warn("Recorded failed run {} for schedule {}. Consecutive failures: {}",
                runId, scheduleId, config.getConsecutiveFailures());
        });
    }

    private void validateCronExpression(String cronExpression) {
        if (!CronExpression.isValidExpression(cronExpression)) {
            throw new IllegalArgumentException("Invalid cron expression: " + cronExpression);
        }
    }

    private void calculateNextRunTime(ScheduleConfig config) {
        try {
            CronExpression cron = CronExpression.parse(config.getCronExpression());
            ZonedDateTime now = ZonedDateTime.now(config.getTimezoneId());
            ZonedDateTime next = cron.next(now);
            if (next != null) {
                config.setNextRunAt(next.toInstant());
            }
        } catch (Exception e) {
            logger.error("Error calculating next run time for schedule {}: {}", config.getId(), e.getMessage());
        }
    }
}
