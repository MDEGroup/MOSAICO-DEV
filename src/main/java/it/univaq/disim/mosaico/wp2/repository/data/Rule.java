package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Rule class for MOSAICO taxonomy.
 * Represents environment rules and constraints.
 */
@Document(collection = "rules")
public record Rule(
    @Id String id,
    String name,
    String description,
    String condition,
    String action,
    boolean enabled
) {}