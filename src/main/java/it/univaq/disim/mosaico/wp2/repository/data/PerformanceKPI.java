package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;

/**
 * PerformanceKPI class for MOSAICO taxonomy.
 * Represents performance key performance indicators.
 */
@Entity
@Table(name = "performance_kpis")
public class PerformanceKPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "benchmark_id")
    private String benchmarkId;

    @Column(columnDefinition = "text")
    private String description;
    @Transient
    private List<MetricKey> includes;
    private KPISpecification specification;
    
    public PerformanceKPI() {}
    public PerformanceKPI(String description, List<MetricKey> includes) {

        this.description = description;
        this.includes = includes;
    }
    public KPISpecification getSpecification() {return specification;}
    public void setSpecification(KPISpecification specification) {this.specification = specification;}
    public List<MetricKey> getIncludes() {return includes;}
    public void setIncludes(List<MetricKey> includes) {this.includes = includes;   }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(String benchmarkId) { this.benchmarkId = benchmarkId; }
}