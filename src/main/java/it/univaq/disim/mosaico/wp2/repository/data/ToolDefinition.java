package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "toolDefinitions")
public record ToolDefinition(
    @Id String id,
    String name,
    String description,
    String version,
    String category,                 // es. "search", "calculation", "external-api"
    String apiSchema,                // schema dell'API, in formato JSON o YAML
    Map<String, Object> config,      // configurazione dello strumento
    List<Map<String, Object>> examples, // esempi di utilizzo
    String authenticationMethod      // metodo di autenticazione richiesto
) {}