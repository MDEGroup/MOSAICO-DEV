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
@Table(name = "behavior_definitions")
public class BehaviorDefinition {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;
    private String type;                     // es. "reactive", "deliberative", "hybrid"

    @Transient
    private List<Map<String, Object>> goals; // obiettivi del comportamento

    @Transient
    private Map<String, Object> triggers;    // eventi che attivano il comportamento

    private String algorithmDescription;     // descrizione dell'algoritmo

    @Transient
    private Map<String, Object> parameters;  // parametri configurabili

    @Transient
    private List<String> requiredCapabilities; // capacità richieste per implementare il comportamento

    public BehaviorDefinition() {}

    public BehaviorDefinition(String id, String name, String description, String type, List<Map<String, Object>> goals, Map<String, Object> triggers, String algorithmDescription, Map<String, Object> parameters, List<String> requiredCapabilities) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.goals = goals;
        this.triggers = triggers;
        this.algorithmDescription = algorithmDescription;
        this.parameters = parameters;
        this.requiredCapabilities = requiredCapabilities;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String type() { return type; }
    public List<Map<String, Object>> goals() { return goals; }
    public Map<String, Object> triggers() { return triggers; }
    public String algorithmDescription() { return algorithmDescription; }
    public Map<String, Object> parameters() { return parameters; }
    public List<String> requiredCapabilities() { return requiredCapabilities; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}