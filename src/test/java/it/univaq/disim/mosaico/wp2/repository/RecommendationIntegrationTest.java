package it.univaq.disim.mosaico.wp2.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CommunicationProtocolRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CoordinationPatternRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.service.RecommendationService;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class RecommendationIntegrationTest {

    @Autowired
    private RecommendationService recommendationService;

    @MockBean
    private ModelRepository modelRepository;

    @MockBean
    private AgentDefinitionRepository agentDefinitionRepository;

    @MockBean
    private CoordinationPatternRepository coordinationPatternRepository;

    @MockBean
    private CommunicationProtocolRepository communicationProtocolRepository;

    // Dati di test
    private Model healthcareModel;
    private Model financeModel;
    private AgentDefinition healthcareAgent;
    private CoordinationPattern hierarchicalPattern;
    private CommunicationProtocol fipaProtocol;
    private Context healthcareContext;
    private Context financeContext;

    @BeforeEach
    public void setUp() {
        // Crea modelli di test


        
        healthcareModel = new Model(
                "m1",                              // id
                "Healthcare MAS",                  // name
                "A multi-agent system for healthcare", // description
                "1.0",                             // version
                "MAS Lab",                         // author
                "MIT",                             // license
                "multi-agent-system",              // modelType
                Arrays.asList("collaborative", "healthcare", "monitoring"),  // tags
                Map.of("precision", 0.95, "maxAgents", 10),  // config
                "Hierarchical",                    // architecture
                null,                              // agents
                null,                              // tools
                null,                              // coordinationPatterns
                null,                              // communicationProtocols
                null,                              // monitoringConfig
                null,                              // behaviors
                null,                              // metrics
                "MOSAICO MAS Framework v1.2",      // trainingFramework
                "3 distributed nodes, 24 hours",   // trainingCompute
                Arrays.asList("Limited real-time processing for high volume data"), // limitations
                Arrays.asList("Patient monitoring", "Health alert systems"),  // useCases
                245,                               // downloadCount
                "https://repo.mosaico.ai/models/healthcare-model",  // repositoryUrl
                Instant.parse("2025-06-15T10:15:30.00Z"),  // lastUpdated
                Instant.parse("2025-01-10T08:30:00.00Z")   // createdAt
        );
        
        financeModel = new Model(
                "m2",                              // id
                "Finance Analysis MAS",            // name
                "A multi-agent system for financial analysis", // description
                "2.1",                             // version
                "Finance Lab",                     // author
                "Apache-2.0",                      // license
                "data-analysis",                   // modelType
                Arrays.asList("finance", "predictive", "market-analysis"),  // tags
                Map.of("accuracy", 0.92, "maxAgents", 15),  // config
                "Market-based",                    // architecture
                null,                              // agents
                null,                              // tools
                null,                              // coordinationPatterns
                null,                              // communicationProtocols
                null,                              // monitoringConfig
                null,                              // behaviors
                null,                              // metrics
                "MOSAICO MAS Framework v1.3",      // trainingFramework
                "5 high-performance nodes, 36 hours", // trainingCompute
                Arrays.asList("May not adapt to extreme market volatility"), // limitations
                Arrays.asList("Market trend analysis", "Portfolio optimization"),  // useCases
                187,                               // downloadCount
                "https://repo.mosaico.ai/models/finance-model",  // repositoryUrl
                Instant.parse("2025-06-28T14:45:20.00Z"),  // lastUpdated
                Instant.parse("2025-02-15T09:20:00.00Z")   // createdAt
        );
        
        // Crea agenti di test
        healthcareAgent = new AgentDefinition("a1", "HealthcareAgent", "Agent for healthcare tasks", 
                "Reactive", null, Arrays.asList("monitoring", "healthcare", "patient-tracking"), 
                null, null, null, 3);
        
        // Crea pattern di coordinamento
        hierarchicalPattern = new CoordinationPattern("p1", "Hierarchical", "Hierarchical coordination pattern", 
                "hierarchical", null, null, null, null, 3, Arrays.asList("Reactive", "BDI"), 
                null, "FIPA", Arrays.asList("healthcare", "monitoring"));
        
        // Crea protocolli di comunicazione
        fipaProtocol = new CommunicationProtocol("c1", "FIPA-ACL", "FIPA Agent Communication Language", 
                "1.0", "XML", "HTTP", null, true, 1000, true);
        
        // Configura i contesti
        healthcareContext = new Context();
        healthcareContext.setDomainArea("healthcare");
        healthcareContext.setComplexity(3);
        healthcareContext.setTaskType("multi-agent-system");
        healthcareContext.setDistributedExecution(true);
        
        financeContext = new Context();
        financeContext.setDomainArea("finance");
        financeContext.setComplexity(4);
        financeContext.setTaskType("data-analysis");
        financeContext.setDistributedExecution(false);
        
        // Configura i repository mock
        when(modelRepository.findAll()).thenReturn(Arrays.asList(healthcareModel, financeModel));
        when(agentDefinitionRepository.findAll()).thenReturn(Arrays.asList(healthcareAgent));
        when(coordinationPatternRepository.findAll()).thenReturn(Arrays.asList(hierarchicalPattern));
        when(communicationProtocolRepository.findAll()).thenReturn(Arrays.asList(fipaProtocol));
    }

    @Test
    @DisplayName("Integrazione: Raccomandazione modelli basata sul dominio")
    public void testModelRecommendationsByDomain() {
        // Test per contesto sanitario
        List<Model> healthcareRecommendations = recommendationService.recommendModels(healthcareContext);
        assertNotNull(healthcareRecommendations);
        assertTrue(healthcareRecommendations.size() > 0);
        assertEquals("m1", healthcareRecommendations.get(0).id()); // Il modello sanitario dovrebbe essere il primo
        
        // Test per contesto finanziario
        List<Model> financeRecommendations = recommendationService.recommendModels(financeContext);
        assertNotNull(financeRecommendations);
        assertTrue(financeRecommendations.size() > 0);
        assertEquals("m2", financeRecommendations.get(0).id()); // Il modello finanziario dovrebbe essere il primo
    }

    @Test
    @DisplayName("Integrazione: Raccomandazione architettura MAS")
    public void testMasArchitectureRecommendation() {
        // Test per contesto sanitario
        Map<String, Object> healthcareArchitecture = recommendationService.recommendMasArchitecture(healthcareContext);
        assertNotNull(healthcareArchitecture);
        assertTrue(healthcareArchitecture.containsKey("agentTypes"));
        
        @SuppressWarnings("unchecked")
        List<String> agentTypes = (List<String>) healthcareArchitecture.get("agentTypes");
        assertTrue(agentTypes.contains("HealthcareAgent"));
        
        // Test per contesto finanziario
        Map<String, Object> financeArchitecture = recommendationService.recommendMasArchitecture(financeContext);
        assertNotNull(financeArchitecture);
        
        @SuppressWarnings("unchecked")
        List<String> financeAgentTypes = (List<String>) financeArchitecture.get("agentTypes");
        assertTrue(financeAgentTypes.contains("FinancialAgent"));
    }

    @Test
    @DisplayName("Integrazione: Raccomandazione protocolli di comunicazione")
    public void testCommunicationProtocolRecommendation() {
        // Test per contesto distribuito (dovrebbe preferire FIPA-ACL)
        List<CommunicationProtocol> distributedProtocols = recommendationService.recommendCommunicationProtocols(healthcareContext);
        assertNotNull(distributedProtocols);
        
        // Test per contesto non distribuito
        List<CommunicationProtocol> nonDistributedProtocols = recommendationService.recommendCommunicationProtocols(financeContext);
        assertNotNull(nonDistributedProtocols);
    }

    @Test
    @DisplayName("Integrazione: Raccomandazione tecnologie e framework")
    public void testTechnologiesRecommendation() {
        // Test per contesto sanitario
        Map<String, List<Map<String, Object>>> healthcareTech = recommendationService.recommendTechnologiesAndFrameworks(healthcareContext);
        assertNotNull(healthcareTech);
        assertTrue(healthcareTech.containsKey("development"));
        assertTrue(healthcareTech.get("development").size() > 0);
        
        // Test per contesto finanziario
        Map<String, List<Map<String, Object>>> financeTech = recommendationService.recommendTechnologiesAndFrameworks(financeContext);
        assertNotNull(financeTech);
        assertTrue(financeTech.containsKey("development"));
    }

    @Test
    @DisplayName("Integrazione: Ciclo completo di raccomandazione e feedback")
    public void testRecommendationAndFeedbackCycle() {
        // 1. Ottieni raccomandazioni
        List<Model> recommendations = recommendationService.recommendModels(healthcareContext);
        assertNotNull(recommendations);
        assertTrue(recommendations.size() > 0);
        
        // 2. Fornisci feedback sulla prima raccomandazione
        String modelId = recommendations.get(0).id();
        assertDoesNotThrow(() -> {
            recommendationService.provideFeedback(modelId, healthcareContext, 5, "Ottima raccomandazione!");
        });
        
        // 3. Ottieni raccomandazioni aggiornate (in una implementazione reale, queste dovrebbero essere influenzate dal feedback)
        List<Model> updatedRecommendations = recommendationService.recommendModels(healthcareContext);
        assertNotNull(updatedRecommendations);
        assertTrue(updatedRecommendations.size() > 0);
    }
}
