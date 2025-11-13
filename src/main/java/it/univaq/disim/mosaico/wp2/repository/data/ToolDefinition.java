package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Convert;
import it.univaq.disim.mosaico.wp2.repository.converter.JsonAttributeConverter;
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

    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String,Object> config;

    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> examples; // esempi di utilizzo

    private String authenticationMethod;      // metodo di autenticazione richiesto

    public ToolDefinition() {}

    public ToolDefinition(String id, String name, String description, String version, String category, String apiSchema, Map<String,Object> config, List<Map<String, Object>> examples, String authenticationMethod) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.category = category;
        this.apiSchema = apiSchema;
        this.config = config;
        this.examples = examples;
        this.authenticationMethod = authenticationMethod;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public String category() { return category; }
    public String apiSchema() { return apiSchema; }
    public Map<String,Object> config() { return config; }
    public List<Map<String, Object>> examples() { return examples; }
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
    public Map<String,Object> getConfig() { return config; }
    public void setConfig(Map<String,Object> config) { this.config = config; }
    public List<Map<String, Object>> getExamples() { return examples; }
    public void setExamples(List<Map<String, Object>> examples) { this.examples = examples; }
    public String getAuthenticationMethod() { return authenticationMethod; }
    public void setAuthenticationMethod(String authenticationMethod) { this.authenticationMethod = authenticationMethod; }
}
