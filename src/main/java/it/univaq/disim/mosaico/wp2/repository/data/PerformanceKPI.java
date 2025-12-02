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
    @Column(columnDefinition = "text")
    private String description;
    @Transient
    private List<Metric> includes;

    public List<Metric> getIncludes() {
        return includes;
    }
    public void setIncludes(List<Metric> includes) {
        this.includes = includes;
    }
    public PerformanceKPI() {}
    public PerformanceKPI(String description, List<Metric> includes) {
        this.description = description;
        this.includes = includes;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}