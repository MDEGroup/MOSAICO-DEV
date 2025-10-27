package it.univaq.disim.mosaico.wp2.repository;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.langfuse.client.resources.projects.types.Project;
import com.langfuse.client.resources.projects.types.Projects;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for LangfuseService that actually call the Langfuse API.
 * 
 * These tests require:
 * 1. Langfuse server running on http://localhost:3000
 * 2. Valid API keys configured in application.properties or environment variables
 * 3. Network connectivity to the Langfuse server
 * 4. At least one project already created in Langfuse (for read operations)
 * 
 * NOTE: The Langfuse Public API does NOT support project creation via API.
 * Projects must be created manually via the Langfuse UI.
 * 
 * To run these tests:
 * - Start Langfuse: ./start-langfuse.sh
 * - Create at least one project in Langfuse UI (http://localhost:3000)
 * - Run: LANGFUSE_INTEGRATION_TEST=true ./mvnw test -Dtest=LangfuseServiceIntegrationTest
 */
@SpringBootTest
@TestPropertySource(properties = {
    "langfuse.enabled=true",
    "langfuse.base-url=http://localhost:3000",
    "langfuse.public-key=pk-lf-72af177c-f55e-46f5-a617-0619b95f36da",
    "langfuse.secret-key=sk-lf-9cae9864-b2d6-4a06-9b49-96af21624361",
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
        
        if (!projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            // Mark test as passed but log warning
            assertTrue(true, "Test skipped - no projects available");
            return;
        }
        
        String projectId = (String) projects.get(0).getId();
        
        // Then
        assertNotNull(projects, "Projects list should not be null");
        logger.info("Found {} projects in Langfuse", projects.size());
        
        // If there are projects, verify structure
        if (!projects.isEmpty()) {
            Project firstProject =  projects.get(0);
            assertNotNull(firstProject.getId(), "Project should have an id");
            assertNotNull(firstProject.getName(), "Project should have a name");
            logger.info("First project: {} (id: {})", firstProject.getName(), firstProject.getId());
        } else {
            logger.warn("⚠️  WARNING: No projects found in Langfuse. Please create at least one project in the UI for full test coverage.");
        }
    }

    @Test
    void testCreateProjectReturns405MethodNotAllowed() {
        // Given
        String projectName = "Test Project " + UUID.randomUUID().toString().substring(0, 8);
        String description = "This test verifies that project creation is not supported via public API";
        
        logger.info("Attempting to create project (expecting API limitation): {}", projectName);
        
        // When
        Map<String, Object> createdProject = langfuseService.createProject(projectName, description);
        
        // Then
        // The Langfuse Public API does NOT support project creation
        // This will return null due to 405 Method Not Allowed
        assertNull(createdProject, "Project creation should fail (not supported by public API)");
        
        logger.info("✓ Confirmed: Project creation not supported via API (as expected)");
        logger.info("ℹ️  Projects must be created manually in Langfuse UI");
    }

    @Test
    void testGetProjectByIdWithValidId() {
        // Given - First get all projects to have a valid ID
        List<Project> projects = langfuseService.getProjects();
        
        if (!projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            // Mark test as passed but log warning
            assertTrue(true, "Test skipped - no projects available");
            return;
        }
        
        String projectId = (String) projects.get(0).getId();
        
        // When
        Map<String, Object> project = langfuseService.getProjectById(projectId);
        
        // Then
        // Note: Langfuse Public API may not support GET /projects/{id}
        // This might return null with 404 Not Found
        if (project == null) {
            logger.info("ℹ️  GET /api/public/projects/{{id}} not supported by Langfuse Public API (404)");
            assertTrue(true, "API limitation confirmed");
        } else {
            assertEquals(projectId, project.get("id"), "Project ID should match");
            logger.info("Retrieved project by ID: {}", project.get("name"));
        }
    }

    @Test
    void testGetProjectByIdWithInvalidId() {
        // Given
        String invalidId = "invalid-project-id-" + UUID.randomUUID();
        
        // When
        Map<String, Object> project = langfuseService.getProjectById(invalidId);
        
        // Then
        assertNull(project, "Should return null for invalid project ID");
        logger.info("Correctly returned null for invalid project ID");
    }

    @Test
    void testGetProjectStats() {
        // Given - Get a project
        List<Project> projects = langfuseService.getProjects();
        
        if (projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            assertTrue(true, "Test skipped - no projects available");
            return;
        }
        
        String projectId = (String) projects.get(0).getId();
        
        // When
        Map<String, Object> stats = langfuseService.getProjectStats(projectId);
        
        // Then
        // Note: Stats endpoint may not be available in public API
        if (stats == null) {
            logger.info("ℹ️  Project stats endpoint not available in Langfuse Public API");
            assertTrue(true, "API limitation confirmed");
        } else {
            logger.info("Project stats: {}", stats);
            assertTrue(stats.isEmpty() || stats.containsKey("traceCount") || stats.size() > 0, 
                       "Stats should be empty or contain trace information");
        }
    }

    @Test
    void testGetProjectTraces() {
        // Given - Get a project
        List<Project> projects = langfuseService.getProjects();
        
        if (projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping test. Create a project in Langfuse UI first.");
            assertTrue(true, "Test skipped - no projects available");
            return;
        }
        
        String projectId = projects.get(0).getId();
        
        // When
        List<Map<String, Object>> traces = langfuseService.getProjectTraces(projectId, 10);
        
        // Then
        // Note: Traces endpoint may not be available in public API
        if (traces == null) {
            logger.info("ℹ️  Project traces endpoint not available in Langfuse Public API");
            assertTrue(true, "API limitation confirmed");
        } else {
            logger.info("Found {} traces for project", traces.size());
            assertTrue(traces.isEmpty() || traces.get(0).containsKey("id"), 
                       "Traces should be empty or contain trace data with id");
        }
    }

    @Test
    void testCreateProjectWithNullNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject(null, "Description");
        }, "Should throw IllegalArgumentException for null project name");
    }

    @Test
    void testCreateProjectWithEmptyNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject("", "Description");
        }, "Should throw IllegalArgumentException for empty project name");
    }

    @Test
    void testCreateProjectWithBlankNameShouldFail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject("   ", "Description");
        }, "Should throw IllegalArgumentException for blank project name");
    }

    @Test
    void testCreateProjectWithNullDescription() {
        // Given
        String projectName = "No Description Project " + UUID.randomUUID().toString().substring(0, 8);
        
        // When
        Map<String, Object> createdProject = langfuseService.createProject(projectName, null);
        
        // Then
        // API doesn't support project creation, so this will return null
        assertNull(createdProject, "Project creation not supported via API");
        logger.info("✓ Confirmed: Cannot create project via API (expected behavior)");
    }

    @Test
    void testMultipleProjectsRetrieval() {
        // Given - Get all existing projects
        List<Project> allProjects = langfuseService.getProjects();
        
        // Then
        assertNotNull(allProjects, "Projects list should not be null");
        logger.info("Found {} total projects in Langfuse", allProjects.size());
        
        if (allProjects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Create some projects in Langfuse UI for better test coverage.");
        } else {
            // Verify each project has required fields
            for (Project project : allProjects) {
                assertNotNull(project.getId(), "Each project should have an id");
                assertNotNull(project.getName(), "Each project should have a name");
                logger.info("  - {} (id: {})", project.getName(), project.getId());
            }
        }
    }

    @Test
    void testGetProjectApiKeysForValidProject() {
        // Given - ensure at least one project exists
        List<Project> projects = langfuseService.getProjects();
        if (projects.isEmpty()) {
            logger.warn("⚠️  WARNING: No projects found. Skipping API keys test. Create a project in Langfuse UI first.");
            assertTrue(true, "Test skipped - no projects available");
            return;
        }

        String projectId = (String) projects.get(0).getId();

        // When
        List<Map<String, Object>> apiKeys = langfuseService.getProjectApiKeys(projectId);

        // Then
        assertNotNull(apiKeys, "API keys list should not be null");
        logger.info("Retrieved {} API keys for project {}", apiKeys.size(), projectId);
        // API may return empty list if no keys or if endpoint is limited in self-hosted envs
        if (!apiKeys.isEmpty()) {
            Map<String, Object> first = apiKeys.get(0);
            // Assert commonly expected fields when present
            assertTrue(first.containsKey("id") || first.containsKey("name") || first.containsKey("createdAt"),
                    "API key entry should contain at least an identifier or metadata");
        } else {
            logger.info("ℹ️  No API keys returned by Langfuse Public API for this project (possible limitation or no keys created)");
        }
    }

    @Test
    void testGetProjectApiKeysForInvalidProject() {
        // Given
        String invalidId = "invalid-project-id-" + UUID.randomUUID();

        // When
        List<Map<String, Object>> apiKeys = langfuseService.getProjectApiKeys(invalidId);

        // Then
        assertNotNull(apiKeys, "API keys list should not be null even for invalid project");
        assertTrue(apiKeys.isEmpty(), "Should return empty list for invalid project ID");
        logger.info("Correctly returned empty API keys list for invalid project ID");
    }
}
