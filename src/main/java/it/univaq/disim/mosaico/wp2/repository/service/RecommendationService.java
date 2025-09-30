package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Map;

import it.univaq.disim.mosaico.wp2.repository.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;

/**
 * Service interface for generating model recommendations based on user context.
 * 
 * Copyright 2025 Mosaico
 */
public interface RecommendationService {
    
    /**
     * Finds suitable models based on the provided user context.
     * 
     * @param context The user's current context and requirements
     * @return A list of recommended models sorted by relevance
     */
    List<Model> recommendModels(Context context);
    
    /**
     * Finds suitable models for a specific task.
     * 
     * @param taskId The ID of the task
     * @param taskType The type of task
     * @return A list of recommended models sorted by relevance
     */
    List<Model> recommendModelsForTask(String taskId, String taskType);
    
    /**
     * Finds suitable models based on required capabilities.
     * 
     * @param capabilities List of required capabilities
     * @return A list of models that satisfy the required capabilities
     */
    List<Model> findModelsByCapabilities(List<String> capabilities);
    
    /**
     * Recommends the most suitable MAS architecture based on the provided context.
     * 
     * @param context The user's current context and requirements
     * @return A map containing architectural recommendations (types, patterns, components)
     */
    Map<String, Object> recommendMasArchitecture(Context context);
    
    /**
     * Finds existing MAS projects that are similar to the one being developed.
     * 
     * @param context The context of the project under development
     * @param limit Maximum number of similar projects to return
     * @return A list of similar projects with similarity scores
     */
    List<Map<String, Object>> findSimilarMasProjects(Context context, int limit);
    
    /**
     * Recommends communication protocols based on the provided context and telemetry data.
     * 
     * @param context The user's current context
     * @return A list of recommended communication protocols
     */
    List<CommunicationProtocol> recommendCommunicationProtocols(Context context);
    
    /**
     * Recommends coordination patterns based on the provided context and agent interactions.
     * 
     * @param context The user's current context
     * @param agentTypes The types of agents in the system
     * @return A list of recommended coordination patterns
     */
    List<CoordinationPattern> recommendCoordinationPatterns(Context context, List<String> agentTypes);
    
    /**
     * Recommends agent definitions based on the task requirements and system goals.
     * 
     * @param context The user's current context
     * @return A list of recommended agent definitions
     */
    List<AgentDefinition> recommendAgentDefinitions(Context context);
    
    /**
     * Recommends technologies and frameworks for MAS development based on telemetry data
     * and project requirements.
     * 
     * @param context The user's current context
     * @return A map of recommended technologies by category (e.g., "development", "testing", "deployment")
     */
    Map<String, List<Map<String, Object>>> recommendTechnologiesAndFrameworks(Context context);
    
    /**
     * Provides insights from telemetry data about similar MAS deployments,
     * including performance metrics, resource usage, and common issues.
     * 
     * @param context The user's current context
     * @return A map containing telemetry-based insights
     */
    Map<String, Object> getTelemetryInsights(Context context);
    
    /**
     * Updates the recommendation engine with feedback on a recommendation.
     * 
     * @param modelId The ID of the model that was recommended
     * @param context The context in which it was recommended
     * @param rating The user's rating of the recommendation (1-5)
     * @param feedback Optional feedback text
     */
    void provideFeedback(String modelId, Context context, int rating, String feedback);
}
