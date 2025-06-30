
package it.univaq.disim.mosaico.wp2.repository;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.service.impl.ModelServiceImpl;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;

public class ModelServiceTest {

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
    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelServiceImpl modelService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        List<Model> mockModels = Arrays.asList(model1, model2);
        when(modelRepository.findAll()).thenReturn(mockModels);

        List<Model> models = modelService.findAll();

        assertEquals(2, models.size());
        verify(modelRepository, times(1)).findAll();
    }

    @Test
    public void testFindById() {
        Model mockModel = model1;
        when(modelRepository.findById("1")).thenReturn(Optional.of(mockModel));

        Model model = modelService.findById("1");

        assertNotNull(model);
        assertEquals("1", model.id());
        assertEquals("HealthcareModel", model.name());
        verify(modelRepository, times(1)).findById("1");
    }

    @Test
    public void testSave() {
        Model mockModel = model1;
        when(modelRepository.save(mockModel)).thenReturn(mockModel);

        Model savedModel = modelService.save(mockModel);

        assertNotNull(savedModel);
        assertEquals("1", savedModel.id());
        assertEquals("HealthcareModel", savedModel.name());
        verify(modelRepository, times(1)).save(mockModel);
    }

    @Test
    public void testDeleteById() {
        doNothing().when(modelRepository).deleteById("1");

        modelService.deleteById("1");

        verify(modelRepository, times(1)).deleteById("1");
    }

    @Test
    public void testUpdate() {
        Model mockModel = new Model(
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
        when(modelRepository.existsById("1")).thenReturn(true);
        when(modelRepository.save(mockModel)).thenReturn(mockModel);

        Model updatedModel = modelService.update(mockModel);

        assertNotNull(updatedModel);
        assertEquals("1", updatedModel.id());
        assertEquals("HealthcareModel", updatedModel.name());
        verify(modelRepository, times(1)).existsById("1");
        verify(modelRepository, times(1)).save(mockModel);
    }
}
