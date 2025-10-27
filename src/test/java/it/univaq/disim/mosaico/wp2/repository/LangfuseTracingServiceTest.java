package it.univaq.disim.mosaico.wp2.repository;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseTracingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LangfuseTracingService.
 */
class LangfuseTracingServiceTest {

    @Mock
    private LangfuseProperties properties;

    private LangfuseTracingService tracingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testServiceDisabledWhenNotConfigured() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        
        // When
        tracingService = new LangfuseTracingService(properties);
        
        // Then
        assertFalse(tracingService.isEnabled());
    }

    @Test
    void testServiceEnabledWhenConfigured() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        
        // When
        tracingService = new LangfuseTracingService(properties);
        
        // Then
        assertTrue(tracingService.isEnabled());
    }

    @Test
    void testStartTraceWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("test", "value");
        
        // When
        String traceId = tracingService.startTrace("Test Trace", metadata);
        
        // Then
        assertNull(traceId);
    }

    @Test
    void testEndTraceWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> output = new HashMap<>();
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.endTrace("trace-123", output, null)
        );
    }

    @Test
    void testCreateSpanWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> input = new HashMap<>();
        
        // When
        String spanId = tracingService.createSpan("trace-123", "Test Span", input);
        
        // Then
        assertNull(spanId);
    }

    @Test
    void testEndSpanWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> output = new HashMap<>();
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.endSpan("span-123", output)
        );
    }

    @Test
    void testLogGenerationWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> input = new HashMap<>();
        Map<String, Object> output = new HashMap<>();
        Map<String, Object> metadata = new HashMap<>();
        
        // When
        String generationId = tracingService.logGeneration(
            "trace-123", "Test Generation", "gpt-4", input, output, metadata
        );
        
        // Then
        assertNull(generationId);
    }

    @Test
    void testLogEventWhenDisabled() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        Map<String, Object> metadata = new HashMap<>();
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.logEvent("trace-123", "Test Event", metadata)
        );
    }

    @Test
    void testStartTraceWithNullMetadata() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        tracingService = new LangfuseTracingService(properties);
        
        // When
        String traceId = tracingService.startTrace("Test Trace", null);
        
        // Then
        assertNull(traceId);
    }

    @Test
    void testEndTraceWithNullTraceId() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        tracingService = new LangfuseTracingService(properties);
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.endTrace(null, null, null)
        );
    }

    @Test
    void testCreateSpanWithNullTraceId() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        tracingService = new LangfuseTracingService(properties);
        
        // When
        String spanId = tracingService.createSpan(null, "Test Span", null);
        
        // Then
        assertNull(spanId);
    }

    @Test
    void testEndSpanWithNullSpanId() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        tracingService = new LangfuseTracingService(properties);
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.endSpan(null, null)
        );
    }

    @Test
    void testLogGenerationWithNullTraceId() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        tracingService = new LangfuseTracingService(properties);
        
        // When
        String generationId = tracingService.logGeneration(
            null, "Test", "gpt-4", null, null, null
        );
        
        // Then
        assertNull(generationId);
    }

    @Test
    void testLogEventWithNullTraceId() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test");
        when(properties.getSecretKey()).thenReturn("sk-test");
        tracingService = new LangfuseTracingService(properties);
        
        // When/Then - should not throw exception
        assertDoesNotThrow(() -> 
            tracingService.logEvent(null, "Test Event", null)
        );
    }

    @Test
    void testServiceInitializationWithValidConfig() {
        // Given
        when(properties.isConfigured()).thenReturn(true);
        when(properties.getBaseUrl()).thenReturn("http://localhost:3000");
        when(properties.getPublicKey()).thenReturn("pk-test-key");
        when(properties.getSecretKey()).thenReturn("sk-test-secret");
        
        // When
        tracingService = new LangfuseTracingService(properties);
        
        // Then
        assertTrue(tracingService.isEnabled());
    }

    @Test
    void testServiceInitializationWithInvalidConfig() {
        // Given
        when(properties.isConfigured()).thenReturn(false);
        
        // When
        tracingService = new LangfuseTracingService(properties);
        
        // Then
        assertFalse(tracingService.isEnabled());
    }
}
