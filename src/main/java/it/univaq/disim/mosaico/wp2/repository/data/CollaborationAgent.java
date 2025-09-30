package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.mongodb.core.mapping.Document;
import it.univaq.disim.mosaico.wp2.repository.data.enums.CollaborationPattern;

/**
 * CollaborationAgent specialization for MOSAICO taxonomy.
 */
@Document(collection = "collaborationAgents")
public record CollaborationAgent(
    String id,
    String name,
    String description,
    String version,
    Provider provider,
    String license,
    String beliefs,
    String intentions,
    String desires,
    String role,
    String objective,
    CollaborationPattern collaborationPattern
) {}