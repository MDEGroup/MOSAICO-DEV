package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Entity representing a scheduled benchmark execution configuration.
 * Stores cron expression and scheduling metadata for automated benchmark runs.
 */
@Entity
@Table(name = "schedule_configs", indexes = {
    @Index(name = "idx_schedule_config_benchmark_id", columnList = "benchmark_id"),
    @Index(name = "idx_schedule_config_agent_id", columnList = "agent_id"),
    @Index(name = "idx_schedule_config_enabled", columnList = "enabled"),
    @Index(name = "idx_schedule_config_next_run_at", columnList = "next_run_at")
})
public class ScheduleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "benchmark_id", nullable = false)
    private String benchmarkId;

    @Column(name = "agent_id", nullable = false)
    private String agentId;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "last_run_at")
    private Instant lastRunAt;

    @Column(name = "last_run_id")
    private String lastRunId;

    @Column(name = "last_run_status")
    private String lastRunStatus;

    @Column(name = "next_run_at")
    private Instant nextRunAt;

    @Column(name = "run_count")
    private Integer runCount;

    @Column(name = "failure_count")
    private Integer failureCount;

    @Column(name = "consecutive_failures")
    private Integer consecutiveFailures;

    @Column(name = "max_consecutive_failures")
    private Integer maxConsecutiveFailures;

    @Column(name = "auto_disable_on_failure")
    private Boolean autoDisableOnFailure;

    @Column(name = "langfuse_run_name")
    private String langfuseRunName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    // JPA
    public ScheduleConfig() {
    }

    public ScheduleConfig(String name, String benchmarkId, String agentId, String cronExpression) {
        this.name = name;
        this.benchmarkId = benchmarkId;
        this.agentId = agentId;
        this.cronExpression = cronExpression;
        this.timezone = "UTC";
        this.enabled = true;
        this.runCount = 0;
        this.failureCount = 0;
        this.consecutiveFailures = 0;
        this.maxConsecutiveFailures = 3;
        this.autoDisableOnFailure = true;
        this.createdAt = Instant.now();
    }

    // Record-style accessors
    public String id() { return id; }
    public String name() { return name; }
    public String benchmarkId() { return benchmarkId; }
    public String agentId() { return agentId; }
    public String cronExpression() { return cronExpression; }
    public Boolean enabled() { return enabled; }
    public Instant nextRunAt() { return nextRunAt; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(String benchmarkId) { this.benchmarkId = benchmarkId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Instant getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(Instant lastRunAt) { this.lastRunAt = lastRunAt; }

    public String getLastRunId() { return lastRunId; }
    public void setLastRunId(String lastRunId) { this.lastRunId = lastRunId; }

    public String getLastRunStatus() { return lastRunStatus; }
    public void setLastRunStatus(String lastRunStatus) { this.lastRunStatus = lastRunStatus; }

    public Instant getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(Instant nextRunAt) { this.nextRunAt = nextRunAt; }

    public Integer getRunCount() { return runCount; }
    public void setRunCount(Integer runCount) { this.runCount = runCount; }

    public Integer getFailureCount() { return failureCount; }
    public void setFailureCount(Integer failureCount) { this.failureCount = failureCount; }

    public Integer getConsecutiveFailures() { return consecutiveFailures; }
    public void setConsecutiveFailures(Integer consecutiveFailures) { this.consecutiveFailures = consecutiveFailures; }

    public Integer getMaxConsecutiveFailures() { return maxConsecutiveFailures; }
    public void setMaxConsecutiveFailures(Integer maxConsecutiveFailures) { this.maxConsecutiveFailures = maxConsecutiveFailures; }

    public Boolean getAutoDisableOnFailure() { return autoDisableOnFailure; }
    public void setAutoDisableOnFailure(Boolean autoDisableOnFailure) { this.autoDisableOnFailure = autoDisableOnFailure; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLangfuseRunName() { return langfuseRunName; }
    public void setLangfuseRunName(String langfuseRunName) { this.langfuseRunName = langfuseRunName; }

    // Helper methods
    public void recordRunSuccess(String runId) {
        this.lastRunAt = Instant.now();
        this.lastRunId = runId;
        this.lastRunStatus = "COMPLETED";
        this.runCount = (runCount == null ? 0 : runCount) + 1;
        this.consecutiveFailures = 0;
    }

    public void recordRunFailure(String runId) {
        this.lastRunAt = Instant.now();
        this.lastRunId = runId;
        this.lastRunStatus = "FAILED";
        this.runCount = (runCount == null ? 0 : runCount) + 1;
        this.failureCount = (failureCount == null ? 0 : failureCount) + 1;
        this.consecutiveFailures = (consecutiveFailures == null ? 0 : consecutiveFailures) + 1;

        if (autoDisableOnFailure != null && autoDisableOnFailure
            && maxConsecutiveFailures != null
            && consecutiveFailures >= maxConsecutiveFailures) {
            this.enabled = false;
        }
    }

    public ZoneId getTimezoneId() {
        return timezone != null ? ZoneId.of(timezone) : ZoneId.of("UTC");
    }

    public ZonedDateTime getNextRunAtZoned() {
        return nextRunAt != null ? nextRunAt.atZone(getTimezoneId()) : null;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
