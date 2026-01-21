package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a single execution of a benchmark against an agent.
 * Tracks the run lifecycle from creation to completion, including status,
 * timing, and any errors encountered.
 */
@Entity
@Table(name = "benchmark_runs", indexes = {
    @Index(name = "idx_benchmark_run_benchmark_id", columnList = "benchmark_id"),
    @Index(name = "idx_benchmark_run_agent_id", columnList = "agent_id"),
    @Index(name = "idx_benchmark_run_status", columnList = "status"),
    @Index(name = "idx_benchmark_run_started_at", columnList = "started_at")
})
public class BenchmarkRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "benchmark_id", nullable = false)
    private String benchmarkId;

    @Column(name = "agent_id", nullable = false)
    private String agentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RunStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by", nullable = false)
    private TriggerType triggeredBy;

    @Column(name = "triggered_by_user")
    private String triggeredByUser;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "traces_processed")
    private Integer tracesProcessed;

    @Column(name = "metrics_computed")
    private Integer metricsComputed;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "schedule_config_id")
    private String scheduleConfigId;

    @OneToMany(mappedBy = "benchmarkRun", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BenchmarkResult> results = new ArrayList<>();

    // JPA
    public BenchmarkRun() {
    }

    public BenchmarkRun(String benchmarkId, String agentId, TriggerType triggeredBy) {
        this.benchmarkId = benchmarkId;
        this.agentId = agentId;
        this.triggeredBy = triggeredBy;
        this.status = RunStatus.PENDING;
        this.retryCount = 0;
    }

    // Record-style accessors
    public String id() { return id; }
    public String benchmarkId() { return benchmarkId; }
    public String agentId() { return agentId; }
    public RunStatus status() { return status; }
    public TriggerType triggeredBy() { return triggeredBy; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public String errorMessage() { return errorMessage; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(String benchmarkId) { this.benchmarkId = benchmarkId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public RunStatus getStatus() { return status; }
    public void setStatus(RunStatus status) { this.status = status; }

    public TriggerType getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(TriggerType triggeredBy) { this.triggeredBy = triggeredBy; }

    public String getTriggeredByUser() { return triggeredByUser; }
    public void setTriggeredByUser(String triggeredByUser) { this.triggeredByUser = triggeredByUser; }

    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }

    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getTracesProcessed() { return tracesProcessed; }
    public void setTracesProcessed(Integer tracesProcessed) { this.tracesProcessed = tracesProcessed; }

    public Integer getMetricsComputed() { return metricsComputed; }
    public void setMetricsComputed(Integer metricsComputed) { this.metricsComputed = metricsComputed; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public String getScheduleConfigId() { return scheduleConfigId; }
    public void setScheduleConfigId(String scheduleConfigId) { this.scheduleConfigId = scheduleConfigId; }

    public List<BenchmarkResult> getResults() { return results; }
    public void setResults(List<BenchmarkResult> results) { this.results = results; }

    // Lifecycle methods
    public void start() {
        this.status = RunStatus.RUNNING;
        this.startedAt = Instant.now();
    }

    public void complete() {
        this.status = RunStatus.COMPLETED;
        this.completedAt = Instant.now();
    }

    public void fail(String errorMessage) {
        this.status = RunStatus.FAILED;
        this.completedAt = Instant.now();
        this.errorMessage = errorMessage;
    }

    public void cancel() {
        this.status = RunStatus.CANCELLED;
        this.completedAt = Instant.now();
    }

    public void incrementRetry() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }

    public long getDurationMillis() {
        if (startedAt == null || completedAt == null) {
            return 0;
        }
        return completedAt.toEpochMilli() - startedAt.toEpochMilli();
    }
}
