package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "monitoring_configs")
public class MonitoringConfig {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private boolean enabled;
    private int samplingRate;               // percentuale di eventi da monitorare

    @Transient
    private List<String> trackedEvents;     // eventi da monitorare

    @Transient
    private Map<String, Object> alertRules; // regole per gli avvisi

    private String telemetryEndpoint;       // endpoint per l'invio della telemetria
    private boolean collectPerformanceMetrics; // se raccogliere metriche di performance
    private boolean collectUsageStatistics;  // se raccogliere statistiche di utilizzo
    private int dataRetentionDays;           // giorni di conservazione dei dati

    public MonitoringConfig() {}

    public MonitoringConfig(String id, boolean enabled, int samplingRate, List<String> trackedEvents, Map<String, Object> alertRules, String telemetryEndpoint, boolean collectPerformanceMetrics, boolean collectUsageStatistics, int dataRetentionDays) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.enabled = enabled;
        this.samplingRate = samplingRate;
        this.trackedEvents = trackedEvents;
        this.alertRules = alertRules;
        this.telemetryEndpoint = telemetryEndpoint;
        this.collectPerformanceMetrics = collectPerformanceMetrics;
        this.collectUsageStatistics = collectUsageStatistics;
        this.dataRetentionDays = dataRetentionDays;
    }

    public String id() { return id; }
    public boolean enabled() { return enabled; }
    public int samplingRate() { return samplingRate; }
    public List<String> trackedEvents() { return trackedEvents; }
    public Map<String, Object> alertRules() { return alertRules; }
    public String telemetryEndpoint() { return telemetryEndpoint; }
    public boolean collectPerformanceMetrics() { return collectPerformanceMetrics; }
    public boolean collectUsageStatistics() { return collectUsageStatistics; }
    public int dataRetentionDays() { return dataRetentionDays; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}