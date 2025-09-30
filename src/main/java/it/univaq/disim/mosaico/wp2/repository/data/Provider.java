package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Provider class for MOSAICO taxonomy.
 * Represents a provider of agents, tools, or services.
 */
@Document(collection = "providers")
public record Provider(
    @Id String id,
    String name,
    String description,
    String contactUrl
) {}