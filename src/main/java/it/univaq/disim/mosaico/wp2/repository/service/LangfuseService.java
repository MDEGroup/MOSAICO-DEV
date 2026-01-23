package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.langfuse.client.LangfuseClient;
import com.langfuse.client.core.LangfuseClientApiException;
import com.langfuse.client.core.RequestOptions;
import com.langfuse.client.resources.commons.types.Dataset;
import com.langfuse.client.resources.commons.types.DatasetItem;
import com.langfuse.client.resources.commons.types.DatasetRunWithItems;
import com.langfuse.client.resources.commons.types.Score;
import com.langfuse.client.resources.commons.types.TraceWithDetails;
import com.langfuse.client.resources.commons.types.TraceWithFullDetails;
import com.langfuse.client.resources.datasetitems.types.CreateDatasetItemRequest;
import com.langfuse.client.resources.datasets.types.CreateDatasetRequest;
import com.langfuse.client.resources.projects.ProjectsClient;
import com.langfuse.client.resources.projects.requests.CreateProjectRequest;
import com.langfuse.client.resources.projects.types.Project;
import com.langfuse.client.resources.trace.requests.GetTracesRequest;
import com.langfuse.client.resources.utils.pagination.types.MetaResponse;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import com.langfuse.client.resources.commons.types.ScoreV1;

/**
 * Service for managing Langfuse projects.
 * Provides methods to retrieve and manage projects from Langfuse API.
 */
