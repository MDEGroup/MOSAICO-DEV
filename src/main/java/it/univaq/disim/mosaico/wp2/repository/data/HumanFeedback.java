package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import it.univaq.disim.mosaico.wp2.repository.data.enums.FeedbackKind;
import java.util.UUID;

/**
 * HumanFeedback class for MOSAICO taxonomy.
 * Represents human feedback on agent performance.
 */
@Entity
@Table(name = "human_feedbacks")
public class HumanFeedback {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private FeedbackKind kind;
    private String source;
    private String rationale;

    public HumanFeedback() {}

    public HumanFeedback(String id, FeedbackKind kind, String source, String rationale) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.kind = kind;
        this.source = source;
        this.rationale = rationale;
    }

    public String id() { return id; }
    public FeedbackKind kind() { return kind; }
    public String source() { return source; }
    public String rationale() { return rationale; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}