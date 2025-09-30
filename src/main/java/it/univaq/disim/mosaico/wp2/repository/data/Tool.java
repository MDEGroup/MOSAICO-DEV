// filepath: /Users/juridirocco/development/MOSAICO/repository/src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Tool.java
package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Tool class for MOSAICO taxonomy.
 * Updated to align with PUML model.
 */
@Document(collection = "tools")
public record Tool(
    @Id String id,
    String name,
    String description,
    String authMethod,
    String scopes,
    String quotaLimit,
    String rateLimitPolicy
) {}