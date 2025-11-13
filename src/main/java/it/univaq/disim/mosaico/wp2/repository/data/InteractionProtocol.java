package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA Entity for InteractionProtocol.
 */
@Entity
@Table(name = "interaction_protocols")
public class InteractionProtocol {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String version;

    private String specUrl;

    private String description;

    public InteractionProtocol() {
        // JPA
    }

    public InteractionProtocol(String id, String name, String version, String specUrl, String description) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.version = version;
        this.specUrl = specUrl;
        this.description = description;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String version() { return version; }
    public String specUrl() { return specUrl; }
    public String description() { return description; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getSpecUrl() { return specUrl; }
    public void setSpecUrl(String specUrl) { this.specUrl = specUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}