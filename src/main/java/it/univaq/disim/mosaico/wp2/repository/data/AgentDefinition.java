package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "agent_definitions")
public class AgentDefinition {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String type;

    @Transient
    private Map<String,Object> config;

    @Transient
    private List<String> capabilities;

    @Transient
    private Map<String,Object> parameters;

    @Transient
    private List<String> requiredTools;

    private String initializationScript;

    private int complexityLevel;

    public AgentDefinition() {}

    public AgentDefinition(String id, String name, String description, String type, Map<String,Object> config, List<String> capabilities, Map<String,Object> parameters, List<String> requiredTools, String initializationScript, int complexityLevel) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.config = config;
        this.capabilities = capabilities;
        this.parameters = parameters;
        this.requiredTools = requiredTools;
        this.initializationScript = initializationScript;
        this.complexityLevel = complexityLevel;
    }



    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String,Object> getConfig() { return config; }
    public void setConfig(Map<String,Object> config) { this.config = config; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    public Map<String,Object> getParameters() { return parameters; }
    public void setParameters(Map<String,Object> parameters) { this.parameters = parameters; }
    public List<String> getRequiredTools() { return requiredTools; }
    public void setRequiredTools(List<String> requiredTools) { this.requiredTools = requiredTools; }
    public String getInitializationScript() { return initializationScript; }
    public void setInitializationScript(String initializationScript) { this.initializationScript = initializationScript; }
    public int getComplexityLevel() { return complexityLevel; }
    public void setComplexityLevel(int complexityLevel) { this.complexityLevel = complexityLevel; }
}