package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.service.RecommendationService;

/**
 * Controller for model recommendations based on user context.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
public class RecommendationController {

    Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    @Autowired
    private final RecommendationService recommendationService;
    
    public RecommendationController(@Autowired RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    
    /**
     * Get model recommendations based on the provided context.
     * 
     * @param context The user context containing task information, requirements and preferences
     * @return A list of recommended models
     */
    @PostMapping("/recommendations")
    public ResponseEntity<List<Model>> getRecommendations(@RequestBody Context context) {
        logger.info("POST /recommendations for context: task={}, type={}", context.getTaskId(), context.getTaskType());
        List<Model> recommendations = recommendationService.recommendModels(context);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * Get model recommendations for a specific task type.
     * 
     * @param taskId The ID of the task
     * @param taskType The type of task
     * @return A list of recommended models suitable for the task
     */
    @PostMapping("/recommendations/task")
    public ResponseEntity<List<Model>> getRecommendationsForTask(
            @RequestParam String taskId,
            @RequestParam String taskType) {
        logger.info("POST /recommendations/task for taskId={}, taskType={}", taskId, taskType);
        List<Model> recommendations = recommendationService.recommendModelsForTask(taskId, taskType);
        return ResponseEntity.ok(recommendations);
    }
    
    /**
     * Provide feedback on a recommendation.
     * 
     * @param modelId The ID of the recommended model
     * @param context The context in which the recommendation was made
     * @param rating The user's rating (1-5)
     * @param feedback Optional feedback text
     * @return Confirmation response
     */
    @PostMapping("/recommendations/feedback")
    public ResponseEntity<?> provideFeedback(
            @RequestParam String modelId,
            @RequestBody Context context,
            @RequestParam int rating,
            @RequestParam(required = false) String feedback) {
        logger.info("POST /recommendations/feedback for modelId={}, rating={}", modelId, rating);
        recommendationService.provideFeedback(modelId, context, rating, feedback);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Recommend a MAS architecture based on the user's context.
     * 
     * @param context The user context containing requirements
     * @return A map with architectural recommendations
     */
    @PostMapping("/recommendations/architecture")
    public ResponseEntity<Map<String, Object>> recommendArchitecture(@RequestBody Context context) {
        logger.info("POST /recommendations/architecture for context: domain={}, complexity={}", 
                context.getDomainArea(), context.getComplexity());
        Map<String, Object> architecture = recommendationService.recommendMasArchitecture(context);
        return ResponseEntity.ok(architecture);
    }
    
    /**
     * Find similar MAS projects to the one being developed.
     * 
     * @param context The context describing the current project
     * @param limit Maximum number of similar projects to return
     * @return A list of similar projects with similarity scores
     */
    @PostMapping("/recommendations/similar-projects")
    public ResponseEntity<List<Map<String, Object>>> findSimilarProjects(
            @RequestBody Context context,
            @RequestParam(defaultValue = "3") int limit) {
        logger.info("POST /recommendations/similar-projects for context: domain={}", context.getDomainArea());
        List<Map<String, Object>> similarProjects = recommendationService.findSimilarMasProjects(context, limit);
        return ResponseEntity.ok(similarProjects);
    }
    
    /**
     * Recommend communication protocols based on the user's context.
     * 
     * @param context The user context containing requirements
     * @return A list of recommended communication protocols
     */
    @PostMapping("/recommendations/communication-protocols")
    public ResponseEntity<List<CommunicationProtocol>> recommendCommunicationProtocols(@RequestBody Context context) {
        logger.info("POST /recommendations/communication-protocols for context: distributed={}", 
                context.isDistributedExecution());
        List<CommunicationProtocol> protocols = recommendationService.recommendCommunicationProtocols(context);
        return ResponseEntity.ok(protocols);
    }
    
    /**
     * Recommend coordination patterns based on the user's context and agent types.
     * 
     * @param context The user context containing requirements
     * @param agentTypes The types of agents in the system
     * @return A list of recommended coordination patterns
     */
    @PostMapping("/recommendations/coordination-patterns")
    public ResponseEntity<List<CoordinationPattern>> recommendCoordinationPatterns(
            @RequestBody Context context,
            @RequestParam List<String> agentTypes) {
        logger.info("POST /recommendations/coordination-patterns for context: complexity={}, agents={}", 
                context.getComplexity(), agentTypes);
        List<CoordinationPattern> patterns = recommendationService.recommendCoordinationPatterns(context, agentTypes);
        return ResponseEntity.ok(patterns);
    }
    
    /**
     * Recommend agent definitions based on the user's context.
     * 
     * @param context The user context containing requirements
     * @return A list of recommended agent definitions
     */
    @PostMapping("/recommendations/agent-definitions")
    public ResponseEntity<List<AgentDefinition>> recommendAgentDefinitions(@RequestBody Context context) {
        logger.info("POST /recommendations/agent-definitions for context: domain={}, complexity={}", 
                context.getDomainArea(), context.getComplexity());
        List<AgentDefinition> agents = recommendationService.recommendAgentDefinitions(context);
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Recommend technologies and frameworks for MAS development.
     * 
     * @param context The user context containing requirements
     * @return A map of recommended technologies by category
     */
    @PostMapping("/recommendations/technologies")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> recommendTechnologies(@RequestBody Context context) {
        logger.info("POST /recommendations/technologies for context: domain={}", context.getDomainArea());
        Map<String, List<Map<String, Object>>> technologies = 
                recommendationService.recommendTechnologiesAndFrameworks(context);
        return ResponseEntity.ok(technologies);
    }
    
    /**
     * Get telemetry insights for similar MAS deployments.
     * 
     * @param context The user context describing the MAS being developed
     * @return A map containing telemetry-based insights
     */
    @PostMapping("/recommendations/telemetry-insights")
    public ResponseEntity<Map<String, Object>> getTelemetryInsights(@RequestBody Context context) {
        logger.info("POST /recommendations/telemetry-insights for context: domain={}", context.getDomainArea());
        Map<String, Object> insights = recommendationService.getTelemetryInsights(context);
        return ResponseEntity.ok(insights);
    }
}
