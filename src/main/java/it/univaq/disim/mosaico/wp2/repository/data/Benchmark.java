package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
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
    private String datasetRef;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "benchmark_evaluates", joinColumns = @JoinColumn(name = "benchmark_id"), inverseJoinColumns = @JoinColumn(name = "agent_id"))
    private List<Agent> evaluates = new ArrayList<>();

    @Transient
    private List<PerformanceKPI> measures;
    private String runName;

    @Transient
    private List<Skill> assess;
    
    private String taskDef;
    private String protocolVersion;
    private String features;
       

    public Benchmark(String id, String metadata, String features, String datasetRef, String taskDef,
            String protocolVersion, List<Agent> evaluates, List<PerformanceKPI> measures, List<Skill> assess,
            String runName) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.metadata = metadata;
        this.features = features;
        this.datasetRef = datasetRef;
        this.taskDef = taskDef;
        this.protocolVersion = protocolVersion;
        this.evaluates = evaluates == null ? new ArrayList<>() : evaluates;
        this.measures = measures;
        this.assess = assess;
        this.runName = runName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // other getters/setters omitted for brevity
    public Benchmark() {
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getDatasetRef() {
        return datasetRef;
    }

    public void setDatasetRef(String datasetRef) {
        this.datasetRef = datasetRef;
    }

    public String getTaskDef() {
        return taskDef;
    }

    public void setTaskDef(String taskDef) {
        this.taskDef = taskDef;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public List<Agent> getEvaluates() {
        return evaluates;
    }

    public void setEvaluates(List<Agent> evaluates) {
        this.evaluates = evaluates;
    }

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public List<PerformanceKPI> getMeasures() {
        return measures;
    }

    public void setMeasures(List<PerformanceKPI> measures) {
        this.measures = measures;
    }

    public List<Skill> getAssess() {
        return assess;
    }

    public void setAssess(List<Skill> assess) {
        this.assess = assess;
    }
}