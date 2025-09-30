package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * AgentConsumption class for MOSAICO taxonomy.
 * Base class for agent consumption patterns.
 */
@Document(collection = "agentConsumptions")
public record AgentConsumption(
    @Id String id,
    String hyperparameter,
    List<InputParameter> inputParameters,
    List<OutputStructure> outputStructures
) {}