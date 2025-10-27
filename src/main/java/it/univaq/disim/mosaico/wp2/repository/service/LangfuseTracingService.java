package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for interacting with Langfuse tracing/observability API.
 * Provides methods to create traces, spans, generations, and events.
 */
@Service
public class LangfuseTracingService {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseTracingService.class);

    private final LangfuseProperties properties;
    private final WebClient webClient;

    public LangfuseTracingService(LangfuseProperties properties) {
        this.properties = properties;
        
        if (properties.isConfigured()) {
            String auth = properties.getPublicKey() + ":" + properties.getSecretKey();
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
            
            this.webClient = WebClient.builder()
                    .baseUrl(properties.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
                    
            logger.info("Langfuse tracing service initialized: {}", properties.getBaseUrl());
        } else {
            this.webClient = null;
            logger.info("Langfuse tracing disabled or not configured");
        }
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    /**
     * Start a new trace.
     * @param name Trace name (e.g. "GET /api/agents")
     * @param metadata Additional metadata
     * @return Trace ID
     */
    public String startTrace(String name, Map<String, Object> metadata) {
        if (!isEnabled()) {
            return null;
        }

        String traceId = UUID.randomUUID().toString();
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", traceId);
        payload.put("name", name);
        payload.put("timestamp", Instant.now().toString());
        if (metadata != null) {
            payload.put("metadata", metadata);
        }

        sendAsync("/api/public/traces", payload);
        
        logger.debug("Started Langfuse trace: {} [{}]", name, traceId);
        return traceId;
    }

    /**
     * End a trace with final metadata.
     * @param traceId Trace ID
     * @param output Output data
     * @param metadata Final metadata
     */
    public void endTrace(String traceId, Map<String, Object> output, Map<String, Object> metadata) {
        if (!isEnabled() || traceId == null) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", traceId);
        if (output != null) {
            payload.put("output", output);
        }
        if (metadata != null) {
            payload.put("metadata", metadata);
        }

        sendAsync("/api/public/traces", payload);
        logger.debug("Ended Langfuse trace: {}", traceId);
    }

    /**
     * Create a span within a trace.
     * @param traceId Parent trace ID
     * @param name Span name
     * @param input Input data
     * @return Span ID
     */
    public String createSpan(String traceId, String name, Map<String, Object> input) {
        if (!isEnabled() || traceId == null) {
            return null;
        }

        String spanId = UUID.randomUUID().toString();
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", spanId);
        payload.put("traceId", traceId);
        payload.put("name", name);
        payload.put("startTime", Instant.now().toString());
        if (input != null) {
            payload.put("input", input);
        }

        sendAsync("/api/public/spans", payload);
        
        logger.debug("Created Langfuse span: {} in trace {}", name, traceId);
        return spanId;
    }

    /**
     * End a span with output.
     * @param spanId Span ID
     * @param output Output data
     */
    public void endSpan(String spanId, Map<String, Object> output) {
        if (!isEnabled() || spanId == null) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", spanId);
        payload.put("endTime", Instant.now().toString());
        if (output != null) {
            payload.put("output", output);
        }

        sendAsync("/api/public/spans", payload);
        logger.debug("Ended Langfuse span: {}", spanId);
    }

    /**
     * Log a generation (LLM call).
     * @param traceId Parent trace ID
     * @param name Generation name
     * @param model Model identifier
     * @param input Input prompt/data
     * @param output Generated output
     * @param metadata Additional metadata (tokens, cost, etc.)
     * @return Generation ID
     */
    public String logGeneration(String traceId, String name, String model, 
                                 Map<String, Object> input, Map<String, Object> output, 
                                 Map<String, Object> metadata) {
        if (!isEnabled() || traceId == null) {
            return null;
        }

        String generationId = UUID.randomUUID().toString();
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", generationId);
        payload.put("traceId", traceId);
        payload.put("name", name);
        payload.put("model", model);
        payload.put("startTime", Instant.now().toString());
        payload.put("endTime", Instant.now().toString());
        if (input != null) {
            payload.put("input", input);
        }
        if (output != null) {
            payload.put("output", output);
        }
        if (metadata != null) {
            payload.put("metadata", metadata);
        }

        sendAsync("/api/public/generations", payload);
        
        logger.debug("Logged Langfuse generation: {} [{}]", name, generationId);
        return generationId;
    }

    /**
     * Log an event.
     * @param traceId Parent trace ID
     * @param name Event name
     * @param metadata Event metadata
     */
    public void logEvent(String traceId, String name, Map<String, Object> metadata) {
        if (!isEnabled() || traceId == null) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", UUID.randomUUID().toString());
        payload.put("traceId", traceId);
        payload.put("name", name);
        payload.put("timestamp", Instant.now().toString());
        if (metadata != null) {
            payload.put("metadata", metadata);
        }

        sendAsync("/api/public/events", payload);
        logger.debug("Logged Langfuse event: {} in trace {}", name, traceId);
    }

    /**
     * Send payload to Langfuse API asynchronously (fire-and-forget).
     */
    private void sendAsync(String endpoint, Map<String, Object> payload) {
        if (webClient == null) {
            return;
        }

        webClient.post()
                .uri(endpoint)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .onErrorResume(e -> {
                    logger.warn("Langfuse API call failed ({}): {}", endpoint, e.getMessage());
                    return Mono.empty();
                })
                .subscribe();
    }
}
