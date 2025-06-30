package it.univaq.disim.mosaico.wp2.repository.data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "models")
public record Model(
    @Id String id, 
    String name, 
    String description, 
    String version,
    String author,
    String license,
    
    // Metadati simili a Hugging Face
    String modelType,              // es. "text-generation", "image-classification", "multi-agent-system"
    List<String> tags,             // es. ["collaborative", "reinforcement-learning", "autonomous"]
    Map<String, Object> config,    // configurazione principale del modello
    
    // Architettura
    String architecture,           // descrizione dell'architettura (simile a "modelArchitecture" in HF)
    List<AgentDefinition> agents,  // definizioni degli agenti (simile a "components" in HF)
    List<ToolDefinition> tools,    // strumenti disponibili
    
    // Governance e comunicazione (simile al sistema di pipeline in HF)
    List<CoordinationPattern> coordinationPatterns,
    List<CommunicationProtocol> communicationProtocols,
    
    // Monitoraggio e telemetria (ispirato alla telemetria di HF)
    MonitoringConfig monitoringConfig,
    
    // Comportamento (simile al concetto di "task" e "usage" in HF)
    Map<String, BehaviorDefinition> behaviors,
    
    // Metriche di performance (simile a "metrics" in HF)
    List<Metric> metrics,
    
    // Informazioni di utilizzo (simile a HF Model Card)
    String trainingFramework,      // es. "MOSAICO MAS Framework v1.2"
    String trainingCompute,        // es. "5 distributed nodes, 48 hours"
    List<String> limitations,      // limitazioni conosciute
    List<String> useCases,         // casi d'uso consigliati
    
    // Informazioni di download e distribuzione
    long downloadCount,
    String repositoryUrl,
    Instant lastUpdated,
    Instant createdAt
) {}
