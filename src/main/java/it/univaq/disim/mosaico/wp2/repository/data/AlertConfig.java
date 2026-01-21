package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.AlertCondition;
import it.univaq.disim.mosaico.wp2.repository.data.enums.NotificationChannel;
import it.univaq.disim.mosaico.wp2.repository.data.enums.Severity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing alert configuration for a benchmark KPI.
 * Defines when alerts should be triggered and how notifications should be sent.
 */
@Entity
@Table(name = "alert_configs", indexes = {
    @Index(name = "idx_alert_config_benchmark_id", columnList = "benchmark_id"),
    @Index(name = "idx_alert_config_kpi_name", columnList = "kpi_name"),
    @Index(name = "idx_alert_config_enabled", columnList = "enabled")
})
public class AlertConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "benchmark_id")
    private String benchmarkId;

    @Column(name = "agent_id")
    private String agentId;

    @Column(name = "kpi_name", nullable = false)
    private String kpiName;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private AlertCondition condition;

    @Column(name = "threshold_value", nullable = false)
    private Double threshold;

    @Column(name = "comparison_window_hours")
    private Integer comparisonWindowHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @ElementCollection
    @CollectionTable(name = "alert_config_channels", joinColumns = @JoinColumn(name = "alert_config_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private List<NotificationChannel> channels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "alert_config_recipients", joinColumns = @JoinColumn(name = "alert_config_id"))
    @Column(name = "recipient")
    private List<String> recipients = new ArrayList<>();

    @Column(name = "webhook_url")
    private String webhookUrl;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "cooldown_minutes")
    private Integer cooldownMinutes;

    @Column(name = "last_triggered_at")
    private Instant lastTriggeredAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // JPA
    public AlertConfig() {
    }

    public AlertConfig(String name, String kpiName, AlertCondition condition, Double threshold, Severity severity) {
        this.name = name;
        this.kpiName = kpiName;
        this.condition = condition;
        this.threshold = threshold;
        this.severity = severity;
        this.enabled = true;
        this.cooldownMinutes = 60;
        this.createdAt = Instant.now();
    }

    // Record-style accessors
    public String id() { return id; }
    public String name() { return name; }
    public String kpiName() { return kpiName; }
    public AlertCondition condition() { return condition; }
    public Double threshold() { return threshold; }
    public Severity severity() { return severity; }
    public Boolean enabled() { return enabled; }

    // Standard getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(String benchmarkId) { this.benchmarkId = benchmarkId; }

    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }

    public String getKpiName() { return kpiName; }
    public void setKpiName(String kpiName) { this.kpiName = kpiName; }

    public AlertCondition getCondition() { return condition; }
    public void setCondition(AlertCondition condition) { this.condition = condition; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public Integer getComparisonWindowHours() { return comparisonWindowHours; }
    public void setComparisonWindowHours(Integer comparisonWindowHours) { this.comparisonWindowHours = comparisonWindowHours; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public List<NotificationChannel> getChannels() { return channels; }
    public void setChannels(List<NotificationChannel> channels) { this.channels = channels; }

    public List<String> getRecipients() { return recipients; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getCooldownMinutes() { return cooldownMinutes; }
    public void setCooldownMinutes(Integer cooldownMinutes) { this.cooldownMinutes = cooldownMinutes; }

    public Instant getLastTriggeredAt() { return lastTriggeredAt; }
    public void setLastTriggeredAt(Instant lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean isInCooldown() {
        if (lastTriggeredAt == null || cooldownMinutes == null) {
            return false;
        }
        Instant cooldownEnd = lastTriggeredAt.plusSeconds(cooldownMinutes * 60L);
        return Instant.now().isBefore(cooldownEnd);
    }

    public void markTriggered() {
        this.lastTriggeredAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
