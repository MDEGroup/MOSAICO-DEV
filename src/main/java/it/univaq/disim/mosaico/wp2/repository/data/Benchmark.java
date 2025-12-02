package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.UUID;

/**
 * Benchmark class for MOSAICO taxonomy.
 * Represents evaluation benchmarks for agents.
 */
@Entity
@Table(name = "benchmarks")
public class Benchmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    private String metadata;
    private String features;
    private String datasetRef;
    private String taskDef;
    private String protocolVersion;
    @Transient
    private List<Agent> evaluates;

    @Transient
    private List<PerformanceKPI> measures;

    @Transient
    private List<Skill> assess;
    public Benchmark(String id, String metadata, String features, String datasetRef, String taskDef, String protocolVersion, List<Agent> evaluates, List<PerformanceKPI> measures, List<Skill> assess) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.metadata = metadata;
        this.features = features;
        this.datasetRef = datasetRef;
        this.taskDef = taskDef;
        this.protocolVersion = protocolVersion;
        this.evaluates = evaluates;
        this.measures = measures;
        this.assess = assess;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    // other getters/setters omitted for brevity
    public Benchmark() {}
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public String getDatasetRef() { return datasetRef; }
    public void setDatasetRef(String datasetRef) { this.datasetRef = datasetRef; }

    public String getTaskDef() { return taskDef; }
    public void setTaskDef(String taskDef) { this.taskDef = taskDef; }

    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }
    
}