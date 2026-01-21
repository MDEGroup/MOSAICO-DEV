package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.KPIStatus;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Entity representing historical KPI values for trend analysis and comparison.
 * Tracks KPI values over time with baseline comparisons and threshold status.
 */
@Entity
@Table(name = "kpi_history", indexes = {
    @Index(name = "idx_kpi_history_benchmark_id", columnList = "benchmark_id"),
    @Index(name = "idx_kpi_history_agent_id", columnList = "agent_id"),
    @Index(name = "idx_kpi_history_kpi_name", columnList = "kpi_name"),
    @Index(name = "idx_kpi_history_recorded_at", columnList = "recorded_at"),
    @Index(name = "idx_kpi_history_status", columnList = "status")
})
public class KPIHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "benchmark_id", nullable = false)
    private String benchmarkId;

    @Column(name = "agent_id", nullable = false)
    private String agentId;

    @Column(name = "run_id")
    private String runId;

    @Column(name = "kpi_id")
    private String kpiId;

    @Column(name = "kpi_name", nullable = false)
    private String kpiName;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "baseline_value")
    private Double baselineValue;

    @Column(name = "threshold_min")
    private Double thresholdMin;

    @Column(name = "threshold_max")
    private Double thresholdMax;

    @Column(name = "delta_from_baseline")
    private Double deltaFromBaseline;

    @Column(name = "delta_percentage")
    private Double deltaPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private KPIStatus status;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    // JPA
    public KPIHistory() {
    }

    public KPIHistory(String benchmarkId, String agentId, String kpiName, Double value) {
        this.benchmarkId = benchmarkId;
        this.agentId = agentId;
        this.kpiName = kpiName;
        this.value = value;
        this.status = KPIStatus.UNKNOWN;
        this.recordedAt = Instant.now();
    }

    // Record-style accessors
    public String id() { return id; }
    public String benchmarkId() { return benchmarkId; }
    public String agentId() { return agentId; }
    public String kpiName() { return kpiName; }
    public Double value() { return value; }
    public KPIStatus status() { return status; }
    public Instant recordedAt() { return recordedAt; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(String benchmarkId) { this.benchmarkId = benchmarkId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getKpiId() { return kpiId; }
    public void setKpiId(String kpiId) { this.kpiId = kpiId; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Double getBaselineValue() { return baselineValue; }
    public void setBaselineValue(Double baselineValue) {
        this.baselineValue = baselineValue;
        updateDelta();
    }

    public Double getThresholdMin() { return thresholdMin; }
    public void setThresholdMin(Double thresholdMin) { this.thresholdMin = thresholdMin; }

    public Double getThresholdMax() { return thresholdMax; }
    public void setThresholdMax(Double thresholdMax) { this.thresholdMax = thresholdMax; }

    public Double getDeltaFromBaseline() { return deltaFromBaseline; }
    public void setDeltaFromBaseline(Double deltaFromBaseline) { this.deltaFromBaseline = deltaFromBaseline; }

    public Double getDeltaPercentage() { return deltaPercentage; }
    public void setDeltaPercentage(Double deltaPercentage) { this.deltaPercentage = deltaPercentage; }

    public KPIStatus getStatus() { return status; }
    public void setStatus(KPIStatus status) { this.status = status; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }

    // Helper methods
    private void updateDelta() {
        if (baselineValue != null && value != null) {
            this.deltaFromBaseline = value - baselineValue;
            if (baselineValue != 0) {
                this.deltaPercentage = ((value - baselineValue) / baselineValue) * 100;
            }
        }
    }

    public void evaluateStatus() {
        if (thresholdMin != null && value < thresholdMin) {
            this.status = KPIStatus.CRITICAL;
        } else if (thresholdMax != null && value > thresholdMax) {
            this.status = KPIStatus.WARNING;
        } else if (thresholdMin != null || thresholdMax != null) {
            this.status = KPIStatus.HEALTHY;
        } else {
            this.status = KPIStatus.UNKNOWN;
        }
    }
}
