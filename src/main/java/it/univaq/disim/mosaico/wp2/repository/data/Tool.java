package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * JPA Entity for Tool.
 */
@Entity
@Table(name = "tools")
public class Tool {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String authMethod;

    private String scopes;

    private String quotaLimit;

    private String rateLimitPolicy;

    public Tool() {
        // JPA
    }

    public Tool(String id, String name, String description, String authMethod, String scopes, String quotaLimit, String rateLimitPolicy) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.authMethod = authMethod;
        this.scopes = scopes;
        this.quotaLimit = quotaLimit;
        this.rateLimitPolicy = rateLimitPolicy;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String authMethod() { return authMethod; }
    public String scopes() { return scopes; }
    public String quotaLimit() { return quotaLimit; }
    public String rateLimitPolicy() { return rateLimitPolicy; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthMethod() { return authMethod; }
    public void setAuthMethod(String authMethod) { this.authMethod = authMethod; }
    public String getScopes() { return scopes; }
    public void setScopes(String scopes) { this.scopes = scopes; }
    public String getQuotaLimit() { return quotaLimit; }
    public void setQuotaLimit(String quotaLimit) { this.quotaLimit = quotaLimit; }
    public String getRateLimitPolicy() { return rateLimitPolicy; }
    public void setRateLimitPolicy(String rateLimitPolicy) { this.rateLimitPolicy = rateLimitPolicy; }
}
