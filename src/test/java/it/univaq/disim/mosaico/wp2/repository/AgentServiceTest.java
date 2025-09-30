package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.AgentServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for AgentService.
 */
@ExtendWith(MockitoExtension.class)
public class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;
    
    @InjectMocks
    private AgentServiceImpl agentService;
    
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
            "agent1",
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
    }
    
    @Test
    void testSaveAgent() {
        when(agentRepository.save(testAgent)).thenReturn(testAgent);
        
        Agent savedAgent = agentService.save(testAgent);
        
        assertNotNull(savedAgent);
        assertEquals(testAgent.name(), savedAgent.name());
        verify(agentRepository, times(1)).save(testAgent);
    }
    
    @Test
    void testFindById() {
        when(agentRepository.findById("agent1")).thenReturn(Optional.of(testAgent));
        
        Optional<Agent> foundAgent = agentService.findById("agent1");
        
        assertTrue(foundAgent.isPresent());
        assertEquals(testAgent.name(), foundAgent.get().name());
        verify(agentRepository, times(1)).findById("agent1");
    }
    
    @Test
    void testFindByIdNotFound() {
        when(agentRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        Optional<Agent> foundAgent = agentService.findById("nonexistent");
        
        assertFalse(foundAgent.isPresent());
        verify(agentRepository, times(1)).findById("nonexistent");
    }
    
    @Test
    void testFindAll() {
        List<Agent> agents = List.of(testAgent);
        when(agentRepository.findAll()).thenReturn(agents);
        
        List<Agent> foundAgents = agentService.findAll();
        
        assertEquals(1, foundAgents.size());
        assertEquals(testAgent.name(), foundAgents.get(0).name());
        verify(agentRepository, times(1)).findAll();
    }
    
    @Test
    void testFindByRole() {
        when(agentRepository.findByRole("Specialist")).thenReturn(List.of(testAgent));
        
        List<Agent> specialists = agentService.findByRole("Specialist");
        
        assertEquals(1, specialists.size());
        assertEquals(testAgent.role(), specialists.get(0).role());
        verify(agentRepository, times(1)).findByRole("Specialist");
    }
    
    @Test
    void testFindByProvider() {
        when(agentRepository.findByProvider_Id("provider1")).thenReturn(List.of(testAgent));
        
        List<Agent> providerAgents = agentService.findByProvider("provider1");
        
        assertEquals(1, providerAgents.size());
        assertEquals(testAgent.provider().id(), providerAgents.get(0).provider().id());
        verify(agentRepository, times(1)).findByProvider_Id("provider1");
    }
    
    @Test
    void testFindByIOModality() {
        when(agentRepository.findAll()).thenReturn(List.of(testAgent));
        
        List<Agent> textAgents = agentService.findByIOModality(IOModality.TEXT);
        
        assertEquals(1, textAgents.size());
        assertTrue(textAgents.get(0).ioModalities().contains(IOModality.TEXT));
        verify(agentRepository, times(1)).findAll();
    }
    
    @Test
    void testDeleteById() {
        doNothing().when(agentRepository).deleteById("agent1");
        
        agentService.deleteById("agent1");
        
        verify(agentRepository, times(1)).deleteById("agent1");
    }
}