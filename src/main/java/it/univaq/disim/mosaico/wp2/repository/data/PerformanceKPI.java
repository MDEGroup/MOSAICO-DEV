package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.UUID;

/**
 * PerformanceKPI class for MOSAICO taxonomy.
 * Represents performance key performance indicators.
 */
@Entity
@Table(name = "performance_kpis")
public class PerformanceKPI {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String description;

    @Transient
    private List<Metric> includes;

    public PerformanceKPI() {}

    public PerformanceKPI(String id, String description, List<Metric> includes) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.description = description;
        this.includes = includes;
    }

    public String id() { return id; }
    public String description() { return description; }
    public List<Metric> includes() { return includes; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}