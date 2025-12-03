package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.langfuse.client.LangfuseClient;
import com.langfuse.client.core.LangfuseClientApiException;
import com.langfuse.client.core.RequestOptions;
import com.langfuse.client.resources.commons.types.Dataset;
import com.langfuse.client.resources.commons.types.DatasetItem;
import com.langfuse.client.resources.commons.types.Score;
import com.langfuse.client.resources.commons.types.NumericScore;
import com.langfuse.client.resources.commons.types.TraceWithDetails;
import com.langfuse.client.resources.datasetitems.types.CreateDatasetItemRequest;
import com.langfuse.client.resources.datasets.types.CreateDatasetRequest;
import com.langfuse.client.resources.metrics.requests.GetMetricsRequest;
import com.langfuse.client.resources.metrics.types.MetricsResponse;
import com.langfuse.client.resources.projects.ProjectsClient;
import com.langfuse.client.resources.projects.requests.CreateProjectRequest;
import com.langfuse.client.resources.projects.types.Project;
import com.langfuse.client.resources.trace.requests.GetTracesRequest;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;

/**
 * Service for managing Langfuse projects.
 * Provides methods to retrieve and manage projects from Langfuse API.
 */
@Service
public class LangfuseService {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseService.class);

    private final LangfuseProperties properties;

    public LangfuseService(LangfuseProperties properties) {
        this.properties = properties;

        if (properties.isConfigured()) {
            logger.info("Langfuse project service initialized: {}", properties.getBaseUrl());
        } else {
            logger.info("Langfuse project service disabled or not configured");
        }
    }

    public boolean isEnabled() {
        return properties.isConfigured();
    }

    /**
     * Get list of all projects from Langfuse.
     * 
     * @return List of project objects
     */
    @SuppressWarnings("unchecked")
    public List<Project> getProjects() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty project list");
            return null;
        }

        try {
            LangfuseClient client = buildDefaultClient();
            if (client == null) {
                return null;
            }
            ProjectsClient pips = client.projects();
            return pips.get().getData();
        } catch (LangfuseClientApiException error) {
            System.out.println("ERRORE" + error.getMessage());
            return null;
        }

    }

    /**
     * Get a specific project by ID.
     * 
     * @param projectId Project ID
     * @return Project object or null if not found
     */
    @SuppressWarnings("unchecked")
    public Project getProjectById(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }
        LangfuseClient client = buildDefaultClient();
        if (client == null) {
            return null;
        }
        Project project = client.projects().get().getData().stream().filter(p -> p.getId().equals(projectId))
                .findFirst().orElse(null);
        if (project != null) {
            logger.info("Retrieved project {} from Langfuse", projectId);
        }
        return project;
    }

    public List<TraceWithDetails> getTraces(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse traces: agent is null");
            return null;
        }

        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return null;
        }

        return client.trace().list(GetTracesRequest.builder().name(agent.getLlangfuseProjectName()).build()).getData();
    }

    /**
     * Create a new project in Langfuse.
     * 
     * @param projectName Name of the project
     * @param description Optional description
     * @return Created project object or null if failed
     * @throws IllegalArgumentException if projectName is null or empty
     */
    @SuppressWarnings("unchecked")
    public Project createProject(String projectName, String description) {
        // TO BE IMPLEMENTED
        LangfuseClient client = buildDefaultClient();
        if (client == null) {
            return null;
        }
        Project project = client.projects()
                .create(CreateProjectRequest.builder().name("agent name").retention(3).build());

        return project;
    }

    public void getMetrics(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse metrics: agent is null");
            return;
        }

        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return;
        }
        String query = """
                {
                  "view": "traces",
                  "metrics": [
                    {"measure": "latency_sec", "aggregation": "count"}
                  ],
                  "dimensions": [
                    {"field": "name"}
                  ],
                  "filters": [],
                  "fromTimestamp": "2025-05-01T00:00:00Z",
                  "toTimestamp": "2025-12-13T00:00:00Z"
                }
                """;
        MetricsResponse response = client.metrics()
                .metrics(GetMetricsRequest.builder().query(query).build());
        List<Map<String, Object>> metricsData = response.getData();
        for (Map<String, Object> row : metricsData) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                logger.info(entry.getKey() + ": " + entry.getValue());
            }
        }
        // TO BE IMPLEMENTED
    }

    public void getMetrics2(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse metrics: agent is null");
            return;
        }

        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return;
        }
        List<TraceWithDetails> traces = getTraces(agent);
        for (TraceWithDetails trace : traces) {
            for (String scoreId : trace.getScores()) {
                try {

                    Score score = client.scoreV2().getById(scoreId);
                    score.getNumeric().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        logger.info("Trace {} - {}: {}", trace.getId(), scoreName, scoreValue);
                    });
                     score.getBoolean().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        logger.info("Trace {} - {}: {}", trace.getId(), scoreName, scoreValue);
                    });
                } catch (LangfuseClientApiException ex) {
                    logger.warn("Failed to load score {} for trace {}: {}", scoreId, trace.getId(), ex.getMessage());
                }
            }
        }
    }

    private void logLatencyScore(TraceWithDetails trace, NumericScore numericScore) {
        if ("latency_sec".equals(numericScore.getName())) {
            logger.info("Trace {} - latency_sec score: {}", trace.getId(), numericScore.getValue());
        }
    }

    public List<Dataset> getDatasets() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty dataset list");
            return null;
        }

        try {
            LangfuseClient client = buildDefaultClient();
            if (client == null) {
                return null;
            }
            return client.datasets().list().getData();
        } catch (LangfuseClientApiException error) {
            System.out.println("ERRORE" + error.getMessage());
            return null;
        }

    }

    public DatasetItem createDatasetItems(String datasetName, String input, String expectedOutput) {
        if (datasetName == null || datasetName.isBlank()) {
            logger.warn("Cannot create dataset item: dataset name is missing");
            return null;
        }

        LangfuseClient client = buildDefaultClient();
        if (client == null) {
            return null;
        }
        DatasetItem createdItems = client.datasetItems().create(
                CreateDatasetItemRequest.builder()
                        .datasetName(datasetName)
                        .input(input)
                        .expectedOutput(expectedOutput)
                        .build());

        return createdItems;
    }

    public Dataset createDataset(String datasetName, String description) {
        // TO BE IMPLEMENTED
        LangfuseClient client = buildDefaultClient();
        if (client == null) {
            return null;
        }
        Dataset dataset = client.datasets()
                .create(CreateDatasetRequest.builder().name(datasetName).description(description).build());

        return dataset;
    }

    private LangfuseClient buildDefaultClient() {
        if (!properties.isConfigured()) {
            logger.warn("Langfuse properties are not configured");
            return null;
        }
        return buildClient(properties.getBaseUrl(), properties.getPublicKey(), properties.getSecretKey());
    }

    private LangfuseClient buildClient(String baseUrl, String publicKey, String secretKey) {
        if (baseUrl == null || publicKey == null || secretKey == null) {
            logger.warn("Missing Langfuse credentials, cannot create client");
            return null;
        }
        return LangfuseClient.builder()
                .url(baseUrl)
                .credentials(publicKey, secretKey)
                .build();
    }
}