@Service
public class LangfuseService {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseService.class);

    private final LangfuseProperties properties;
    private final Map<String, LangfuseClient> agentClientCache = new ConcurrentHashMap<>();

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
                // TODO enable logging after demo
                //logNotFound("listing Langfuse projects", error);
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
                // TODO enable logging after demo
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

        LangfuseClient client = buildClientForAgent(agent);
        if (client == null) {
            return Collections.emptyList();
        }
        try {
            MetaResponse numb_of_pages = client.trace()
                    .list(GetTracesRequest.builder().name(agent.getLlangfuseProjectName()).build()).getMeta();
            List<TraceWithDetails> traces = new ArrayList<>();
            for (int i = 1; i <= numb_of_pages.getTotalPages(); i++) {
                traces.addAll(client.trace()
                        .list(GetTracesRequest.builder().page(i).name(agent.getLlangfuseProjectName()).build())
                        .getData());
            }

            return traces;
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                // TODO enable logging after demo
                //logNotFound("loading traces for project " + agent.getLlangfuseProjectName(), ex);
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
    // if (agent == null) {
    // logger.warn("Cannot load Langfuse metrics: agent is null");
    // return;
    // }

    // LangfuseClient client = buildClient(agent.getLlangfuseUrl(),
    // agent.getLlangfusePublicKey(),
    // agent.getLlangfuseSecretKey());
    // if (client == null) {
    // return;
    // }
    // String query = """
    // {
    // "view": "traces",
    // "metrics": [
    // {"measure": "latency_sec", "aggregation": "count"}
    // ],
    // "dimensions": [
    // {"field": "name"}
    // ],
    // "filters": [],
    // "fromTimestamp": "2025-05-01T00:00:00Z",
    // "toTimestamp": "2025-12-13T00:00:00Z"
    // }
    // """;
    // MetricsResponse response = client.metrics()
    // .metrics(GetMetricsRequest.builder().query(query).build());
    // List<Map<String, Object>> metricsData = response.getData();
    // for (Map<String, Object> row : metricsData) {
    // for (Map.Entry<String, Object> entry : row.entrySet()) {
    // logger.info(entry.getKey() + ": " + entry.getValue());
    // }
    // }
    // // TO BE IMPLEMENTED
    // }

    public List<Metric> getMetrics(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot load Langfuse metrics: agent is null");
            return new ArrayList<>();
        }
        LangfuseClient client = buildClientForAgent(agent);
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
                        logger.warn("Failed to load score {} for trace {}: {}", scoreId, trace.getId(),
                                ex.getMessage());
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

        LangfuseClient client = buildClientForAgent(agent);
        if (client == null) {
            return new ArrayList<>();
        }
        List<TraceWithDetails> traces = getTraces(agent);
        List<Metric> metrics = new ArrayList<>();
        for (TraceWithDetails trace : traces) {
            for (String scoreId : trace.getScores())
                try {
                    Score score = client.scoreV2().getById(scoreId);
                    Metric metricRecord = new Metric();
                    score.getNumeric().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        if (metric.equals(scoreName)) {
                            metricRecord.setName(scoreName);
                            metricRecord.setFloatValue(scoreValue.floatValue());
                        }
                    });
                    score.getBoolean().ifPresent(numericScore -> {
                        String scoreName = numericScore.getName();
                        Double scoreValue = numericScore.getValue();
                        if (metric.equals(scoreName)) {
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

    // public List<TraceWithFullDetails> getRunBenchmarkTraces(Agent agent, String datasetName, String runName) {
    //     LangfuseClient client = buildClientForAgent(agent);
    //     List<TraceWithFullDetails> traces = new ArrayList<>();
    //     if (client == null) {
    //         return traces;
    //     }
    //     try {
    //         DatasetRunWithItems datasetRun = client.datasets().getRun(datasetName, runName,
    //                 RequestOptions.builder().build());
    //         datasetRun.getDatasetRunItems().forEach(item -> {
    //             try {
    //                 DatasetItem datasetItem = client.datasetItems().get(item.getDatasetItemId());
    //                 TraceWithFullDetails trace = client.trace().get(item.getTraceId());
    //                 trace.getAdditionalProperties().put("expected", datasetItem.getExpectedOutput());
    //                 traces.add(trace);
    //             } catch (LangfuseClientApiException ex) {
    //                 if (isNotFound(ex)) {
    //                     logNotFound("loading trace " + item.getTraceId(), ex);
    //                 } else {
    //                     throw ex;
    //                 }
    //             }
    //         });
    //         return traces;
    //     } catch (LangfuseClientApiException ex) {
    //         if (isNotFound(ex)) {
    //             logNotFound("loading dataset run " + runName, ex);
    //             return traces;
    //         }
    //         throw ex;
    //     }
    // }

    public List<TraceData> fetchTracesFromRun(Agent agent, String datasetName, String runName) {
        List<TraceData> result = new ArrayList<>();
        LangfuseClient langfuseClient = buildClientForAgent(agent);
        if (langfuseClient == null) {
            return result;
        }
        if (datasetName == null || datasetName.isBlank()) {
            logger.warn("Dataset name is required to fetch Langfuse traces");
            return result;
        }
        if (runName == null || runName.isBlank()) {
            logger.warn("Run name is required to fetch Langfuse traces");
            return result;
        }
        try {
            DatasetRunWithItems datasetRun = langfuseClient.datasets()
                    .getRun(datasetName, runName, RequestOptions.builder().build());

            for (var item : datasetRun.getDatasetRunItems()) {
                try {
                    DatasetItem datasetItem = getDatasetItem(agent, item.getDatasetItemId());
                    TraceWithFullDetails trace = getTraceWithFullDetails(agent, item.getTraceId());

                    if (datasetItem == null || trace == null) {
                        continue;
                    }

                    TraceData traceData = new TraceData();
                    traceData.traceId = trace.getId();
                    traceData.trace = trace;
                    traceData.input = extractInput(datasetItem);
                    traceData.expectedOutput = extractExpectedOutput(datasetItem);
                    traceData.generatedOutput = extractGeneratedOutput(trace);

                    // Fetch scores from Langfuse using generalized method
                    traceData.langfuseScores = getScoresForTrace(trace);

                    result.add(traceData);
                } catch (LangfuseClientApiException ex) {
                    logger.debug("Could not load trace item: {}", ex.getMessage());
                }
            }
        } catch (LangfuseClientApiException ex) {
            logger.warn("  Impossibile caricare run " + runName + ": " + ex.getMessage());
        }

        return result;
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
                // TODO enable logging after demo
                //logNotFound("creating dataset " + datasetName, ex);
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

    private LangfuseClient buildClientForAgent(Agent agent) {
        if (agent == null) {
            logger.warn("Cannot create Langfuse client: agent is null");
            return null;
        }

        logger.debug("Building Langfuse client for agent {}. Agent credentials: url={}, publicKey={}, secretKey={}",
                agent.getId(),
                agent.getLlangfuseUrl(),
                agent.getLlangfusePublicKey() != null ? agent.getLlangfusePublicKey().substring(0, Math.min(10, agent.getLlangfusePublicKey().length())) + "..." : "null",
                agent.getLlangfuseSecretKey() != null ? "***" : "null");

        String baseUrl = StringUtils.hasText(agent.getLlangfuseUrl())
                ? agent.getLlangfuseUrl()
                : properties.getBaseUrl();
        String publicKey = StringUtils.hasText(agent.getLlangfusePublicKey())
                ? agent.getLlangfusePublicKey()
                : properties.getPublicKey();
        String secretKey = StringUtils.hasText(agent.getLlangfuseSecretKey())
                ? agent.getLlangfuseSecretKey()
                : properties.getSecretKey();

        if (!StringUtils.hasText(publicKey) || !StringUtils.hasText(secretKey)) {
            logger.warn("Missing Langfuse credentials for agent {} and no global defaults configured", agent.getId());
            return null;
        }

        if (!StringUtils.hasText(baseUrl)) {
            logger.warn("Missing Langfuse base URL for agent {} and no global default configured", agent.getId());
            return null;
        }

        if (!StringUtils.hasText(agent.getLlangfusePublicKey()) || !StringUtils.hasText(agent.getLlangfuseSecretKey())) {
            logger.debug("Falling back to global Langfuse credentials for agent {}", agent.getId());
        }

        logger.debug("Final credentials being used: url={}, publicKey={}", baseUrl,
                publicKey != null ? publicKey.substring(0, Math.min(10, publicKey.length())) + "..." : "null");

        String cacheKey = buildAgentCacheKey(baseUrl, publicKey, secretKey);
        return agentClientCache.computeIfAbsent(cacheKey, key -> {
            logger.debug("Creating cached Langfuse client for agent {} (key hash: {})", agent.getId(), key.hashCode());
            return buildClient(baseUrl, publicKey, secretKey);
        });
    }

    private String buildAgentCacheKey(String baseUrl, String publicKey, String secretKey) {
        return baseUrl + "|" + publicKey + "|" + secretKey;
    }

    private LangfuseClient buildClient(String baseUrl, String publicKey, String secretKey) {
        if (!StringUtils.hasText(baseUrl) || !StringUtils.hasText(publicKey) || !StringUtils.hasText(secretKey)) {
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

    // ═══════════════════════════════════════════════════════════════════════════
    // DATA EXTRACTION UTILITIES
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Extract input text from a DatasetItem.
     * Handles both Map and simple object inputs.
     *
     * @param item DatasetItem to extract input from
     * @return Extracted input as string, or empty string if null
     */
    public String extractInput(DatasetItem item) {
        if (item == null) {
            return "";
        }
        Object input = item.getInput();
        if (input instanceof Map) {
            return ((Map<?, ?>) input).values().stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.joining(" "));
        }
        if(input instanceof Optional) {
            Optional<?> optionalInput = (Optional<?>) input;
            if(optionalInput.isPresent()) {
                Object unwrappedInput = optionalInput.get();
                if(unwrappedInput instanceof Map) {
                    var value = ((Map<?, ?>) unwrappedInput).values().stream()
                            .map(Object::toString)
                            .collect(java.util.stream.Collectors.joining(" "));
                    return value;
                } else {
                    return unwrappedInput.toString();
                }
            } else {
                return "";
            }
        }
        return input != null ? input.toString() : "";
    }

    /**
     * Extract expected output from a DatasetItem.
     * Handles both Map and simple object outputs.
     *
     * @param item DatasetItem to extract expected output from
     * @return Extracted expected output as string, or empty string if null/absent
     */
    public String extractExpectedOutput(DatasetItem item) {
        if (item == null) {
            return "";
        }
        Object expected = item.getExpectedOutput().orElse(null);
        if (expected instanceof Map) {
            return ((Map<?, ?>) expected).values().stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.joining(" "));
        }
        if(expected instanceof Optional) {
            Optional<?> optionalExpected = (Optional<?>) expected;
            if(optionalExpected.isPresent()) {
                Object unwrappedExpected = optionalExpected.get();
                if(unwrappedExpected instanceof Map) {
                    var value = ((Map<?, ?>) unwrappedExpected).values().stream()
                            .map(Object::toString)
                            .collect(java.util.stream.Collectors.joining(" "));
                    return value;
                } else {
                    return unwrappedExpected.toString();
                }
            } else {
                return "";
            }
        }
        return expected != null ? expected.toString() : "";
    }

    /**
     * Extract generated output from a TraceWithFullDetails.
     *
     * @param trace Trace to extract output from
     * @return Generated output as string, or empty string if null
     */
    public String extractGeneratedOutput(TraceWithFullDetails trace) {
        if (trace == null || trace.getOutput() == null) {
            return "";
        }
        Object output = trace.getOutput();
        if(output instanceof Optional) {
            Optional<?> optionalOutput = (Optional<?>) output;
            if(optionalOutput.isPresent()) {
                Object unwrappedOutput = optionalOutput.get();
                if(unwrappedOutput instanceof Map) {
                    var value = ((Map<?, ?>) unwrappedOutput).values().stream()
                            .map(Object::toString)
                            .collect(java.util.stream.Collectors.joining(" "));
                    return value;
                } else {
                    return unwrappedOutput.toString();
                }
            } else {
                return "";
            }
        }
        return trace.getOutput().toString();
    }

    /**
     * Extract all scores from a TraceWithFullDetails as a map.
     * Handles both numeric and boolean ScoreV1 union types.
     *
     * @param trace Trace to extract scores from
     * @return Map of score name to value (Double)
     */
    public Map<String, Double> getScoresForTrace(TraceWithFullDetails trace) {
        Map<String, Double> scores = new java.util.LinkedHashMap<>();
        if (trace == null || trace.getScores() == null) {
            return scores;
        }

        for (ScoreV1 scoreV1 : trace.getScores()) {
            try {
                scoreV1.getNumeric()
                        .ifPresent(numericScore -> scores.put(numericScore.getName(), numericScore.getValue()));
                scoreV1.getBoolean().ifPresent(boolScore -> scores.put(boolScore.getName(), boolScore.getValue()));
            } catch (Exception ex) {
                logger.debug("Could not process score: {}", ex.getMessage());
            }
        }

        return scores;
    }

    /**
     * Get a specific DatasetItem by ID.
     *
     * @param agent  Agent with Langfuse credentials
     * @param itemId DatasetItem ID
     * @return DatasetItem or null if not found
     */
    public DatasetItem getDatasetItem(Agent agent, String itemId) {
        if (agent == null || itemId == null) {
            return null;
        }
        LangfuseClient client = buildClientForAgent(agent);
        if (client == null) {
            return null;
        }
        try {
            return client.datasetItems().get(itemId);
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                // TODO enable logging after demo
                //logNotFound("fetching dataset item " + itemId, ex);
                return null;
            }
            throw ex;
        }
    }

    /**
     * Get a specific trace with full details by ID.
     *
     * @param agent   Agent with Langfuse credentials
     * @param traceId Trace ID
     * @return TraceWithFullDetails or null if not found
     */
    public TraceWithFullDetails getTraceWithFullDetails(Agent agent, String traceId) {
        if (agent == null || traceId == null) {
            return null;
        }
        LangfuseClient client = buildClientForAgent(agent);
        if (client == null) {
            return null;
        }
        try {
            return client.trace().get(traceId);
        } catch (LangfuseClientApiException ex) {
            if (isNotFound(ex)) {
                // TODO enable logging after demo
                //logNotFound("fetching trace " + traceId, ex);
                return null;
            }
            throw ex;
        }
    }

    public static class TraceData {
        public String traceId;
        public TraceWithFullDetails trace;
        public String input;
        public String expectedOutput;
        public String generatedOutput;
        public Map<String, Double> langfuseScores = new LinkedHashMap<>();
    }
}
