package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.util.List;

/**
 * Benchmark class for MOSAICO taxonomy.
 * Represents evaluation benchmarks for agents.
 */
@Document(collection = "benchmarks")
public record Benchmark(
    @Id String id,
    String metadata,
    String features,
    String datasetRef,
    String taskDef,
    String protocolVersion,
    @DocumentReference List<Agent> evaluates,
    @DocumentReference List<PerformanceKPI> measures,
    @DocumentReference List<Skill> assess
) {}