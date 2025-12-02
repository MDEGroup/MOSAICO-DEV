package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.AgentConsumption;
import it.univaq.disim.mosaico.wp2.repository.data.InteractionProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.InputParameter;
import it.univaq.disim.mosaico.wp2.repository.data.Memory;
import it.univaq.disim.mosaico.wp2.repository.data.OutputStructure;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.Tool;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentConsumptionRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.InteractionProtocolRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.MemoryRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.SkillRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ToolRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AgentRepository.
 */
@ActiveProfiles("test")
public class AgentRepositoryTest {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private ToolRepository toolRepository;
    @Autowired
    private MemoryRepository memoryRepository;
    @Autowired
    private InteractionProtocolRepository interactionProtocolRepository;
    @Autowired
    private AgentConsumptionRepository agentConsumptionRepository;
    private Agent testAgent;
    private Provider testProvider;
    private List<Skill> exampleSkills;
    private List<Tool> exampleTools;
    private List<Memory> exampleMemories;
    private List<InteractionProtocol> exampleProtocols;
    private List<AgentConsumption> exampleConsumptions;
    @AfterAll
    static void tearDown(@Autowired AgentRepository agentRepository, @Autowired ProviderRepository providerRepository) {
        //agentRepository.deleteAll();
        //providerRepository.deleteAll();
    }
    @BeforeEach
    void setUp() {
        // agentRepository.deleteAll();
        testProvider = new Provider("Test Provider", "Test provider description", "http://test.com");

        agentRepository.deleteAll();
        providerRepository.deleteAll();
        testProvider = new Provider("Test Provider", "Test provider description", "http://test.com");
        testProvider = providerRepository.save(testProvider);

        exampleSkills = List.of(
            new Skill(
                null,
                "Code Review",
                "Understands static-analysis alerts and AST diffs",
                ProficiencyLevel.ADVANCED,
                LocalDateTime.now(),
                List.of("SOFT-ENG-TASK-001")
            )
        );

        exampleTools = List.of(
            new Tool(
                null,
                "RepoScanner",
                "Scans repositories for security issues",
                "API_KEY",
                "repo:read",
                "1000/day",
                "per-minute"
            )
        );

        exampleMemories = List.of(
            new Memory(
                null,
                MemoryType.LONG_TERM,
                MemoryScope.AGENT,
                "postgres://memories"
            )
        );

        exampleProtocols = List.of(
            new InteractionProtocol(
                null,
                "FIPA-ACL",
                "1.0",
                "https://specs.example/fipa-acl",
                "Asynchronous agent communication"
            )
        );

        exampleConsumptions = List.of(
            new AgentConsumption(
                null,
                "temperature",
                List.of(new InputParameter("max_tokens", "2048")),
                List.of(new OutputStructure("format", "markdown"))
            )
        );

        skillRepository.saveAll(exampleSkills);
        toolRepository.saveAll(exampleTools);
        memoryRepository.saveAll(exampleMemories);
        interactionProtocolRepository.saveAll(exampleProtocols);
        agentConsumptionRepository.saveAll(exampleConsumptions);

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
            exampleSkills,
            exampleTools,
            exampleMemories,
            exampleProtocols,
            exampleConsumptions
        );
    }
    
    @Test
    void testSaveAndFindById() {
        Agent savedAgent = agentRepository.save(testAgent);
        
        Optional<Agent> foundAgent = agentRepository.findById(savedAgent.getId());
        
        assertTrue(foundAgent.isPresent());
        assertEquals(testAgent.getName(), foundAgent.get().getName());
        assertEquals(testAgent.getRole(), foundAgent.get().getRole());
    }
    
    @Test
    void testFindByName() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByName("Test Agent");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.getName(), agents.get(0).getName());
    }
    
    @Test
    void testFindByRole() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByRole("developer");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.getRole(), agents.get(0).getRole());
    }
    
    @Test
    void testFindByObjective() {
        agentRepository.save(testAgent);
        
        List<Agent> agents = agentRepository.findByObjective("coding assistance");
        
        assertFalse(agents.isEmpty());
        assertEquals(1, agents.size());
        assertEquals(testAgent.getObjective(), agents.get(0).getObjective());
    }
    
    @Test
    void testDeleteById() {
        Agent savedAgent = agentRepository.save(testAgent);
        
        agentRepository.deleteById(savedAgent.getId());
        
        Optional<Agent> foundAgent = agentRepository.findById(savedAgent.getId());
        assertFalse(foundAgent.isPresent());
    }
}