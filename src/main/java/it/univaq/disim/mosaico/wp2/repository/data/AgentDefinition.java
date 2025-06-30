package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "agentDefinitions")
public record AgentDefinition(
    @Id String id,
    String name,
    String description,
    String type,                     // es. "LLM", "ReAct", "Reflexion"
    Map<String, Object> config,      // configurazione specifica dell'agente
    List<String> capabilities,       // capacità dell'agente
    Map<String, Object> parameters,  // parametri configurabili
    List<String> requiredTools,      // strumenti richiesti dall'agente
    String initializationScript,      // codice per inizializzare l'agente
    int complexityLevel
) {}