package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import java.util.List;

/**
 * Agent class for MOSAICO taxonomy.
 * Core agent representation following the PUML model.
 */
@Document(collection = "agents")
public record Agent(
    @Id String id,
    String name,
    String description,
    String version,
    @DocumentReference Provider provider,
    String license,
    String beliefs,
    String intentions,
    String desires,
    String role,
    String objective,
    List<IOModality> ioModalities,
    String backStory,
    @DocumentReference List<Skill> skills,
    @DocumentReference List<Tool> exploits,
    @DocumentReference List<Memory> has,
    @DocumentReference List<InteractionProtocol> supports,
    @DocumentReference List<AgentConsumption> consumptions
) {}