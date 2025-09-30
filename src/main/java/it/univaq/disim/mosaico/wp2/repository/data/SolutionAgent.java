package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * SolutionAgent specialization for MOSAICO taxonomy.
 */
@Document(collection = "solutionAgents")
public record SolutionAgent(
    String id,
    String name,
    String description,
    String version,
    Provider provider,
    String license,
    String promptType
) {}