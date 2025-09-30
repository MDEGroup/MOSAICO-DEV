package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ArtifactType;
import java.util.List;

/**
 * SoftEngTask class for MOSAICO taxonomy.
 * Represents software engineering tasks categorized by SWEBOK.
 */
@Document(collection = "softEngTasks")
public record SoftEngTask(
    @Id String id,
    String name,
    String description,
    SwebokKAId primaryKA,
    List<SwebokKAId> secondaryKAs,
    List<ArtifactType> inputTypes,
    List<ArtifactType> outputTypes,
    List<String> supportedNL,        // Natural Languages
    List<String> supportedPL         // Programming Languages
) {}