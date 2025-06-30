package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "coordinationPatterns")
public record CoordinationPattern(
    @Id String id,
    String name,
    String description,
    String patternType,              // es. "orchestrator", "hierarchical", "peer-to-peer"
    List<String> participatingAgents, // agenti coinvolti
    Map<String, Object> roles,       // ruoli degli agenti
    String flowDefinition,           // definizione del flusso (JSON o YAML)
    List<String> rules,               // regole del pattern di coordinazione
    int complexityLevel,
    List<String> supportedAgentTypes,
    List<String> supportedTools, // strumenti supportati
    String communicationProtocol, // protocollo di comunicazione utilizzato
    List<String> domains
) {}