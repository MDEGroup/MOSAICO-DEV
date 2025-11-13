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
@Table(name = "coordination_patterns")
public class CoordinationPattern {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;
    private String patternType;              // es. "orchestrator", "hierarchical", "peer-to-peer"

    @Transient
    private List<String> participatingAgents; // agenti coinvolti

    @Transient
    private Map<String, Object> roles;       // ruoli degli agenti

    private String flowDefinition;           // definizione del flusso (JSON o YAML)

    @Transient
    private List<String> rules;               // regole del pattern di coordinazione

    private int complexityLevel;

    @Transient
    private List<String> supportedAgentTypes;

    @Transient
    private List<String> supportedTools; // strumenti supportati

    private String communicationProtocol; // protocollo di comunicazione utilizzato

    @Transient
    private List<String> domains;

    public CoordinationPattern() {}

    public CoordinationPattern(String id, String name, String description, String patternType, List<String> participatingAgents, Map<String, Object> roles, String flowDefinition, List<String> rules, int complexityLevel, List<String> supportedAgentTypes, List<String> supportedTools, String communicationProtocol, List<String> domains) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.patternType = patternType;
        this.participatingAgents = participatingAgents;
        this.roles = roles;
        this.flowDefinition = flowDefinition;
        this.rules = rules;
        this.complexityLevel = complexityLevel;
        this.supportedAgentTypes = supportedAgentTypes;
        this.supportedTools = supportedTools;
        this.communicationProtocol = communicationProtocol;
        this.domains = domains;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String patternType() { return patternType; }
    public List<String> participatingAgents() { return participatingAgents; }
    public Map<String, Object> roles() { return roles; }
    public String flowDefinition() { return flowDefinition; }
    public List<String> rules() { return rules; }
    public int complexityLevel() { return complexityLevel; }
    public List<String> supportedAgentTypes() { return supportedAgentTypes; }
    public List<String> supportedTools() { return supportedTools; }
    public String communicationProtocol() { return communicationProtocol; }
    public List<String> domains() { return domains; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}