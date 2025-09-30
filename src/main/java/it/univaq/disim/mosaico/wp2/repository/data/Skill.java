package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Skill class for MOSAICO taxonomy.
 * Represents agent skills with proficiency levels.
 */
@Document(collection = "skills")
public record Skill(
    @Id String id,
    String name,
    String description,
    ProficiencyLevel level,
    LocalDateTime lastEvaluated,
    List<String> softEngTaskIds  // References to SoftEngTask
) {}