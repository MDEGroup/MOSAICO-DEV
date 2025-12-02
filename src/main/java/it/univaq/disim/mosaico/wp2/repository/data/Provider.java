package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA Entity for Provider.
 */
@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String contactUrl;

    public Provider() {
        // JPA
    }

    public Provider(String name, String description, String contactUrl) {
        this.name = name;
        this.description = description;
        this.contactUrl = contactUrl;
    }

    // Record-style accessors (for compatibility with existing code that uses provider.name())
    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String contactUrl() { return contactUrl; }

    // Standard getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContactUrl() { return contactUrl; }
    public void setContactUrl(String contactUrl) { this.contactUrl = contactUrl; }
}