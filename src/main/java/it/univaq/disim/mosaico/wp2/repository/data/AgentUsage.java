package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AgentUsage class for MOSAICO taxonomy.
 * Represents agent usage tracking.
 */
@Entity
@Table(name = "agent_usages")
public class AgentUsage {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private LocalDateTime timestamp;
    private long durationMs;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Transient
    private AgentConsumption via;

    @Transient
    private List<HumanFeedback> feedback;

    @Transient
    private UsageBreakdown details;

    @Transient
    private List<MonetaryCharge> incurs;

    public AgentUsage() {}

    public AgentUsage(String id, LocalDateTime timestamp, long durationMs, Agent agent, AgentConsumption via, List<HumanFeedback> feedback, UsageBreakdown details, List<MonetaryCharge> incurs) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.timestamp = timestamp;
        this.durationMs = durationMs;
    this.agent = agent;
        this.via = via;
        this.feedback = feedback;
        this.details = details;
        this.incurs = incurs;
    }

    public String id() { return id; }
    public LocalDateTime timestamp() { return timestamp; }
    public long durationMs() { return durationMs; }
    public Agent agent() { return agent; }
    public AgentConsumption via() { return via; }
    public List<HumanFeedback> feedback() { return feedback; }
    public UsageBreakdown details() { return details; }
    public List<MonetaryCharge> incurs() { return incurs; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}