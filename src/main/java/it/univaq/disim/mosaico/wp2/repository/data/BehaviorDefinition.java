package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "behaviorDefinitions")
public record BehaviorDefinition(
    @Id String id,
    String name,
    String description,
    String type,                     // es. "reactive", "deliberative", "hybrid"
    List<Map<String, Object>> goals, // obiettivi del comportamento
    Map<String, Object> triggers,    // eventi che attivano il comportamento
    String algorithmDescription,     // descrizione dell'algoritmo
    Map<String, Object> parameters,  // parametri configurabili
    List<String> requiredCapabilities // capacità richieste per implementare il comportamento
) {}