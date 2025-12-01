package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.univaq.disim.mosaico.wp2.repository.controller.AgentController;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AgentController.
 */
@WebMvcTest(AgentController.class)
public class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private AgentService agentService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Agent testAgent;
    private Provider testProvider;
    
    @BeforeEach
    void setUp() {
        testProvider = new Provider(
            "provider1",
            "OpenAI",
            "AI company providing language models",
            "https://openai.com"
        );
        
        testAgent = new Agent(
            "Code Review Agent",
            "AI agent specialized in code review",
            "v1.0",
            testProvider,
            "MIT",
            "Code quality beliefs",
            "Review code efficiently",
            "Deliver high-quality reviews",
            "Specialist",
            "Code Review",
            List.of(IOModality.TEXT),
            "Background in software engineering",
            List.of(), // skills
            List.of(), // tools  
            List.of(), // memory
            List.of(), // interactionProtocols
            List.of()  // agentConsumption
        );
        ReflectionTestUtils.setField(testAgent, "id", "agent1");
    }
    
    @Test
    void testGetAllAgents() throws Exception {
        when(agentService.findAll()).thenReturn(List.of(testAgent));
        
        mockMvc.perform(get("/api/agents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Code Review Agent"))
                .andExpect(jsonPath("$[0].role").value("Specialist"));
        
        verify(agentService, times(1)).findAll();
    }
    
    @Test
    void testGetAgentById() throws Exception {
        when(agentService.findById("agent1")).thenReturn(Optional.of(testAgent));
        
        mockMvc.perform(get("/api/agents/agent1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Code Review Agent"))
                .andExpect(jsonPath("$.id").value("agent1"));
        
        verify(agentService, times(1)).findById("agent1");
    }
    
    @Test
    void testGetAgentByIdNotFound() throws Exception {
        when(agentService.findById("nonexistent")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/agents/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(agentService, times(1)).findById("nonexistent");
    }
    
    @Test
    void testCreateAgent() throws Exception {
        when(agentService.save(any(Agent.class))).thenReturn(testAgent);
        
        mockMvc.perform(post("/api/agents")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAgent)))
                .andExpect(status().isOk())  // Controller returns 200, not 201
                .andExpect(jsonPath("$.name").value("Code Review Agent"));
        
        verify(agentService, times(1)).save(any(Agent.class));
    }
    
    @Test
    void testUpdateAgent() throws Exception {
        when(agentService.findById("agent1")).thenReturn(Optional.of(testAgent));
        when(agentService.save(any(Agent.class))).thenReturn(testAgent);
        
        mockMvc.perform(put("/api/agents/agent1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAgent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Code Review Agent"));
        
        verify(agentService, times(1)).findById("agent1");
        verify(agentService, times(1)).save(any(Agent.class));
    }
    
    @Test
    void testUpdateAgentNotFound() throws Exception {
        when(agentService.findById("nonexistent")).thenReturn(Optional.empty());
        
        mockMvc.perform(put("/api/agents/nonexistent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAgent)))
                .andExpect(status().isNotFound());
        
        verify(agentService, times(1)).findById("nonexistent");
        verify(agentService, never()).save(any(Agent.class));
    }
    
    @Test
    void testDeleteAgent() throws Exception {
        doNothing().when(agentService).deleteById("agent1");
        
        mockMvc.perform(delete("/api/agents/agent1"))
                .andExpect(status().isNoContent());
        
        verify(agentService, times(1)).deleteById("agent1");
    }
    
    @Test
    void testDeleteAgentNotFound() throws Exception {
        doNothing().when(agentService).deleteById("nonexistent");
        
        mockMvc.perform(delete("/api/agents/nonexistent"))
                .andExpect(status().isNoContent());  // Always returns 204
        
        verify(agentService, times(1)).deleteById("nonexistent");
    }
    
    @Test
    void testGetAgentsByRole() throws Exception {
        when(agentService.findByRole("Specialist")).thenReturn(List.of(testAgent));
        
        mockMvc.perform(get("/api/agents/search/role").param("role", "Specialist"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("Specialist"));
        
        verify(agentService, times(1)).findByRole("Specialist");
    }
}