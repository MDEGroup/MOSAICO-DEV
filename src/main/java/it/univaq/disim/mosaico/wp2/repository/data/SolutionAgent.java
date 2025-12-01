package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.UUID;

/**
 * SolutionAgent specialization for MOSAICO taxonomy.
 */
@Entity
@Table(name = "solution_agents")
public class SolutionAgent {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String version;

    @Transient
    private Provider provider;

    private String license;

    private String promptType;

    public SolutionAgent() {}

    public SolutionAgent(String id, String name, String description, String version, Provider provider, String license, String promptType) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.provider = provider;
        this.license = license;
        this.promptType = promptType;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public Provider provider() { return provider; }
    public String license() { return license; }
    public String promptType() { return promptType; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }
    public String getPromptType() { return promptType; }
    public void setPromptType(String promptType) { this.promptType = promptType; }
}