package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Rule class for MOSAICO taxonomy.
 * Represents environment rules and constraints.
 */
@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String condition;

    private String action;

    private boolean enabled;

    public Rule() {}

    public Rule(String id, String name, String description, String condition, String action, boolean enabled) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.condition = condition;
        this.action = action;
        this.enabled = enabled;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String condition() { return condition; }
    public String action() { return action; }
    public boolean enabled() { return enabled; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}