package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;

/**
 * Memory class for MOSAICO taxonomy.
 * Represents agent memory configurations.
 */
@Document(collection = "memories")
public record Memory(
    @Id String id,
    MemoryType type,
    MemoryScope scope,
    String db
) {}