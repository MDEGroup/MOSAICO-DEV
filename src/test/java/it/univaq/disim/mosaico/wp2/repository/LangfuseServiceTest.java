package it.univaq.disim.mosaico.wp2.repository;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.langfuse.client.resources.projects.types.Project;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LangfuseService.
 */
class LangfuseServiceTest {

    @Mock
    private LangfuseProperties properties;

    private LangfuseService langfuseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceDisabledWhenNotConfigured() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        
        // When
        langfuseService = new LangfuseService(properties);
        
        // Then
        assertFalse(langfuseService.isEnabled());
    }

    @Test
    void testServiceEnabledWhenConfigured() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        
        // When
        langfuseService = new LangfuseService(properties);
        
        // Then
        assertTrue(langfuseService.isEnabled());
    }

    @Test
    void testGetProjectsWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        List<Project> projects = langfuseService.getProjects();
        
        // Then
        assertNotNull(projects);
        assertTrue(projects.isEmpty());
    }

    @Test
    void testGetProjectByIdWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        Map<String, Object> project = langfuseService.getProjectById("test-id");
        
        // Then
        assertNull(project);
    }

    @Test
    void testGetProjectStatsWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        Map<String, Object> stats = langfuseService.getProjectStats("test-id");
        
        // Then
        assertNull(stats);
    }

    @Test
    void testGetProjectTracesWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        List<Map<String, Object>> traces = langfuseService.getProjectTraces("test-id", 100);
        
        // Then
        assertNotNull(traces);
        assertTrue(traces.isEmpty());
    }

    @Test
    void testServiceInitializationWithValidConfig() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test-key");
        when(properties.getSecretKey()).thenReturn("sk-test-secret");
        
        // When
        langfuseService = new LangfuseService(properties);
        
        // Then
        assertTrue(langfuseService.isEnabled());
    }

    @Test
    void testServiceInitializationWithInvalidConfig() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        
        // When
        langfuseService = new LangfuseService(properties);
        
        // Then
        assertFalse(langfuseService.isEnabled());
    }

    @Test
    void testGetProjectsReturnsEmptyListWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        List<Project> result = langfuseService.getProjects();
        
        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetProjectTracesWithLimitWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        List<Map<String, Object>> result = langfuseService.getProjectTraces("project-123", 50);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateProjectWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        Map<String, Object> project = langfuseService.createProject("Test Project", "Test Description");
        
        // Then
        assertNull(project);
    }

    @Test
    void testCreateProjectWithNullName() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        langfuseService = new LangfuseService(properties);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject(null, "Description");
        });
    }

    @Test
    void testCreateProjectWithEmptyName() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        langfuseService = new LangfuseService(properties);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            langfuseService.createProject("  ", "Description");
        });
    }

    @Test
    void testCreateProjectWithNullDescription() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        langfuseService = new LangfuseService(properties);
        
        // When
        Map<String, Object> project = langfuseService.createProject("Test Project", null);
        
        // Then
        assertNull(project);
    }
}
