package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import it.univaq.disim.mosaico.wp2.repository.data.enums.FeedbackKind;

/**
 * HumanFeedback class for MOSAICO taxonomy.
 * Represents human feedback on agent performance.
 */
@Document(collection = "humanFeedbacks")
public record HumanFeedback(
    @Id String id,
    FeedbackKind kind,
    String source,
    String rationale
) {}