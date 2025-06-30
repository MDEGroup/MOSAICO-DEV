package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "metrics")
public record Metric(
    @Id String id,
    String name,
    String description,
    String category,                // es. "performance", "accuracy", "resource-usage"
    String unit,                    // unità di misura
    double value,                   // valore della metrica
    Map<String, Object> dimensions, // dimensioni per la metrica (es. agente, operazione)
    Instant timestamp,              // timestamp della misurazione
    String source                   // origine della metrica
) {}