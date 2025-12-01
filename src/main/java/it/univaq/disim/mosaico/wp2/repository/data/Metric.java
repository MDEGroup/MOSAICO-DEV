package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Metric class for MOSAICO taxonomy.
 * Updated to align with PUML model.
 */
@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private float metric_value;
    private String unit;

    public Metric() {
    }

    public Metric(String id, String name, float value, String unit) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.metric_value = value;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMetric_value() {
        return metric_value;
    }

    public void setMetric_value(float metric_value) {
        this.metric_value = metric_value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}