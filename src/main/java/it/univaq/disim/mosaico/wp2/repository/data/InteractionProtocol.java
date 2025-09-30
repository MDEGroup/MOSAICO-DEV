package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * InteractionProtocol class for MOSAICO taxonomy.
 * Represents communication protocols between agents.
 */
@Document(collection = "interactionProtocols")
public record InteractionProtocol(
    @Id String id,
    String name,
    String version,
    String specUrl,
    String description
) {}