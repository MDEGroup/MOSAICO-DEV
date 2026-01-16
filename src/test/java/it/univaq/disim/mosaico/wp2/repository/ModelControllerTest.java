package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.controller.ModelController;
import it.univaq.disim.mosaico.wp2.repository.data.Model;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerTest {

    // @Autowired
    // private MockMvc mockMvc;

    // @Test
    // void all() throws Exception {
    // mockMvc.perform(get("/model"))
    // .andExpect(status().isOk());
    // }
    // @Test
    // void all2() throws Exception {
    // mockMvc.perform(get("/models"))
    // .andExpect(status().is4xxClientError());
    // }
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ModelRepository repository;

    @InjectMocks
    private ModelController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    void testGetAllModels() throws Exception {
        when(repository.findAll()).thenReturn(Arrays.asList(
                new Model(
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
                ), new Model(
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
                )));

        mockMvc.perform(get("/model"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));

        verify(repository, times(1)).findAll();
    }

    void testGetModelById() throws Exception {
        // when(repository.findById("1")).thenReturn(Optional.of(
        // new Model("1","1", "Model1", "Description1", "Type1", "Owner1")
        // ));
        // mockMvc.perform(get("/model/1"))
        // .andExpect(status().isOk())
        // .andExpect(jsonPath("$.id").value("1"))
        // .andExpect(jsonPath("$.name").value("1"));

        // verify(repository, times(1)).findById("1");
        when(repository.findById("1")).thenReturn(Optional.of(
                new Model(
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
                )));

        Model response = controller.one("1");

        assertNotNull(response);
    }

    void testGetModelByIdNotFound() throws Exception {
        when(repository.findById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/model/1"))
                .andExpect(status().isNotFound());

        verify(repository, times(1)).findById("1");
    }

    void testUpdateModel() throws Exception {
        Model updatedModel = new Model(
                        "1", // id
                        "U HealthcareModel", // name
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
        when(repository.findById("1"))
                .thenReturn(Optional.of(new Model(
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
                )));
        when(repository.save(any(Model.class))).thenReturn(updatedModel);

        mockMvc.perform(put("/model/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"id\":\"1\",\"name\":\"UpdatedModel\",\"description\":\"UpdatedDescription\",\"type\":\"UpdatedType\",\"owner\":\"UpdatedOwner\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedModel"));

        verify(repository, times(1)).findById("1");
        verify(repository, times(1)).save(any(Model.class));
    }

    void testDeleteModel() throws Exception {
        doNothing().when(repository).deleteById("1");

        mockMvc.perform(delete("/model/1"))
                .andExpect(status().isOk());

        verify(repository, times(1)).deleteById("1");
    }

}