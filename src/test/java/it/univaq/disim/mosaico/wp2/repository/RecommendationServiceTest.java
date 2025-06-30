package it.univaq.disim.mosaico.wp2.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.univaq.disim.mosaico.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CommunicationProtocolRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CoordinationPatternRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.RecommendationServiceImpl;

public class RecommendationServiceTest {

    // Dati mock per i test
    private static final Model model1 = new Model(
        "1",                                // id
        "HealthcareModel",                  // name
        "Modello multi-agente per il monitoraggio della salute",  // description
        "1.0",                              // version
        "MOSAICO Health Team",              // author
        "MIT",                              // license
        "multi-agent-system",               // modelType
        List.of("collaborative", "healthcare", "monitoring"),  // tags
        Map.of("precision", 0.95, "maxAgents", 10),  // config
        "Hierarchical",                     // architecture
        null,                               // agents
        null,                               // tools
        null,                               // coordinationPatterns
        null,                               // communicationProtocols
        null,                               // monitoringConfig
        null,                               // behaviors
        null,                               // metrics
        "MOSAICO MAS Framework v1.2",       // trainingFramework
        "3 distributed nodes, 24 hours",    // trainingCompute
        List.of("Limited real-time processing for high volume data"), // limitations
        List.of("Patient monitoring", "Health alert systems"),  // useCases
        245,                                // downloadCount
        "https://repo.mosaico.ai/models/healthcare-model",  // repositoryUrl
        Instant.parse("2025-06-15T10:15:30.00Z"),  // lastUpdated
        Instant.parse("2025-01-10T08:30:00.00Z")   // createdAt
    );
    
    private static final Model model2 = new Model(
        "2",                                // id
        "FinanceModel",                     // name
        "Modello multi-agente per analisi finanziaria",  // description
        "2.1",                              // version
        "MOSAICO Finance Group",            // author
        "Apache-2.0",                       // license
        "data-analysis",                    // modelType
        List.of("finance", "predictive", "market-analysis"),  // tags
        Map.of("accuracy", 0.92, "maxAgents", 15),  // config
        "Market-based",                     // architecture
        null,                               // agents
        null,                               // tools
        null,                               // coordinationPatterns
        null,                               // communicationProtocols
        null,                               // monitoringConfig
        null,                               // behaviors
        null,                               // metrics
        "MOSAICO MAS Framework v1.3",       // trainingFramework
        "5 high-performance nodes, 36 hours",  // trainingCompute
        List.of("May not adapt to extreme market volatility"), // limitations
        List.of("Market trend analysis", "Portfolio optimization"),  // useCases
        187,                                // downloadCount
        "https://repo.mosaico.ai/models/finance-model",  // repositoryUrl
        Instant.parse("2025-06-28T14:45:20.00Z"),  // lastUpdated
        Instant.parse("2025-02-15T09:20:00.00Z")   // createdAt
    );   

    private static final AgentDefinition agent1 = new AgentDefinition("a1", "Agent1", "Description1", "ReactiveAgent",
            null, List.of("reasoning", "learning"), null, List.of("tool1", "tool2"), null, 3);
            
    private static final AgentDefinition agent2 = new AgentDefinition("a2", "Agent2", "Description2", "BDIAgent",
            null, List.of("planning", "healthcare"), null, List.of("tool3"), null, 4);

    private static final CoordinationPattern pattern1 = new CoordinationPattern("p1", "Pattern1", "Description1", 
            "hierarchical", null, null, null, null, 3, List.of("ReactiveAgent", "BDIAgent"), 
            List.of("tool1"), "FIPA", List.of("healthcare"));
            
    private static final CoordinationPattern pattern2 = new CoordinationPattern("p2", "Pattern2", "Description2", 
            "peer-to-peer", null, null, null, null, 2, List.of("ReactiveAgent"), 
            List.of("tool2"), "Simple", List.of("finance"));

    private static final CommunicationProtocol protocol1 = new CommunicationProtocol("c1", "Protocol1", "Description1", 
            "1.0", "JSON", "HTTP", null, false, 1000, true);
            
