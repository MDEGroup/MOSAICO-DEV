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
@Table(name = "environments")
public class Environment {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;

    @Transient
    private Map<String, Object> properties;

    @Transient
    private List<Agent> agents;

    @Transient
    private List<Rule> environmentRules;

    public Environment() {}

    public Environment(String id, String name, String description, Map<String, Object> properties, List<Agent> agents, List<Rule> environmentRules) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.properties = properties;
        this.agents = agents;
        this.environmentRules = environmentRules;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public Map<String, Object> properties() { return properties; }
    public List<Agent> agents() { return agents; }
    public List<Rule> environmentRules() { return environmentRules; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}