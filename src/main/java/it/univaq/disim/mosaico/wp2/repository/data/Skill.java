package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for Skill.
 */
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProficiencyLevel level;

    private LocalDateTime lastEvaluated;

    @ElementCollection
    private List<String> softEngTaskIds;

    public Skill() {
        // JPA
    }

    public Skill(String id, String name, String description, ProficiencyLevel level, LocalDateTime lastEvaluated, List<String> softEngTaskIds) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.level = level;
        this.lastEvaluated = lastEvaluated;
        this.softEngTaskIds = softEngTaskIds;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public ProficiencyLevel level() { return level; }
    public LocalDateTime lastEvaluated() { return lastEvaluated; }
    public List<String> softEngTaskIds() { return softEngTaskIds; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProficiencyLevel getLevel() { return level; }
    public void setLevel(ProficiencyLevel level) { this.level = level; }
    public LocalDateTime getLastEvaluated() { return lastEvaluated; }
    public void setLastEvaluated(LocalDateTime lastEvaluated) { this.lastEvaluated = lastEvaluated; }
    public List<String> getSoftEngTaskIds() { return softEngTaskIds; }
    public void setSoftEngTaskIds(List<String> softEngTaskIds) { this.softEngTaskIds = softEngTaskIds; }
}