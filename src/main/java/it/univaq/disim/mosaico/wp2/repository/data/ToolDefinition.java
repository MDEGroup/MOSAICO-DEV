package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tool_definitions")
public class ToolDefinition {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    private String version;

    private String category;                 // es. "search", "calculation", "external-api"
    
    private String apiSchema;                // schema dell'API, in formato JSON o YAML

    @ElementCollection
    @CollectionTable(name = "tool_config_entries", joinColumns = @JoinColumn(name = "tool_definition_id"))
    @MapKeyColumn(name = "config_key")
    @Column(name = "config_value")
    private Map<String,String> config = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "tool_examples", joinColumns = @JoinColumn(name = "tool_definition_id"))
    @Column(name = "example_payload")
    private List<String> examples = new ArrayList<>(); // esempi di utilizzo

    private String authenticationMethod;      // metodo di autenticazione richiesto

    public ToolDefinition() {}

    public ToolDefinition(String id, String name, String description, String version, String category, String apiSchema, Map<String,?> config, List<String> examples, String authenticationMethod) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.category = category;
        this.apiSchema = apiSchema;
        if (config != null) {
            config.forEach((key, value) -> this.config.put(key, value == null ? null : value.toString()));
        }
        if (examples != null) {
            this.examples.addAll(examples);
        }
        this.authenticationMethod = authenticationMethod;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public String category() { return category; }
    public String apiSchema() { return apiSchema; }
    public Map<String,String> config() { return config; }
    public List<String> examples() { return examples; }
    public String authenticationMethod() { return authenticationMethod; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getApiSchema() { return apiSchema; }
    public void setApiSchema(String apiSchema) { this.apiSchema = apiSchema; }
    public Map<String,String> getConfig() { return config; }
    public void setConfig(Map<String,?> config) {
        this.config.clear();
        if (config != null) {
            config.forEach((key, value) -> this.config.put(key, value == null ? null : value.toString()));
        }
    }
    public List<String> getExamples() { return examples; }
    public void setExamples(List<String> examples) {
        this.examples.clear();
        if (examples != null) {
            this.examples.addAll(examples);
        }
    }
    public String getAuthenticationMethod() { return authenticationMethod; }
    public void setAuthenticationMethod(String authenticationMethod) { this.authenticationMethod = authenticationMethod; }
}
