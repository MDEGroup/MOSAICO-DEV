package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private float value;
    private String unit;

    public Metric() {}

    public Metric(String id, String name, float value, String unit) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    public String id() { return id; }
    public String name() { return name; }
    public float value() { return value; }
    public String unit() { return unit; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}