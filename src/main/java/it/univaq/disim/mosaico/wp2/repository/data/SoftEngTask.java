package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ArtifactType;
import java.util.List;

/**
 * SoftEngTask class for MOSAICO taxonomy.
 * Represents software engineering tasks categorized by SWEBOK.
 */
@Entity
@Table(name = "soft_eng_tasks")
public class SoftEngTask {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private SwebokKAId primaryKA;

    @ElementCollection
    private List<SwebokKAId> secondaryKAs;

    @ElementCollection
    private List<ArtifactType> inputTypes;

    @ElementCollection
    private List<ArtifactType> outputTypes;

    @ElementCollection
    private List<String> supportedNL;

    @ElementCollection
    private List<String> supportedPL;

    public SoftEngTask() {}

    public SoftEngTask(String id, String name, String description, SwebokKAId primaryKA, List<SwebokKAId> secondaryKAs, List<ArtifactType> inputTypes, List<ArtifactType> outputTypes, List<String> supportedNL, List<String> supportedPL) {
        this.id = (id == null) ? java.util.UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.primaryKA = primaryKA;
        this.secondaryKAs = secondaryKAs;
        this.inputTypes = inputTypes;
        this.outputTypes = outputTypes;
        this.supportedNL = supportedNL;
        this.supportedPL = supportedPL;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public SwebokKAId primaryKA() { return primaryKA; }
    public List<SwebokKAId> secondaryKAs() { return secondaryKAs; }
    public List<ArtifactType> inputTypes() { return inputTypes; }
    public List<ArtifactType> outputTypes() { return outputTypes; }
    public List<String> supportedNL() { return supportedNL; }
    public List<String> supportedPL() { return supportedPL; }

    // getters/setters omitted for brevity (generated below)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public SwebokKAId getPrimaryKA() { return primaryKA; }
    public void setPrimaryKA(SwebokKAId primaryKA) { this.primaryKA = primaryKA; }
    public List<SwebokKAId> getSecondaryKAs() { return secondaryKAs; }
    public void setSecondaryKAs(List<SwebokKAId> secondaryKAs) { this.secondaryKAs = secondaryKAs; }
    public List<ArtifactType> getInputTypes() { return inputTypes; }
    public void setInputTypes(List<ArtifactType> inputTypes) { this.inputTypes = inputTypes; }
    public List<ArtifactType> getOutputTypes() { return outputTypes; }
    public void setOutputTypes(List<ArtifactType> outputTypes) { this.outputTypes = outputTypes; }
    public List<String> getSupportedNL() { return supportedNL; }
    public void setSupportedNL(List<String> supportedNL) { this.supportedNL = supportedNL; }
    public List<String> getSupportedPL() { return supportedPL; }
    public void setSupportedPL(List<String> supportedPL) { this.supportedPL = supportedPL; }
}