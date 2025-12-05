package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.ArrayList;
import java.util.Collections;
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
import com.langfuse.client.resources.commons.types.DatasetRunWithItems;
import com.langfuse.client.resources.commons.types.Score;
import com.langfuse.client.resources.commons.types.Trace;
import com.langfuse.client.resources.commons.types.NumericScore;
import com.langfuse.client.resources.commons.types.TraceWithDetails;
import com.langfuse.client.resources.commons.types.TraceWithFullDetails;
import com.langfuse.client.resources.datasetitems.types.CreateDatasetItemRequest;
import com.langfuse.client.resources.datasets.types.CreateDatasetRequest;
import com.langfuse.client.resources.datasets.types.PaginatedDatasets;
import com.langfuse.client.resources.metrics.requests.GetMetricsRequest;
import com.langfuse.client.resources.metrics.types.MetricsResponse;
import com.langfuse.client.resources.projects.ProjectsClient;
import com.langfuse.client.resources.projects.requests.CreateProjectRequest;
import com.langfuse.client.resources.projects.types.Project;
import com.langfuse.client.resources.scorev2.requests.GetScoresRequest;
import com.langfuse.client.resources.scorev2.types.GetScoresResponse;
import com.langfuse.client.resources.trace.requests.GetTracesRequest;
import com.langfuse.client.resources.utils.pagination.types.MetaResponse;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;

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
            return Collections.emptyList();
        }

        try {
            LangfuseClient client = buildDefaultClient();
            if (client == null) {
                return Collections.emptyList();
            }
            ProjectsClient pips = client.projects();
            return pips.get().getData();
        } catch (LangfuseClientApiException error) {
            if (isNotFound(error)) {
                logNotFound("listing Langfuse projects", error);
                return Collections.emptyList();
            }
            logger.warn("Failed to load Langfuse projects: {}", error.getMessage());
            return Collections.emptyList();
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
        try {
            Project project = client.projects().get().getData().stream().filter(p -> p.getId().equals(projectId))
                    .findFirst().orElse(null);
            if (project != null) {
                logger.info("Retrieved project {} from Langfuse", projectId);
            }
            return project;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("fetching project " + projectId, ex);
                return null;
            }
            throw ex;
        }
    }

    public List<TraceWithDetails> getTraces(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse traces: agent is null");
            return Collections.emptyList();
        }

        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return Collections.emptyList();
        }
        try {
            MetaResponse numb_of_pages = client.trace().list(GetTracesRequest.builder().name(agent.getLlangfuseProjectName()).build()).getMeta();
            List<TraceWithDetails> traces = new ArrayList<>();
            for (int i = 1; i <= numb_of_pages.getTotalPages(); i++) {
                traces.addAll(client.trace().list(GetTracesRequest.builder().page(i).name(agent.getLlangfuseProjectName()).build()).getData());
            }

            return traces;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("loading traces for project " + agent.getLlangfuseProjectName(), ex);
                return Collections.emptyList();
            }
            throw ex;
        }
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
        try {
            Project project = client.projects()
                    .create(CreateProjectRequest.builder().name("agent name").retention(3).build());

            return project;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("creating project " + projectName, ex);
                return null;
            }
            throw ex;
        }
    }

    // public void getMetrics(Agent agent) {
    //     if (agent == null) {
    //         logger.warn("Cannot load Langfuse metrics: agent is null");
    //         return;
    //     }

    //     LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
    //             agent.getLlangfuseSecretKey());
    //     if (client == null) {
    //         return;
    //     }
    //     String query = """
    //             {
    //               "view": "traces",
    //               "metrics": [
    //                 {"measure": "latency_sec", "aggregation": "count"}
    //               ],
    //               "dimensions": [
    //                 {"field": "name"}
    //               ],
    //               "filters": [],
    //               "fromTimestamp": "2025-05-01T00:00:00Z",
    //               "toTimestamp": "2025-12-13T00:00:00Z"
    //             }
    //             """;
    //     MetricsResponse response = client.metrics()
    //             .metrics(GetMetricsRequest.builder().query(query).build());
    //     List<Map<String, Object>> metricsData = response.getData();
    //     for (Map<String, Object> row : metricsData) {
    //         for (Map.Entry<String, Object> entry : row.entrySet()) {
    //             logger.info(entry.getKey() + ": " + entry.getValue());
    //         }
    //     }
    //     // TO BE IMPLEMENTED
    // }

    public List<Metric> getMetrics(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse metrics: agent is null");
            return new ArrayList<>();
        }
        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return new ArrayList<>();
        }
        List<TraceWithDetails> traces = getTraces(agent);
        List<Metric> metrics = new ArrayList<>();
        for (TraceWithDetails trace : traces) {
            for (String scoreId : trace.getScores()) {
                try {
                    Score score = client.scoreV2().getById(scoreId);
                    score.getNumeric().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        Metric metricRecord = new Metric();
                        metricRecord.setName(scoreName);
                        metricRecord.setFloatValue(scoreValue.floatValue());
                        metrics.add(metricRecord);
                    });
                     score.getBoolean().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        Metric metricRecord = new Metric();
                        metricRecord.setName(scoreName);
                        metricRecord.setBooleanValue(scoreValue.intValue() != 0);
                    });
                } catch (LangfuseClientApiException ex) {
                    if (isNotFound(ex)) {
                        logNotFound("loading score " + scoreId, ex);
                    } else {
                        logger.warn("Failed to load score {} for trace {}: {}", scoreId, trace.getId(), ex.getMessage());
                    }
                }
            }
        }
        return metrics;
    }

    public List<Metric> getMetrics(Agent agent, String metric) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse metrics: agent is null");
            return new ArrayList<>();
        }

        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        if (client == null) {
            return new ArrayList<>();
        }
        List<TraceWithDetails> traces = getTraces(agent);
        List<Metric> metrics = new ArrayList<>();
        for (TraceWithDetails trace : traces) {
            for(String scoreId : trace.getScores())
                try {
                    Score score = client.scoreV2().getById(scoreId);
                    Metric metricRecord = new Metric();
                    score.getNumeric().ifPresent(numericScore -> { 
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        if(metric.equals(scoreName)){
                            metricRecord.setName(scoreName);
                            metricRecord.setFloatValue(scoreValue.floatValue());
                        }
                    });
                        score.getBoolean().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        if(metric.equals(scoreName)){
                            metricRecord.setName(scoreName);
                            metricRecord.setBooleanValue(scoreValue.intValue() != 0);   
                        }
                    });
                    metrics.add(metricRecord);
                } catch (LangfuseClientApiException ex) {
                    if (isNotFound(ex)) {
                        logNotFound("loading score for trace " + trace.getId(), ex);
                    } else {
                        logger.warn("Failed to load score for trace {}: {}", trace.getId(), ex.getMessage());
                    }
                }
        }
        return metrics;
    }


    public List<TraceWithFullDetails> getRunBenchmarkTraces(Agent agent, String datasetName, String runName) {
        // TO BE IMPLEMENTED
        LangfuseClient client = buildClient(agent.getLlangfuseUrl(), agent.getLlangfusePublicKey(),
                agent.getLlangfuseSecretKey());
        List<TraceWithFullDetails> traces = new ArrayList<>();
        if (client == null) {
            return traces;
        }
        try {
            DatasetRunWithItems datasetRun = client.datasets().getRun(datasetName, runName, RequestOptions.builder().build());
            datasetRun.getDatasetRunItems().forEach(item -> {
                try {
                    DatasetItem datasetItem = client.datasetItems().get(item.getDatasetItemId());
                    TraceWithFullDetails trace = client.trace().get(item.getTraceId());
                    trace.getAdditionalProperties().put("expected", datasetItem.getExpectedOutput());
                    traces.add(trace);
                } catch (LangfuseClientApiException ex) {
                    if (isNotFound(ex)) {
                        logNotFound("loading trace " + item.getTraceId(), ex);
                    } else {
                        throw ex;
                    }
                }
            });
            return traces;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("loading dataset run " + runName, ex);
                return traces;
            }
            throw ex;
        }
    }
    
    public List<Dataset> getDatasets() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty dataset list");
            return Collections.emptyList();
        }

        try {
            LangfuseClient client = buildDefaultClient();
            if (client == null) {
                return Collections.emptyList();
            }
            return client.datasets().list().getData();
        } catch (LangfuseClientApiException error) {
            if (isNotFound(error)) {
                logNotFound("listing datasets", error);
                return Collections.emptyList();
            }
            logger.warn("Failed to load datasets: {}", error.getMessage());
            return Collections.emptyList();
        }

    }
    public Dataset getDataset(String dataset) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty dataset list");
            return null;
        }

        try {
            LangfuseClient client = buildDefaultClient();
            if (client == null) {
                return null;
            }
            return client.datasets().get(dataset);
        } catch (LangfuseClientApiException error) {
            if (isNotFound(error)) {
                logNotFound("fetching dataset " + dataset, error);
                return null;
            }
            logger.warn("Failed to load dataset {}: {}", dataset, error.getMessage());
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
        try {
            DatasetItem createdItems = client.datasetItems().create(
                    CreateDatasetItemRequest.builder()
                            .datasetName(datasetName)
                            .input(input)
                            .expectedOutput(expectedOutput)
                            .build());

            return createdItems;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("creating dataset item in " + datasetName, ex);
                return null;
            }
            throw ex;
        }
    }

    public Dataset createDataset(String datasetName, String description) {
        // TO BE IMPLEMENTED
        LangfuseClient client = buildDefaultClient();
        if (client == null) {
            return null;
        }
        try {
            Dataset dataset = client.datasets()
                    .create(CreateDatasetRequest.builder().name(datasetName).description(description).build());

            return dataset;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                logNotFound("creating dataset " + datasetName, ex);
                return null;
            }
            throw ex;
        }
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

    private boolean isNotFound(LangfuseClientApiException ex) {
        return ex != null && ex.statusCode() == 404;
    }

    private void logNotFound(String action, LangfuseClientApiException ex) {
        logger.info("Langfuse returned 404 while {}: {}", action, ex.getMessage());
    }
}
