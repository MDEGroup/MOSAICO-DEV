package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AgentUsage class for MOSAICO taxonomy.
 * Represents agent usage tracking.
 */
@Document(collection = "agentUsages")
public record AgentUsage(
    @Id String id,
    LocalDateTime timestamp,
    long durationMs,
    @DocumentReference Agent agent,
    @DocumentReference AgentConsumption via,
    @DocumentReference List<HumanFeedback> feedback,
    UsageBreakdown details,
    List<MonetaryCharge> incurs
) {}