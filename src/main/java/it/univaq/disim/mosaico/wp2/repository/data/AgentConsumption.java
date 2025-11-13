package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity for AgentConsumption. Nested parameter lists are kept transient for now
 * and can be migrated to embeddables later.
 */
@Entity
@Table(name = "agent_consumptions")
public class AgentConsumption {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String hyperparameter;

    @Transient
    private List<InputParameter> inputParameters;

    @Transient
    private List<OutputStructure> outputStructures;

    public AgentConsumption() {
        // JPA
    }

    public AgentConsumption(String id, String hyperparameter, List<InputParameter> inputParameters, List<OutputStructure> outputStructures) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.hyperparameter = hyperparameter;
        this.inputParameters = inputParameters;
        this.outputStructures = outputStructures;
    }

    public String id() { return id; }
    public String hyperparameter() { return hyperparameter; }
    public List<InputParameter> inputParameters() { return inputParameters; }
    public List<OutputStructure> outputStructures() { return outputStructures; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHyperparameter() { return hyperparameter; }
    public void setHyperparameter(String hyperparameter) { this.hyperparameter = hyperparameter; }
    public List<InputParameter> getInputParameters() { return inputParameters; }
    public void setInputParameters(List<InputParameter> inputParameters) { this.inputParameters = inputParameters; }
    public List<OutputStructure> getOutputStructures() { return outputStructures; }
    public void setOutputStructures(List<OutputStructure> outputStructures) { this.outputStructures = outputStructures; }
}