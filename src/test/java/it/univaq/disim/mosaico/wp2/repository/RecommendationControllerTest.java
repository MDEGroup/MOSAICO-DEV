package it.univaq.disim.mosaico.wp2.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.univaq.disim.mosaico.wp2.repository.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.controller.RecommendationController;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.service.RecommendationService;

@WebMvcTest(RecommendationController.class)
@Import(RecommendationControllerTest.TestConfig.class)
public class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Model model1;
    private Model model2;
    private AgentDefinition agent1;
    private CoordinationPattern pattern1;
    private CommunicationProtocol protocol1;
    private Context testContext;

    @BeforeEach
    public void setup() {
        // Inizializza i dati di test
        model1 = new Model(
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
        
        model2 = new Model(
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

        agent1 = new AgentDefinition("a1", "Agent1", "Description1", "ReactiveAgent",
            null, List.of("reasoning", "learning"), null, List.of("tool1", "tool2"), null, 3);

        pattern1 = new CoordinationPattern("p1", "Pattern1", "Description1", 
            "hierarchical", null, null, null, null, 3, List.of("ReactiveAgent", "BDIAgent"), 
            List.of("tool1"), "FIPA", List.of("healthcare"));

        protocol1 = new CommunicationProtocol("c1", "Protocol1", "Description1", 
            "1.0", "JSON", "HTTP", null, false, 1000, true);

        testContext = new Context();
        testContext.setDomainArea("healthcare");
        testContext.setComplexity(3);
        testContext.setTaskType("multi-agent-system");
        testContext.setDistributedExecution(true);
    }

    @Test
    @DisplayName("Test getRecommendations endpoint")
    public void testGetRecommendations() throws Exception {
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(recommendationService.recommendModels(any(Context.class))).thenReturn(mockModels);

        mockMvc.perform(post("/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    @DisplayName("Test getRecommendationsForTask endpoint")
    public void testGetRecommendationsForTask() throws Exception {
        List<Model> mockModels = Arrays.asList(model1);
        when(recommendationService.recommendModelsForTask(anyString(), anyString())).thenReturn(mockModels);

        mockMvc.perform(post("/recommendations/task")
                .param("taskId", "task123")
                .param("taskType", "multi-agent-system"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    @DisplayName("Test provideFeedback endpoint")
    public void testProvideFeedback() throws Exception {
        mockMvc.perform(post("/recommendations/feedback")
                .param("modelId", "1")
                .param("rating", "4")
                .param("feedback", "Ottimo modello")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test recommendArchitecture endpoint")
    public void testRecommendArchitecture() throws Exception {
        Map<String, Object> mockArchitecture = new HashMap<>();
        mockArchitecture.put("agentTypes", Arrays.asList("HybridAgent", "HealthcareAgent"));
        mockArchitecture.put("coordinationPatterns", Arrays.asList("Hierarchical", "Market-based"));
        mockArchitecture.put("communicationProtocols", Arrays.asList("FIPA-ACL"));
        mockArchitecture.put("recommendedAgentCount", 12);
        mockArchitecture.put("justification", "This architecture is recommended based on distributed execution requirements, task complexity level 3, domain-specific needs in healthcare, and best practices for similar systems.");

        when(recommendationService.recommendMasArchitecture(any(Context.class))).thenReturn(mockArchitecture);

        mockMvc.perform(post("/recommendations/architecture")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agentTypes").isArray())
                .andExpect(jsonPath("$.agentTypes[0]").value("HybridAgent"))
                .andExpect(jsonPath("$.coordinationPatterns").isArray())
                .andExpect(jsonPath("$.justification").isString());
    }

    @Test
    @DisplayName("Test findSimilarProjects endpoint")
    public void testFindSimilarProjects() throws Exception {
        List<Map<String, Object>> mockProjects = Arrays.asList(
            Map.of("id", "project-1", 
                   "name", "Healthcare Monitoring MAS", 
                   "domain", "healthcare", 
                   "similarityScore", 0.85),
            Map.of("id", "project-2", 
                   "name", "Financial Market Simulation", 
                   "domain", "finance", 
                   "similarityScore", 0.65)
        );

        when(recommendationService.findSimilarMasProjects(any(Context.class), anyInt())).thenReturn(mockProjects);

        mockMvc.perform(post("/recommendations/similar-projects")
                .param("limit", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("project-1"))
                .andExpect(jsonPath("$[0].similarityScore").value(0.85))
                .andExpect(jsonPath("$[1].id").value("project-2"));
    }

    @Test
    @DisplayName("Test recommendCommunicationProtocols endpoint")
    public void testRecommendCommunicationProtocols() throws Exception {
        List<CommunicationProtocol> mockProtocols = Arrays.asList(protocol1);

        when(recommendationService.recommendCommunicationProtocols(any(Context.class))).thenReturn(mockProtocols);

        mockMvc.perform(post("/recommendations/communication-protocols")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].name").value("Protocol1"));
    }

    @Test
    @DisplayName("Test recommendCoordinationPatterns endpoint")
    public void testRecommendCoordinationPatterns() throws Exception {
        List<CoordinationPattern> mockPatterns = Arrays.asList(pattern1);

        when(recommendationService.recommendCoordinationPatterns(any(Context.class), anyList())).thenReturn(mockPatterns);

        mockMvc.perform(post("/recommendations/coordination-patterns")
                .param("agentTypes", "ReactiveAgent", "BDIAgent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[0].name").value("Pattern1"));
    }

    @Test
    @DisplayName("Test recommendAgentDefinitions endpoint")
    public void testRecommendAgentDefinitions() throws Exception {
        List<AgentDefinition> mockAgents = Arrays.asList(agent1);

        when(recommendationService.recommendAgentDefinitions(any(Context.class))).thenReturn(mockAgents);

        mockMvc.perform(post("/recommendations/agent-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("a1"))
                .andExpect(jsonPath("$[0].name").value("Agent1"));
    }

    @Test
    @DisplayName("Test recommendTechnologies endpoint")
    public void testRecommendTechnologies() throws Exception {
        Map<String, List<Map<String, Object>>> mockTechnologies = new HashMap<>();
        
        List<Map<String, Object>> devFrameworks = Arrays.asList(
            Map.of("name", "JADE",
                   "description", "Java Agent DEvelopment Framework",
                   "score", 0.9)
        );
        
        List<Map<String, Object>> testingTools = Arrays.asList(
            Map.of("name", "JUnit",
                   "description", "Java testing framework",
                   "score", 0.95)
        );
        
        mockTechnologies.put("development", devFrameworks);
        mockTechnologies.put("testing", testingTools);

        when(recommendationService.recommendTechnologiesAndFrameworks(any(Context.class))).thenReturn(mockTechnologies);

        mockMvc.perform(post("/recommendations/technologies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.development").isArray())
                .andExpect(jsonPath("$.development[0].name").value("JADE"))
                .andExpect(jsonPath("$.testing").isArray())
                .andExpect(jsonPath("$.testing[0].name").value("JUnit"));
    }

    @Test
    @DisplayName("Test getTelemetryInsights endpoint")
    public void testGetTelemetryInsights() throws Exception {
        Map<String, Object> mockInsights = new HashMap<>();
        
        Map<String, Object> performanceMetrics = Map.of(
            "averageResponseTime", 120,
            "throughput", 5000,
            "errorRate", 0.02
        );
        
        mockInsights.put("performanceMetrics", performanceMetrics);
        mockInsights.put("recommendedOptimizations", Arrays.asList(
            Map.of("area", "Communication",
                   "recommendation", "Implement message batching to reduce overhead")
        ));

        when(recommendationService.getTelemetryInsights(any(Context.class))).thenReturn(mockInsights);

        mockMvc.perform(post("/recommendations/telemetry-insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testContext)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceMetrics").exists())
                .andExpect(jsonPath("$.performanceMetrics.averageResponseTime").value(120))
                .andExpect(jsonPath("$.recommendedOptimizations").isArray());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RecommendationService recommendationService() {
            return Mockito.mock(RecommendationService.class);
        }
    }

}
