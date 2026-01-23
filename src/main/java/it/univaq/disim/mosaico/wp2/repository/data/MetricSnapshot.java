package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Entity representing a snapshot of a computed metric for historical tracking.
 * Stores the metric value at a specific point in time for a benchmark result.
 */
@Entity
@Table(name = "metric_snapshots", indexes = {
    @Index(name = "idx_metric_snapshot_run_id", columnList = "run_id"),
    @Index(name = "idx_metric_snapshot_result_id", columnList = "result_id"),
    @Index(name = "idx_metric_snapshot_metric_type", columnList = "metric_type"),
    @Index(name = "idx_metric_snapshot_timestamp", columnList = "timestamp")
})
public class MetricSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "run_id", nullable = false)
    private String runId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private BenchmarkResult benchmarkResult;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;

    @Column(name = "metric_key", nullable = false)
    private String metricKey;

    @Column(name = "metric_name")
    private String metricName;

    @Column(name = "value", nullable = false)
    private Double value;

    @Column(name = "unit")
    private String unit;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    // JPA
    public MetricSnapshot() {
    }

    public MetricSnapshot(String runId, MetricType metricType, String metricKey, Double value) {
        this.runId = runId;
        this.metricType = metricType;
        this.metricKey = metricKey;
        this.value = value;
        this.timestamp = Instant.now();
    }

    // Record-style accessors
    public String id() { return id; }
    public String runId() { return runId; }
    public MetricType metricType() { return metricType; }
    public String metricKey() { return metricKey; }
    public Double value() { return value; }
    public Instant timestamp() { return timestamp; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public BenchmarkResult getBenchmarkResult() { return benchmarkResult; }
    public void setBenchmarkResult(BenchmarkResult benchmarkResult) { this.benchmarkResult = benchmarkResult; }

    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }

    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }

    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    // Factory method from Metric
    public static MetricSnapshot fromMetric(String runId, Metric metric, String traceId) {
        MetricSnapshot snapshot = new MetricSnapshot();
        snapshot.setRunId(runId);
        snapshot.setMetricType(metric.getType());
        snapshot.setMetricKey(metric.getType().name());
        snapshot.setMetricName(metric.getName());
        snapshot.setValue(metric.getFloatValue().map(Float::doubleValue).orElse(0.0));
        snapshot.setUnit(metric.getUnit());
        snapshot.setTraceId(traceId);
        snapshot.setTimestamp(Instant.now());
        return snapshot;
    }
}
