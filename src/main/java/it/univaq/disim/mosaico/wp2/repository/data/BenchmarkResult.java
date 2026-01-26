package it.univaq.disim.mosaico.wp2.repository.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity representing the results of a benchmark run for a specific trace.
 * Contains the computed metrics and KPI values for that trace.
 */
@Entity
@Table(name = "benchmark_results", indexes = {
    @Index(name = "idx_benchmark_result_run_id", columnList = "run_id"),
    @Index(name = "idx_benchmark_result_trace_id", columnList = "trace_id"),
    @Index(name = "idx_benchmark_result_created_at", columnList = "created_at")
})
public class BenchmarkResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_id", nullable = false)
    @JsonBackReference
    private BenchmarkRun benchmarkRun;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "expected_text", columnDefinition = "text")
    private String expectedText;

    @Column(name = "generated_text", columnDefinition = "text")
    private String generatedText;

    @OneToMany(mappedBy = "benchmarkResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetricSnapshot> metricSnapshots = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "benchmark_result_kpi_values", joinColumns = @JoinColumn(name = "result_id"))
    @MapKeyColumn(name = "kpi_name")
    @Column(name = "kpi_value")
    private Map<String, Double> kpiValues = new HashMap<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // JPA
    public BenchmarkResult() {
    }

    public BenchmarkResult(BenchmarkRun benchmarkRun, String traceId) {
        this.benchmarkRun = benchmarkRun;
        this.traceId = traceId;
        this.createdAt = Instant.now();
    }

    // Record-style accessors
    public String id() { return id; }
    public BenchmarkRun benchmarkRun() { return benchmarkRun; }
    public String traceId() { return traceId; }
    public Instant createdAt() { return createdAt; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BenchmarkRun getBenchmarkRun() { return benchmarkRun; }
    public void setBenchmarkRun(BenchmarkRun benchmarkRun) { this.benchmarkRun = benchmarkRun; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getExpectedText() { return expectedText; }
    public void setExpectedText(String expectedText) { this.expectedText = expectedText; }

    public String getGeneratedText() { return generatedText; }
    public void setGeneratedText(String generatedText) { this.generatedText = generatedText; }

    public List<MetricSnapshot> getMetricSnapshots() { return metricSnapshots; }
    public void setMetricSnapshots(List<MetricSnapshot> metricSnapshots) { this.metricSnapshots = metricSnapshots; }

    public Map<String, Double> getKpiValues() { return kpiValues; }
    public void setKpiValues(Map<String, Double> kpiValues) { this.kpiValues = kpiValues; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public void addMetricSnapshot(MetricSnapshot snapshot) {
        metricSnapshots.add(snapshot);
        snapshot.setBenchmarkResult(this);
    }

    public void addKpiValue(String kpiName, Double value) {
        kpiValues.put(kpiName, value);
    }
}
