package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import com.langfuse.client.LangfuseClient;
import com.langfuse.client.core.LangfuseClientApiException;
import com.langfuse.client.resources.commons.types.Dataset;
import com.langfuse.client.resources.commons.types.DatasetItem;
import com.langfuse.client.resources.commons.types.TraceWithDetails;
import com.langfuse.client.resources.datasetitems.types.CreateDatasetItemRequest;
import com.langfuse.client.resources.datasets.types.CreateDatasetRequest;
import com.langfuse.client.resources.projects.ProjectsClient;
import com.langfuse.client.resources.projects.requests.CreateProjectRequest;
import com.langfuse.client.resources.projects.types.Project;
/**
 * Service for managing Langfuse projects.
 * Provides methods to retrieve and manage projects from Langfuse API.
 */
@Service
public class LangfuseService {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseService.class);

    private final LangfuseProperties properties;
    private final LangfuseClient client;

    public LangfuseService(LangfuseProperties properties) {
        this.properties = properties;
        
        if (properties.isConfigured()) {
            // String auth = properties.getPublicKey() + ":" + properties.getSecretKey();
            // String authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
            
            // this.webClient = WebClient.builder()
            //         .baseUrl(properties.getBaseUrl())
            //         .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
            //         .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            //         .build();
            client = LangfuseClient.builder().url(properties.getBaseUrl())
                    .credentials(properties.getPublicKey(), properties.getSecretKey()).build();        
            logger.info("Langfuse project service initialized: {}", properties.getBaseUrl());
        } else {
            this.client = null;
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
    public Project getProjectById(String projectId) {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }
        Project project = client.projects().get().getData().stream().filter(p -> p.getId().equals(projectId)).findFirst().orElse(null);
        if (project != null) {
            logger.info("Retrieved project {} from Langfuse", projectId);
        }
        return project;
    }



    public List<TraceWithDetails> getTraces() {
        return client.trace().list().getData();
    }
 
    /**
     * Create a new project in Langfuse.
     * @param projectName Name of the project
     * @param description Optional description
     * @return Created project object or null if failed
     * @throws IllegalArgumentException if projectName is null or empty
     */
    @SuppressWarnings("unchecked")
    public Project createProject(String projectName, String description) {
        //TO BE IMPLEMENTED
        Project project = client.projects().create(CreateProjectRequest.builder().name("agent name").retention(3).build());
        
        return project;
    }

    public List<Dataset> getDatasets() {
        if (!isEnabled()) {
            logger.warn("Langfuse is not enabled, returning empty dataset list");
            return null;
        }

        try {
            return client.datasets().list().getData();
        } catch (LangfuseClientApiException error) {
            System.out.println("ERRORE" + error.getMessage());
            return null;
        }
       
    }

    public DatasetItem createDatasetItems(String datasetId, String input, String expectedOutput) {
        //TO BE IMPLEMENTED
        DatasetItem createdItems = client.datasetItems().create(CreateDatasetItemRequest.builder().datasetName("No Description Dataset 1f1654e7").input(input).expectedOutput(expectedOutput).build());
        
        return createdItems;
    }

    public Dataset createDataset(String datasetName, String description) {
        //TO BE IMPLEMENTED
        Dataset dataset = client.datasets().create(CreateDatasetRequest.builder().name(datasetName).description(description).build());
        
        return dataset;
    }



   
}
