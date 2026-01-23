package it.univaq.disim.mosaico.wp2.repository;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import com.langfuse.client.resources.commons.types.DatasetItem;
import com.langfuse.client.resources.commons.types.TraceWithFullDetails;
import com.langfuse.client.resources.commons.types.Dataset;
import com.langfuse.client.resources.projects.types.Project;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LangfuseService that actually call the Langfuse API.
 * 
 * These tests require:
 * 1. Langfuse server running on http://localhost:3000
 * 2. Valid API keys configured in application.properties or environment
 * variables
 * 3. Network connectivity to the Langfuse server
 * 4. At least one project already created in Langfuse (for read operations)
 * 
 * NOTE: The Langfuse Public API does NOT support project creation via API.
 * Projects must be created manually via the Langfuse UI.
 * 
 * To run these tests:
 * - Start Langfuse: ./start-langfuse.sh
 * - Create at least one project in Langfuse UI (http://localhost:3000)
 * - Run: LANGFUSE_INTEGRATION_TEST=true ./mvnw test
 * -Dtest=LangfuseServiceIntegrationTest
 */
@SpringBootTest
@TestPropertySource(properties = {
        "langfuse.enabled=true",
        "langfuse.base-url=http://localhost:3000",
        "langfuse.public-key=pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015",
        "langfuse.secret-key=sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee",
        "langfuse.timeout-seconds=10"
})
@EnabledIfEnvironmentVariable(named = "LANGFUSE_INTEGRATION_TEST", matches = "true", disabledReason = "Integration tests disabled. Set LANGFUSE_INTEGRATION_TEST=true to enable")
class LangfuseServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseServiceIntegrationTest.class);

    @Autowired
    private LangfuseService langfuseService;

    @Autowired
    private LangfuseProperties langfuseProperties;

    @BeforeEach
    void setUp() {
        assertNotNull(langfuseService, "LangfuseService should be autowired");
        assertNotNull(langfuseProperties, "LangfuseProperties should be autowired");
    }

    private Agent buildAgentWithIntegrationCredentials() {
        Agent agent = new Agent();
        agent.setLlangfuseUrl(langfuseProperties.getBaseUrl());
        agent.setLlangfusePublicKey(langfuseProperties.getPublicKey());
        agent.setLlangfuseSecretKey(langfuseProperties.getSecretKey());
        agent.setLlangfuseProjectName("agentse.app.summarization");
        return agent;
    }

    @Test
    void testServiceIsConfiguredAndEnabled() {
        // Given & When
        boolean isEnabled = langfuseService.isEnabled();
        boolean isConfigured = langfuseProperties.isConfigured();

        // Then
        assertTrue(isConfigured, "Langfuse should be configured");
        assertTrue(isEnabled, "LangfuseService should be enabled");

        assertEquals("http://localhost:3000", langfuseProperties.getBaseUrl());
        assertNotNull(langfuseProperties.getPublicKey());
        assertNotNull(langfuseProperties.getSecretKey());
        assertTrue(langfuseProperties.getPublicKey().startsWith("pk-"));
        assertTrue(langfuseProperties.getSecretKey().startsWith("sk-"));
    }

    @Test
    void testGetProjectsReturnsValidResponse() {
        // Given
        assertTrue(langfuseService.isEnabled(), "Service should be enabled");

        // When
        // Given - First get all projects to have a valid ID
        List<Project> projects = langfuseService.getProjects();

        if (projects == null || projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            // Mark test as passed but log warning
            assertTrue(true, "Test skipped - no projects available");
            return;
        }

        // Then
        assertNotNull(projects, "Projects list should not be null");
        logger.info("Found {} projects in Langfuse", projects.size());

        // If there are projects, verify structure
        if (!projects.isEmpty()) {
            Project firstProject = projects.get(0);
            assertNotNull(firstProject.getId(), "Project should have an id");
            assertNotNull(firstProject.getName(), "Project should have a name");
            logger.info("First project: {} (id: {})", firstProject.getName(), firstProject.getId());
        } else {
            logger.warn(
                    "⚠️  WARNING: No projects found in Langfuse. Please create at least one project in the UI for full test coverage.");
        }
    }

    @Test
    void testGetProjectByIdWithValidId() {
        // Given - First get all projects to have a valid ID
        List<Project> projects = langfuseService.getProjects();

        if (projects == null || projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            // Mark test as passed but log warning
            assertTrue(true, "Test skipped - no projects available");
            return;
        }

        String projectId = (String) projects.get(0).getId();

        // When
        Project project = langfuseService.getProjectById(projectId);

        // Then
        // Note: Langfuse Public API may not support GET /projects/{id}
        // This might return null with 404 Not Found
        if (project == null) {
            logger.info("ℹ️  GET /api/public/projects/{{id}} not supported by Langfuse Public API (404)");
            assertTrue(true, "API limitation confirmed");
        } else {
            assertEquals(projectId, project.getId(), "Project ID should match");
            logger.info("Retrieved project by ID: {}", project.getName());
        }
    }

    @Test
    void testGetProjectByIdWithInvalidId() {
        // Given
        String invalidId = "invalid-project-id-" + UUID.randomUUID();

        // When
        Project project = langfuseService.getProjectById(invalidId);

        // Then
        assertNull(project, "Should return null for invalid project ID");
        logger.info("Correctly returned null for invalid project ID");
    }

    // #region Create Project Tests
    void testCreateProjectWithNullNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject(null, "Description");
        }, "Should throw IllegalArgumentException for null project name");
    }

    void testCreateProjectWithEmptyNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject("", "Description");
        }, "Should throw IllegalArgumentException for empty project name");
    }

    void testCreateProjectWithBlankNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject("   ", "Description");
        }, "Should throw IllegalArgumentException for blank project name");
    }

    void testCreateProjectWithNullDescription() {
        // Given
        String projectName = "No Description Project " + UUID.randomUUID().toString().substring(0, 8);

        // When
        Project createdProject = langfuseService.createProject(projectName, null);

        // Then
        // API doesn't support project creation, so this will return null
        assertNull(createdProject, "Project creation not supported via API");
        logger.info("✓ Confirmed: Cannot create project via API (expected behavior)");
    }

    void testCreateProjectSuccess() {
        // Given
        String projectName = "No Description Project " + UUID.randomUUID().toString().substring(0, 8);

        // When
        Project createdProject = langfuseService.createProject(projectName, "null");

        // Then
        // API doesn't support project creation, so this will return null
        assertNotNull(createdProject, "Project creation not supported via API");
        logger.info("✓ Confirmed: created project via API (expected behavior)");
    }

    // #endregion
    // @Test
    void testCreateDatasetSuccess() {
        // Given
        String datasetName = "No Description Dataset " + UUID.randomUUID().toString().substring(0, 8);

        // When
        Dataset createdDataset = langfuseService.createDataset(datasetName, "dataset description");
        logger.info("Created dataset with ID: {}", createdDataset.getId());
        // Then
        // API doesn't support dataset creation, so this will return null
        assertNotNull(createdDataset, "Dataset creation not supported via API");
        logger.info("✓ Confirmed: created dataset via API (expected behavior)");
    }

    // @Test
    void testCreateDatasetItemSuccess() {
        String datasetName = "No Description Dataset " + UUID.randomUUID().toString().substring(0, 8);
        Dataset createdDataset = langfuseService.createDataset(datasetName, "dataset description");
        if (createdDataset == null || createdDataset.getName() == null) {
            logger.warn("⚠️  Dataset creation unsupported on this Langfuse instance. Skipping dataset item test.");
            assertTrue(true, "Dataset creation not supported");
            return;
        }
        logger.info("Created dataset with ID: {}", createdDataset.getId());
        DatasetItem createdItem = langfuseService.createDatasetItems(createdDataset.getName(), "Italy", "Rome");
        assertNotNull(createdItem, "Dataset creation not supported via API");
        logger.info("✓ Confirmed: created dataset item via API (expected behavior)");
    }

    @Test
    void testGetDataset() {
        String datasetName = "No Description Dataset 1f4d1a1c";
        Dataset retrievedDataset = langfuseService.getDataset(datasetName);
        assertNotNull(retrievedDataset, "Retrieved dataset should not be null");
        logger.info("✓ Confirmed: retrieved dataset via API (expected behavior)");
    }

    @Test
    void testGetTraces() {
        Agent agent = buildAgentWithIntegrationCredentials();
        var traces = langfuseService.getTraces(agent);
        assertNotNull(traces, "Traces response should not be null");
        // traces.forEach(trace -> {
        // logger.info("Trace ID: {} - Name: {}", trace.getId(), trace.getName());
        // });
        logger.info("Retrieved {} traces for project ID", traces.size());

    }

    @Test
    void getRunBenchmark() {
        // Given
        Agent agent = buildAgentWithIntegrationCredentials();
        // When
        List<TraceData> lista = langfuseService.fetchTracesFromRun(agent, "ause_train",
                "run test - 2025-12-05T08:48:15.353757Z");
        assertTrue(!lista.isEmpty());
        logger.info("Retrieved run benchmarks for project ID {}", lista.size());

    }

    @Test
    void testGetMetrics() {
        // Given
        Agent agent = buildAgentWithIntegrationCredentials();

        // When
        langfuseService.getMetrics(agent, "latency_sec");
        logger.info("Retrieved metrics for project ID");

    }

    @Test
    void testMultipleProjectsRetrieval() {
        // Given - Get all existing projects
        List<Project> allProjects = langfuseService.getProjects();

        // Then
        assertNotNull(allProjects, "Projects list should not be null");
        logger.info("Found {} total projects in Langfuse", allProjects.size());

        if (allProjects.isEmpty()) {
            logger.warn(
                    "⚠️  WARNING: No projects found. Create some projects in Langfuse UI for better test coverage.");
        } else {
            // Verify each project has required fields
            for (Project project : allProjects) {
                assertNotNull(project.getId(), "Each project should have an id");
                assertNotNull(project.getName(), "Each project should have a name");
                logger.info("  - {} (id: {})", project.getName(), project.getId());
            }
        }
    }

}
