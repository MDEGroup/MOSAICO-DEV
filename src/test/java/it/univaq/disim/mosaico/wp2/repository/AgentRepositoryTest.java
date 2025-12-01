package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AgentRepository.
 */
@DataMongoTest
@ActiveProfiles("test")
public class AgentRepositoryTest {

    @Autowired
    private AgentRepository agentRepository;
    
    private Agent testAgent;
    private Provider testProvider;

    @AfterAll
    static void tearDown(@Autowired AgentRepository agentRepository, @Autowired ProviderRepository providerRepository) {
        //agentRepository.deleteAll();
        //providerRepository.deleteAll();
    }
    @BeforeEach
    void setUp() {
        // agentRepository.deleteAll();
        testProvider = new Provider("provider1", "Test Provider", "Test provider description", "http://test.com");
        
        testAgent = new Agent(
            "Test Agent",
            "Test agent description",
            "1.0.0",
            testProvider,
            "MIT",
            "Test beliefs",
            "Test intentions", 
            "Test desires",
            "developer",
            "coding assistance",
            Arrays.asList(IOModality.TEXT, IOModality.CODE),
            "Test backstory",
            List.of(), // skills
            List.of(), // exploits
            List.of(), // has
            List.of(), // supports
            List.of()  // consumptions
        );
    }
    
    @Test
    void testSaveAndFindById() {
        Agent savedAgent = agentRepository.save(testAgent);
        
        Optional<Agent> foundAgent = agentRepository.findById(savedAgent.id());
        
        assertTrue(foundAgent.isPresent());
        assertEquals(testAgent.name(), foundAgent.get().name());
        assertEquals(testAgent.role(), foundAgent.get().role());
    }
    
    @Test
    void testFindByName() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByName("Test Agent");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.name(), agents.get(0).name());
    }
    
    @Test
    void testFindByRole() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByRole("developer");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.role(), agents.get(0).role());
    }
    
    @Test
    void testFindByObjective() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByObjective("coding assistance");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.objective(), agents.get(0).objective());
    }
    
    @Test
    void testDeleteById() {
        Agent savedAgent = agentRepository.save(testAgent);
        
        agentRepository.deleteById(savedAgent.id());
        
        Optional<Agent> foundAgent = agentRepository.findById(savedAgent.id());
        assertFalse(foundAgent.isPresent());
    }
}