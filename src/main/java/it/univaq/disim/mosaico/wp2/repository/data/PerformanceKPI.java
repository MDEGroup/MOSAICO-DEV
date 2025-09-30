package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.util.List;

/**
 * PerformanceKPI class for MOSAICO taxonomy.
 * Represents performance key performance indicators.
 */
@Document(collection = "performanceKPIs")
public record PerformanceKPI(
    @Id String id,
    String description,
    @DocumentReference List<Metric> includes
) {}