    private static final CommunicationProtocol protocol2 = new CommunicationProtocol("c2", "Protocol2", "Description2", 
            "2.0", "MessagePack", "WebSocket", null, true, 500, false);

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private AgentDefinitionRepository agentDefinitionRepository;

    @Mock
    private CoordinationPatternRepository coordinationPatternRepository;

    @Mock
    private CommunicationProtocolRepository communicationProtocolRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test recommendModels: dovrebbe restituire modelli ordinati per rilevanza")
    public void testRecommendModels() {
        // Arrange
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(mockModels);
        
        Context context = new Context();
        context.setDomainArea("healthcare");
        context.setTaskType("multi-agent-system");
        
        // Act
        List<Model> recommendations = recommendationService.recommendModels(context);
        
        // Assert
        assertNotNull(recommendations);
        assertTrue(recommendations.size() > 0);
        assertEquals("1", recommendations.get(0).id()); // Verificare che il modello sanitario sia il primo
        verify(modelRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test recommendModelsForTask: dovrebbe restituire modelli adatti al tipo di task")
    public void testRecommendModelsForTask() {
        // Arrange
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(mockModels);
        
        String taskId = "task123";
        String taskType = "multi-agent-system";
        
        // Act
        List<Model> recommendations = recommendationService.recommendModelsForTask(taskId, taskType);
        
        // Assert
        assertNotNull(recommendations);
        assertTrue(recommendations.size() > 0);
        assertEquals("1", recommendations.get(0).id());
        verify(modelRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Test findModelsByCapabilities: dovrebbe trovare modelli con le capacità richieste")
    public void testFindModelsByCapabilities() {
        // Arrange
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(mockModels);
        
        List<String> capabilities = List.of("collaborative");
        
        // Act
        List<Model> result = recommendationService.findModelsByCapabilities(capabilities);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertEquals("1", result.get(0).id());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test recommendMasArchitecture: dovrebbe restituire un'architettura adatta")
    public void testRecommendMasArchitecture() {
        // Arrange
        Context context = new Context();
        context.setDomainArea("healthcare");
        context.setComplexity(4);
        context.setDistributedExecution(true);
        
        // Act
        Map<String, Object> architecture = recommendationService.recommendMasArchitecture(context);
        
        // Assert
        assertNotNull(architecture);
        assertTrue(architecture.containsKey("agentTypes"));
        assertTrue(architecture.containsKey("coordinationPatterns"));
        assertTrue(architecture.containsKey("communicationProtocols"));
        assertTrue(architecture.containsKey("recommendedAgentCount"));
        assertTrue(architecture.containsKey("justification"));
        
        @SuppressWarnings("unchecked")
        List<String> agentTypes = (List<String>) architecture.get("agentTypes");
        assertTrue(agentTypes.contains("HybridAgent"));
        assertTrue(agentTypes.contains("HealthcareAgent"));
    }
    
    @Test
    @DisplayName("Test findSimilarMasProjects: dovrebbe trovare progetti simili")
    public void testFindSimilarMasProjects() {
        // Arrange
        Context context = new Context();
        context.setDomainArea("healthcare");
        int limit = 2;
        
        // Act
        List<Map<String, Object>> similarProjects = recommendationService.findSimilarMasProjects(context, limit);
        
        // Assert
        assertNotNull(similarProjects);
        assertTrue(similarProjects.size() <= limit);
        assertTrue(similarProjects.get(0).containsKey("similarityScore"));
    }
    
    @DisplayName("Test recommendCommunicationProtocols: dovrebbe suggerire protocolli appropriati")
    public void testRecommendCommunicationProtocols() {
        // Arrange
        List<CommunicationProtocol> mockProtocols = Arrays.asList(protocol1, protocol2);
        when(communicationProtocolRepository.findAll()).thenReturn(mockProtocols);
        
        Context context = new Context();
        context.setDistributedExecution(true);
        
        // Act
        List<CommunicationProtocol> protocols = recommendationService.recommendCommunicationProtocols(context);
        
        // Assert
        assertNotNull(protocols);
        verify(communicationProtocolRepository, times(1)).findAll();
        // Il primo protocollo dovrebbe essere distribuito
        assertTrue(protocols.get(0).distributed());
    }
    
    @Test
    @DisplayName("Test recommendCoordinationPatterns: dovrebbe suggerire pattern appropriati")
    public void testRecommendCoordinationPatterns() {
        // Arrange
        List<CoordinationPattern> mockPatterns = Arrays.asList(pattern1, pattern2);
        when(coordinationPatternRepository.findAll()).thenReturn(mockPatterns);
        
        Context context = new Context();
        context.setDomainArea("healthcare");
        context.setComplexity(3);
        List<String> agentTypes = List.of("ReactiveAgent", "BDIAgent");
        
        // Act
        List<CoordinationPattern> patterns = recommendationService.recommendCoordinationPatterns(context, agentTypes);
        
        // Assert
        assertNotNull(patterns);
        verify(coordinationPatternRepository, times(1)).findAll();
        // Il primo pattern dovrebbe essere per il dominio sanitario e supportare i tipi di agente specificati
        assertEquals("p1", patterns.get(0).id());
    }
    @DisplayName("Test recommendAgentDefinitions: dovrebbe suggerire definizioni di agenti appropriate")
    public void testRecommendAgentDefinitions() {
        // Arrange
        List<AgentDefinition> mockAgents = Arrays.asList(agent1, agent2);
        when(agentDefinitionRepository.findAll()).thenReturn(mockAgents);
        
        Context context = new Context();
        context.setDomainArea("healthcare");
        context.setComplexity(4);
        
        // Act
        List<AgentDefinition> agents = recommendationService.recommendAgentDefinitions(context);
        
        // Assert
        assertNotNull(agents);
        verify(agentDefinitionRepository, times(1)).findAll();
        // Il primo agente dovrebbe avere il tag healthcare
        assertTrue(agents.get(0).capabilities().contains("healthcare"));
    }
    
    @Test
    @DisplayName("Test recommendTechnologiesAndFrameworks: dovrebbe suggerire tecnologie appropriate")
    public void testRecommendTechnologiesAndFrameworks() {
        // Arrange
        Context context = new Context();
        context.setDomainArea("healthcare");
        
        // Act
        Map<String, List<Map<String, Object>>> technologies = recommendationService.recommendTechnologiesAndFrameworks(context);
        
        // Assert
        assertNotNull(technologies);
        assertTrue(technologies.containsKey("development"));
        assertTrue(technologies.containsKey("testing"));
        assertTrue(technologies.containsKey("deployment"));
        
        // Verifica che ci siano elementi in ciascuna categoria
        assertTrue(technologies.get("development").size() > 0);
        assertTrue(technologies.get("testing").size() > 0);
        assertTrue(technologies.get("deployment").size() > 0);
    }
    
    @Test
    @DisplayName("Test getTelemetryInsights: dovrebbe fornire analisi dai dati telemetrici")
    public void testGetTelemetryInsights() {
        // Arrange
        Context context = new Context();
        context.setDomainArea("healthcare");
        
        // Act
        Map<String, Object> insights = recommendationService.getTelemetryInsights(context);
        
        // Assert
        assertNotNull(insights);
        assertTrue(insights.containsKey("performanceMetrics"));
        assertTrue(insights.containsKey("resourceUsage"));
        assertTrue(insights.containsKey("commonIssues"));
        assertTrue(insights.containsKey("recommendedOptimizations"));
    }
    
    @Test
    @DisplayName("Test provideFeedback: dovrebbe accettare feedback sulle raccomandazioni")
    public void testProvideFeedback() {
        // Arrange
        String modelId = "1";
        Context context = new Context();
        int rating = 4;
        String feedback = "Molto utile per il mio caso d'uso";
        
        // Act & Assert
        // Non ci sono ritorni da verificare, ma assicuriamoci che non vengano lanciate eccezioni
        assertDoesNotThrow(() -> {
            recommendationService.provideFeedback(modelId, context, rating, feedback);
        });
    }
}
