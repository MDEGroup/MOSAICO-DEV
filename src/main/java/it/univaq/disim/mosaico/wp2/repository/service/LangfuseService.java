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
import java.util.Base64;
import java.util.List;
import java.util.Map;
import com.langfuse.client.LangfuseClient;
import com.langfuse.client.core.LangfuseClientApiException;
import com.langfuse.client.resources.projects.ProjectsClient;
import com.langfuse.client.resources.projects.types.Project;
/**
 * Service for managing Langfuse projects.
 * Provides methods to retrieve and manage projects from Langfuse API.
 */
@Service
public class LangfuseService {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseService.class);

    private final LangfuseProperties properties;
    private final WebClient webClient;

    public LangfuseService(LangfuseProperties properties) {
        this.properties = properties;
        
        if (properties.isConfigured()) {
            String auth = properties.getPublicKey() + ":" + properties.getSecretKey();
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
            
            this.webClient = WebClient.builder()
                    .baseUrl(properties.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
                    
            logger.info("Langfuse project service initialized: {}", properties.getBaseUrl());
        } else {
            this.webClient = null;
            logger.info("Langfuse project service disabled or not configured");
        }
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    /**
     * Get list of all projects from Langfuse.
     * @return List of project objects
     */
    @SuppressWarnings("unchecked")
    public List<Project>  getProjects() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty project list");
            return null;
        }
        LangfuseClient client = LangfuseClient.builder().url(properties.getBaseUrl())
                .credentials(properties.getPublicKey(), properties.getSecretKey()).build();

        try {
            ProjectsClient pips = client.projects();
            return pips.get().getData();
        } catch (LangfuseClientApiException error) {
            System.out.println("ERRORE" + error.getMessage());
            return null;
        }
       
    }

    /**
     * Get a specific project by ID.
     * @param projectId Project ID
     * @return Project object or null if not found
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProjectById(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }

        Map<String, Object> project = webClient.get()
                .uri("/api/public/projects/{id}", projectId)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> Mono.empty())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

        if (project != null) {
            logger.info("Retrieved project {} from Langfuse", projectId);
        }
        return project;
    }

    /**
     * Get project statistics.
     * @param projectId Project ID
     * @return Statistics object or null if not found
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProjectStats(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }

        Map<String, Object> stats = webClient.get()
                .uri("/api/public/metrics/projects/{id}", projectId)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> Mono.empty())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

        if (stats != null) {
            logger.info("Retrieved stats for project {} from Langfuse", projectId);
        }
        return stats;
    }

    /**
     * Get traces for a specific project.
     * @param projectId Project ID
     * @param limit Maximum number of traces to retrieve
     * @return List of traces
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getProjectTraces(String projectId, int limit) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty traces list");
            return List.of();
        }

        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/public/traces")
                        .queryParam("projectId", projectId)
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

        if (response != null && response.containsKey("data")) {
            List<Map<String, Object>> traces = (List<Map<String, Object>>) response.get("data");
            logger.info("Retrieved {} traces for project {} from Langfuse", traces.size(), projectId);
            return traces;
        }

        logger.warn("No traces found for project {}", projectId);
        return List.of();
    }

    /**
     * Create a new project in Langfuse.
     * @param projectName Name of the project
     * @param description Optional description
     * @return Created project object or null if failed
     * @throws IllegalArgumentException if projectName is null or empty
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> createProject(String projectName, String description) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }

        if (projectName == null || projectName.trim().isEmpty()) {
            logger.error("Project name cannot be null or empty");
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("name", projectName.trim());
        if (description != null && !description.trim().isEmpty()) {
            requestBody.put("description", description.trim());
        }

        Map<String, Object> project = webClient.post()
                .uri("/api/public/projects")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> Mono.empty())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

        if (project != null) {
            logger.info("Created project '{}' in Langfuse", projectName);
        }
        return project;
    }

    /**
     * Get the configured API keys currently used by this service (from configuration).
     * Note: These keys are organization/project scoped depending on how they were created in Langfuse UI.
     * This does NOT fetch keys from Langfuse API; it only returns what is configured locally.
     *
     * @return a map containing "publicKey" and "secretKey" when enabled, or an empty map if disabled
     */
    public Map<String, String> getConfiguredApiKeys() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled; returning empty API keys map");
            return java.util.Collections.emptyMap();
        }
        return java.util.Map.of(
                "publicKey", properties.getPublicKey(),
                "secretKey", properties.getSecretKey()
        );
    }

    /**
     * Attempt to get the API key for a specific project via Public API.
     * IMPORTANT: As of the current Langfuse Public API, there is no supported endpoint
     * to retrieve existing API keys for a project. Secrets are typically only shown on creation
     * and API key management is intended via the Langfuse UI or management APIs.
     *
     * This method therefore returns null and logs an informational message to document the limitation.
     * If in the future Langfuse exposes a public endpoint (e.g., GET /api/public/keys?projectId=...),
     * this method can be updated accordingly.
     *
     * @param projectId the Langfuse project id
     * @return null (API not available via public endpoints)
     */
    public Map<String, Object> getProjectApiKey(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }

        logger.info("Attempt to retrieve API key for project {} via Public API", projectId);
        logger.info("Public API does not expose an endpoint to fetch existing project API keys; returning null");
        return null;
    }

    /**
     * Get API keys for a specific project using Langfuse Public API.
     * Endpoint: GET /api/public/projects/{projectId}/apiKeys
     *
     * The response is expected to be an object that may contain a "data" array with API key entries.
     * Each entry typically includes metadata for the API key (the secret value might not be present for security).
     *
     * @param projectId the Langfuse project id
     * @return list of API key objects (empty list if none or if the endpoint is unavailable)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getProjectApiKeys(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty api keys list");
            return List.of();
        }

        Map<String, Object> response = webClient.get()
                .uri("/api/public/projects/{projectId}/apiKeys", projectId)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> Mono.empty())
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();

        if (response != null && response.containsKey("data")) {
            Object data = response.get("data");
            if (data instanceof List) {
                List<Map<String, Object>> keys = (List<Map<String, Object>>) data;
                logger.info("Retrieved {} API keys for project {} from Langfuse", keys.size(), projectId);
                return keys;
            }
        }

        logger.info("No API keys found for project {} or unexpected response format", projectId);
        return List.of();
    }
}
