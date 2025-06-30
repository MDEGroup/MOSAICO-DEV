package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;




@SpringBootTest
public class ModelRepositoryTest {

    private static final Model model1 = new Model(
            "1", // id
            "HealthcareModel", // name
            "Modello multi-agente per il monitoraggio della salute", // description
            "1.0", // version
            "MOSAICO Health Team", // author
            "MIT", // license
            "multi-agent-system", // modelType
            List.of("collaborative", "healthcare", "monitoring"), // tags
            Map.of("precision", 0.95, "maxAgents", 10), // config
            "Hierarchical", // architecture
            null, // agents
            null, // tools
            null, // coordinationPatterns
            null, // communicationProtocols
            null, // monitoringConfig
            null, // behaviors
            null, // metrics
            "MOSAICO MAS Framework v1.2", // trainingFramework
            "3 distributed nodes, 24 hours", // trainingCompute
            List.of("Limited real-time processing for high volume data"), // limitations
            List.of("Patient monitoring", "Health alert systems"), // useCases
            245, // downloadCount
            "https://repo.mosaico.ai/models/healthcare-model", // repositoryUrl
            Instant.parse("2025-06-15T10:15:30.00Z"), // lastUpdated
            Instant.parse("2025-01-10T08:30:00.00Z") // createdAt
    );

    private static final Model model2 = new Model(
            "2", // id
            "FinanceModel", // name
            "Modello multi-agente per analisi finanziaria", // description
            "2.1", // version
            "MOSAICO Finance Group", // author
            "Apache-2.0", // license
            "data-analysis", // modelType
            List.of("finance", "predictive", "market-analysis"), // tags
            Map.of("accuracy", 0.92, "maxAgents", 15), // config
            "Market-based", // architecture
            null, // agents
            null, // tools
            null, // coordinationPatterns
            null, // communicationProtocols
            null, // monitoringConfig
            null, // behaviors
            null, // metrics
            "MOSAICO MAS Framework v1.3", // trainingFramework
            "5 high-performance nodes, 36 hours", // trainingCompute
            List.of("May not adapt to extreme market volatility"), // limitations
            List.of("Market trend analysis", "Portfolio optimization"), // useCases
            187, // downloadCount
            "https://repo.mosaico.ai/models/finance-model", // repositoryUrl
            Instant.parse("2025-06-28T14:45:20.00Z"), // lastUpdated
            Instant.parse("2025-02-15T09:20:00.00Z") // createdAt
    );
    @Autowired
    private ModelRepository repository;

    

    @Test
    
    public void createModel() {
        Model savedModel = repository.save(model1);
        assertNotNull(savedModel);
        assertEquals("1", savedModel.id());
        assertEquals("HealthcareModel", savedModel.name());
        //verify(repository, times(1)).save(model);
    }

    @Test
    public void findModelById() {
        repository.save(model1);
        Optional<Model> foundModel = repository.findById("1");

        assertTrue(foundModel.isPresent());
        assertEquals("1", foundModel.get().id());
        assertEquals("HealthcareModel", foundModel.get().name());
        
    }

    @Test
    public void findModelByIdException() {
        Optional<Model> foundModel = repository.findById("2");
        assertTrue(!foundModel.isPresent());
    }

    @Test
    public void findModelByName() {
        var models = repository.findByName("HealthcareModel");
        assertNotNull(models);
        assertEquals(1, models.size());
        assertEquals("HealthcareModel", models.get(0).name());
    }

    @Test
    public void deleteModel() {
        repository.save(model1);
        var i = repository.count();
        repository.deleteById("1");
        assertEquals(i-1, repository.count());
    }
}
