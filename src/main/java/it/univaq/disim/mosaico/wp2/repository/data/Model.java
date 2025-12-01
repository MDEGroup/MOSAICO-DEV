package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "models")
public class Model {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;
    private String version;
    private String author;
    private String license;

    // Metadati simili a Hugging Face
    private String modelType;              // es. "text-generation", "image-classification", "multi-agent-system"
    @Transient
    private List<String> tags;             // es. ["collaborative", "reinforcement-learning", "autonomous"]
    @ElementCollection
    @CollectionTable(name = "model_config_entries", joinColumns = @JoinColumn(name = "model_id"))
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value")
    private Map<String, String> config = new HashMap<>();    // configurazione principale del modello

    // Architettura
    private String architecture;           // descrizione dell'architettura (simile a "modelArchitecture" in HF)
    @Transient
    private List<AgentDefinition> agents;  // definizioni degli agenti (simile a "components" in HF)
    @Transient
    private List<ToolDefinition> tools;    // strumenti disponibili

    // Governance e comunicazione (simile al sistema di pipeline in HF)
    @Transient
    private List<CoordinationPattern> coordinationPatterns;
    @Transient
    private List<CommunicationProtocol> communicationProtocols;

    // Monitoraggio e telemetria (ispirato alla telemetria di HF)
    @Transient
    private MonitoringConfig monitoringConfig;

    // Comportamento (simile al concetto di "task" e "usage" in HF)
        @ManyToMany
        @JoinTable(name = "model_behaviors",
            joinColumns = @JoinColumn(name = "model_id"),
            inverseJoinColumns = @JoinColumn(name = "behavior_id"))
    private List<BehaviorDefinition> behaviors = new ArrayList<>();

    // Metriche di performance (simile a "metrics" in HF)
    @Transient
    private List<Metric> metrics;

    // Informazioni di utilizzo (simile a HF Model Card)
    private String trainingFramework;      // es. "MOSAICO MAS Framework v1.2"
    private String trainingCompute;        // es. "5 distributed nodes, 48 hours"
    @Transient
    private List<String> limitations;      // limitazioni conosciute
    @Transient
    private List<String> useCases;         // casi d'uso consigliati

    // Informazioni di download e distribuzione
    private long downloadCount;
    private String repositoryUrl;
    private Instant lastUpdated;
    private Instant createdAt;

    public Model() {}

    public Model(String id, String name, String description, String version, String author, String license, String modelType, List<String> tags, Map<String, ?> config, String architecture, List<AgentDefinition> agents, List<ToolDefinition> tools, List<CoordinationPattern> coordinationPatterns, List<CommunicationProtocol> communicationProtocols, MonitoringConfig monitoringConfig, List<BehaviorDefinition> behaviors, List<Metric> metrics, String trainingFramework, String trainingCompute, List<String> limitations, List<String> useCases, long downloadCount, String repositoryUrl, Instant lastUpdated, Instant createdAt) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.author = author;
        this.license = license;
        this.modelType = modelType;
        this.tags = tags;
        if (config != null) {
            config.forEach((key, value) -> this.config.put(key, value == null ? null : value.toString()));
        }
        this.architecture = architecture;
        this.agents = agents;
        this.tools = tools;
        this.coordinationPatterns = coordinationPatterns;
        this.communicationProtocols = communicationProtocols;
        this.monitoringConfig = monitoringConfig;
        if (behaviors != null) {
            this.behaviors.addAll(behaviors);
        }
        this.metrics = metrics;
        this.trainingFramework = trainingFramework;
        this.trainingCompute = trainingCompute;
        this.limitations = limitations;
        this.useCases = useCases;
        this.downloadCount = downloadCount;
        this.repositoryUrl = repositoryUrl;
        this.lastUpdated = lastUpdated;
        this.createdAt = createdAt;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public String author() { return author; }
    public String license() { return license; }
    public String modelType() { return modelType; }
    public List<String> tags() { return tags; }
    public Map<String, String> config() { return config; }
    public String architecture() { return architecture; }
    public List<AgentDefinition> agents() { return agents; }
    public List<ToolDefinition> tools() { return tools; }
    public List<CoordinationPattern> coordinationPatterns() { return coordinationPatterns; }
    public List<CommunicationProtocol> communicationProtocols() { return communicationProtocols; }
    public MonitoringConfig monitoringConfig() { return monitoringConfig; }
    public List<BehaviorDefinition> behaviors() { return behaviors; }
    public List<Metric> metrics() { return metrics; }
    public String trainingFramework() { return trainingFramework; }
    public String trainingCompute() { return trainingCompute; }
    public List<String> limitations() { return limitations; }
    public List<String> useCases() { return useCases; }
    public long downloadCount() { return downloadCount; }
    public String repositoryUrl() { return repositoryUrl; }
    public Instant lastUpdated() { return lastUpdated; }
    public Instant createdAt() { return createdAt; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    // getters/setters omitted for brevity (can be generated if needed)
}
