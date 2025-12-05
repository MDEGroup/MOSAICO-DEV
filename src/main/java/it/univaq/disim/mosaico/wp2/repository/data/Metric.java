package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Optional;
import java.util.UUID;

import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;

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
    private MetricType type;
   

    private String name;
    private Float floatValue;
    private Boolean booleanValue;
    private String stringValue;
    // private Optional<> floatValue;
    private String unit;


    public Metric() {
    }

    public Metric(String id, String name, Float floatValue, Boolean booleanValue, String stringValue, String unit) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.floatValue = floatValue;
        this.booleanValue = booleanValue;
        this.stringValue = stringValue;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Optional<Float> getFloatValue() {
        return Optional.ofNullable(floatValue);
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Optional<Boolean> getBooleanValue() {
        return Optional.ofNullable(booleanValue);
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Optional<String> getStringValue() {
        return Optional.ofNullable(stringValue);
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
     public MetricType getType() {
        return type;
    }
    public void setType(MetricType type) {
        this.type = type;
    }
}