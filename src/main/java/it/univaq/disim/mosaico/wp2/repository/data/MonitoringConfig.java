package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "monitoringConfigs")
public record MonitoringConfig(
    @Id String id,
    boolean enabled,
    int samplingRate,               // percentuale di eventi da monitorare
    List<String> trackedEvents,     // eventi da monitorare
    Map<String, Object> alertRules, // regole per gli avvisi
    String telemetryEndpoint,       // endpoint per l'invio della telemetria
    boolean collectPerformanceMetrics, // se raccogliere metriche di performance
    boolean collectUsageStatistics,  // se raccogliere statistiche di utilizzo
    int dataRetentionDays           // giorni di conservazione dei dati
) {}