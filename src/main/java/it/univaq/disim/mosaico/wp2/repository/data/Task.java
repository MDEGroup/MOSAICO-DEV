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
@Table(name = "tasks")
public class Task {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;

    @Transient
    private List<Tool> requiredTools;

    @Transient
    private Map<String, Object> parameters;

    private String precondition;
    private String postcondition;
    private int estimatedDuration;

    public Task() {}

    public Task(String id, String name, String description, List<Tool> requiredTools, Map<String, Object> parameters, String precondition, String postcondition, int estimatedDuration) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.requiredTools = requiredTools;
        this.parameters = parameters;
        this.precondition = precondition;
        this.postcondition = postcondition;
        this.estimatedDuration = estimatedDuration;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public List<Tool> requiredTools() { return requiredTools; }
    public Map<String, Object> parameters() { return parameters; }
    public String precondition() { return precondition; }
    public String postcondition() { return postcondition; }
    public int estimatedDuration() { return estimatedDuration; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    // other getters/setters omitted for brevity
}
