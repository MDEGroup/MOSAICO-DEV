package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import it.univaq.disim.mosaico.wp2.repository.data.enums.CollaborationPattern;
import java.util.UUID;

/**
 * CollaborationAgent specialization for MOSAICO taxonomy.
 */
@Entity
@Table(name = "collaboration_agents")
public class CollaborationAgent {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;
    private String version;

    @Transient
    private Provider provider;

    private String license;
    private String beliefs;
    private String intentions;
    private String desires;
    private String role;
    private String objective;

    private CollaborationPattern collaborationPattern;

    public CollaborationAgent() {}

    public CollaborationAgent(String id, String name, String description, String version, Provider provider, String license, String beliefs, String intentions, String desires, String role, String objective, CollaborationPattern collaborationPattern) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.provider = provider;
        this.license = license;
        this.beliefs = beliefs;
        this.intentions = intentions;
        this.desires = desires;
        this.role = role;
        this.objective = objective;
        this.collaborationPattern = collaborationPattern;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public Provider provider() { return provider; }
    public String license() { return license; }
    public String beliefs() { return beliefs; }
    public String intentions() { return intentions; }
    public String desires() { return desires; }
    public String role() { return role; }
    public String objective() { return objective; }
    public CollaborationPattern collaborationPattern() { return collaborationPattern; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}