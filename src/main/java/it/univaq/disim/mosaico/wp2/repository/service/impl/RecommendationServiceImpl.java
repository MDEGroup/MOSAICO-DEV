package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.dto.Context;
import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CommunicationProtocolRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.CoordinationPatternRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.service.RecommendationService;

/**
 * Implementation of the RecommendationService for suggesting models based on user context.
 * 
 * Copyright 2025 Mosaico
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private AgentDefinitionRepository agentDefinitionRepository;
    
    @Autowired
    private CoordinationPatternRepository coordinationPatternRepository;
    
    @Autowired
    private CommunicationProtocolRepository communicationProtocolRepository;
    
    // In-memory store of user feedback for recommendations (in a production environment,
    // this would be stored in a database)
    private Map<String, List<Map<String, Object>>> feedbackStore = new HashMap<>();
    
    // In-memory store of telemetry data from deployed MAS systems
    // In a production environment, this would be stored in a database or time-series database
    private List<Map<String, Object>> telemetryStore = new ArrayList<>();

    @Override
    public List<Model> recommendModels(Context context) {
        // Retrieve all models
        List<Model> allModels = modelRepository.findAll();
        
        // Score each model based on the context
        List<ScoredModel> scoredModels = allModels.stream()
            .map(model -> scoreModelForContext(model, context))
            .filter(scoredModel -> scoredModel.getScore() > 0) // Filter out irrelevant models
            .sorted(Comparator.comparing(ScoredModel::getScore).reversed()) // Sort by score descending
            .collect(Collectors.toList());
        
        // Return just the models, without the scores
        return scoredModels.stream()
            .map(ScoredModel::getModel)
            .collect(Collectors.toList());
    }

    @Override
    public List<Model> recommendModelsForTask(String taskId, String taskType) {
        // Create a minimal context with just task information
        Context context = new Context();
        context.setTaskId(taskId);
        context.setTaskType(taskType);
        
        return recommendModels(context);
    }

    @Override
    public List<Model> findModelsByCapabilities(List<String> capabilities) {
        // Create a context focused on capabilities
        Context context = new Context();
        context.setRequiredCapabilities(capabilities);
        
        return recommendModels(context);
    }

    @Override
    public void provideFeedback(String modelId, Context context, int rating, String feedback) {
        // Store the feedback for future recommendation improvement
        Map<String, Object> feedbackData = new HashMap<>();
        feedbackData.put("modelId", modelId);
        feedbackData.put("context", context);
        feedbackData.put("rating", rating);
        feedbackData.put("feedback", feedback);
        feedbackData.put("timestamp", System.currentTimeMillis());
        
        // Add to feedback store
        if (!feedbackStore.containsKey(modelId)) {
            feedbackStore.put(modelId, new ArrayList<>());
        }
        feedbackStore.get(modelId).add(feedbackData);
        
        // In a real system, we would also update a machine learning model or
        // recommendation algorithm weights based on this feedback
    }
    
    /**
     * Scores a model based on how well it matches the given context.
     * 
     * @param model The model to score
     * @param context The context to match against
     * @return A ScoredModel containing the model and its score
     */
    private ScoredModel scoreModelForContext(Model model, Context context) {
        double score = 0.0;
        
        // Basic scoring logic - in a real system this would be much more sophisticated
        // and likely use machine learning techniques
        
        // Match on task type
        if (context.getTaskType() != null && model.modelType() != null) {
            if (model.modelType().equals(context.getTaskType())) {
                score += 10.0;
            } else if (model.modelType().contains(context.getTaskType()) || 
                       context.getTaskType().contains(model.modelType())) {
                score += 5.0;
            }
        }
        
        // Match on required capabilities
        if (context.getRequiredCapabilities() != null && model.tags() != null) {
            for (String capability : context.getRequiredCapabilities()) {
                if (model.tags().contains(capability)) {
                    score += 5.0;
                }
            }
        }
        
        // Match on domain area
        if (context.getDomainArea() != null && model.tags() != null) {
            if (model.tags().contains(context.getDomainArea())) {
                score += 8.0;
            }
        }
        
        // Match on available tools
        if (context.getAvailableTools() != null && model.tools() != null) {
            for (String availableTool : context.getAvailableTools()) {
                for (var tool : model.tools()) {
                    if (tool.name().equals(availableTool)) {
                        score += 3.0;
                    }
                }
            }
        }
        
        // Match on preferred authors
        if (context.getPreferredAuthors() != null && model.author() != null) {
            if (context.getPreferredAuthors().contains(model.author())) {
                score += 4.0;
            }
        }
        
        // Match on preferred license
        if (context.getPreferredLicense() != null && model.license() != null) {
            if (model.license().equals(context.getPreferredLicense())) {
                score += 3.0;
            }
        }
        
        // Historical data: previously used models get a boost
        if (context.getPreviouslyUsedModels() != null && 
            context.getPreviouslyUsedModels().contains(model.id())) {
            score += 2.0;
        }
        
        // For a production system, we would also incorporate:
        // - User profile and skill matching
        // - Model performance metrics
        // - Collaborative filtering (what similar users preferred)
        // - Contextual constraints (system resources, time constraints)
        
        return new ScoredModel(model, score);
    }
    
    /**
     * Helper class to associate a model with its recommendation score.
     */
    private static class ScoredModel {
        private final Model model;
        private final double score;
        
        public ScoredModel(Model model, double score) {
            this.model = model;
            this.score = score;
        }
        
        public Model getModel() {
            return model;
        }
        
        public double getScore() {
            return score;
        }
    }

    @Override
    public Map<String, Object> recommendMasArchitecture(Context context) {
        Map<String, Object> architectureRecommendation = new HashMap<>();
        
        // 1. Determine the appropriate agent types based on requirements
        List<String> recommendedAgentTypes = new ArrayList<>();
        
        // Analyze the context to determine appropriate agent types
        if (context.isDistributedExecution()) {
            recommendedAgentTypes.add("DistributedAgent");
        }
        
        // Add recommended agent types based on complexity
        if (context.getComplexity() >= 4) {
            recommendedAgentTypes.add("HybridAgent");
        } else if (context.getComplexity() >= 2) {
            recommendedAgentTypes.add("ReactiveAgent");
        } else {
            recommendedAgentTypes.add("SimpleAgent");
        }
        
        // Add domain-specific agent types
        if ("healthcare".equals(context.getDomainArea())) {
            recommendedAgentTypes.add("HealthcareAgent");
        } else if ("finance".equals(context.getDomainArea())) {
            recommendedAgentTypes.add("FinancialAgent");
        }
        
        // 2. Determine appropriate coordination patterns
        List<String> recommendedPatterns = new ArrayList<>();
        
        // Suggest coordination patterns based on system characteristics
        if (context.isDistributedExecution() && context.getComplexity() > 3) {
            recommendedPatterns.add("Hierarchical");
            recommendedPatterns.add("Market-based");
        } else {
            recommendedPatterns.add("Centralized");
            recommendedPatterns.add("Blackboard");
        }
        
        // 3. Suggest communication protocols
        List<String> recommendedProtocols = new ArrayList<>();
        
        // Suggest protocols based on system requirements
        if (context.isDistributedExecution()) {
            recommendedProtocols.add("FIPA-ACL");
        } else {
            recommendedProtocols.add("Simple-Protocol");
        }
        
        // 4. Recommend appropriate system scale
        int recommendedAgentCount = Math.max(5, context.getComplexity() * 3);
        
        // 5. Build the recommendation map
        architectureRecommendation.put("agentTypes", recommendedAgentTypes);
        architectureRecommendation.put("coordinationPatterns", recommendedPatterns);
        architectureRecommendation.put("communicationProtocols", recommendedProtocols);
        architectureRecommendation.put("recommendedAgentCount", recommendedAgentCount);
        architectureRecommendation.put("justification", generateJustification(context));
        
        return architectureRecommendation;
    }
    
    @Override
    public List<Map<String, Object>> findSimilarMasProjects(Context context, int limit) {
        List<Map<String, Object>> similarProjects = new ArrayList<>();
        
        // In a production system, we would search a database of existing projects
        // For this implementation, we'll create some example projects
        
        // Example project 1
        Map<String, Object> project1 = new HashMap<>();
        project1.put("id", "project-1");
        project1.put("name", "Healthcare Monitoring MAS");
        project1.put("description", "A multi-agent system for monitoring patient health data");
        project1.put("domain", "healthcare");
        project1.put("agentCount", 8);
        project1.put("architecture", "Hierarchical");
        project1.put("similarityScore", calculateSimilarityScore(project1, context));
        
        // Example project 2
        Map<String, Object> project2 = new HashMap<>();
        project2.put("id", "project-2");
        project2.put("name", "Financial Market Simulation");
        project2.put("description", "MAS for simulating financial market behaviors");
        project2.put("domain", "finance");
        project2.put("agentCount", 15);
        project2.put("architecture", "Market-based");
        project2.put("similarityScore", calculateSimilarityScore(project2, context));
        
        // Example project 3
        Map<String, Object> project3 = new HashMap<>();
        project3.put("id", "project-3");
        project3.put("name", "Smart City Traffic Management");
        project3.put("description", "MAS for optimizing traffic flow in urban areas");
        project3.put("domain", "urban-planning");
        project3.put("agentCount", 25);
        project3.put("architecture", "Distributed");
        project3.put("similarityScore", calculateSimilarityScore(project3, context));
        
        // Add projects to the list
        similarProjects.add(project1);
        similarProjects.add(project2);
        similarProjects.add(project3);
        
        // Sort by similarity score and limit the results
        similarProjects.sort(Comparator.comparingDouble(p -> -((Double) p.get("similarityScore"))));
        return similarProjects.stream().limit(limit).collect(Collectors.toList());
    }
    
    @Override
    public List<CommunicationProtocol> recommendCommunicationProtocols(Context context) {
        List<CommunicationProtocol> allProtocols = communicationProtocolRepository.findAll();
        List<CommunicationProtocol> recommendedProtocols = new ArrayList<>();
        
        for (CommunicationProtocol protocol : allProtocols) {
            double score = scoreProtocolForContext(protocol, context);
            if (score > 0.5) { // Threshold for recommendation
                recommendedProtocols.add(protocol);
            }
        }
        
        // Sort protocols by their score
        Map<CommunicationProtocol, Double> protocolScores = new HashMap<>();
        for (CommunicationProtocol protocol : recommendedProtocols) {
            protocolScores.put(protocol, scoreProtocolForContext(protocol, context));
        }
        
        return recommendedProtocols.stream()
                .sorted(Comparator.comparing(protocol -> -protocolScores.get(protocol)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CoordinationPattern> recommendCoordinationPatterns(Context context, List<String> agentTypes) {
        List<CoordinationPattern> allPatterns = coordinationPatternRepository.findAll();
        List<CoordinationPattern> recommendedPatterns = new ArrayList<>();
        
        for (CoordinationPattern pattern : allPatterns) {
            double score = scorePatternForContext(pattern, context, agentTypes);
            if (score > 0.5) { // Threshold for recommendation
                recommendedPatterns.add(pattern);
            }
        }
        
        // Sort patterns by their score
        Map<CoordinationPattern, Double> patternScores = new HashMap<>();
        for (CoordinationPattern pattern : recommendedPatterns) {
            patternScores.put(pattern, scorePatternForContext(pattern, context, agentTypes));
        }
        
        return recommendedPatterns.stream()
                .sorted(Comparator.comparing(pattern -> -patternScores.get(pattern)))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AgentDefinition> recommendAgentDefinitions(Context context) {
        List<AgentDefinition> allAgents = agentDefinitionRepository.findAll();
        List<AgentDefinition> recommendedAgents = new ArrayList<>();
        
        for (AgentDefinition agent : allAgents) {
            double score = scoreAgentForContext(agent, context);
            if (score > 0.5) { // Threshold for recommendation
                recommendedAgents.add(agent);
            }
        }
        
        // Sort agents by their score
        Map<AgentDefinition, Double> agentScores = new HashMap<>();
        for (AgentDefinition agent : recommendedAgents) {
            agentScores.put(agent, scoreAgentForContext(agent, context));
        }
        
        return recommendedAgents.stream()
                .sorted(Comparator.comparing(agent -> -agentScores.get(agent)))
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, List<Map<String, Object>>> recommendTechnologiesAndFrameworks(Context context) {
        Map<String, List<Map<String, Object>>> recommendations = new HashMap<>();
        
        // Development frameworks
        List<Map<String, Object>> devFrameworks = new ArrayList<>();
        Map<String, Object> jade = new HashMap<>();
        jade.put("name", "JADE");
        jade.put("description", "Java Agent DEvelopment Framework");
        jade.put("language", "Java");
        jade.put("url", "https://jade.tilab.com/");
        jade.put("score", 0.9);
        jade.put("justification", "Industry standard Java-based MAS framework with FIPA compliance");
        
        Map<String, Object> jason = new HashMap<>();
        jason.put("name", "Jason");
        jason.put("description", "Java-based interpreter for AgentSpeak");
        jason.put("language", "Java/AgentSpeak");
        jason.put("url", "http://jason.sourceforge.net/");
        jason.put("score", 0.85);
        jason.put("justification", "BDI agent-oriented programming language with logical reasoning");
        
        devFrameworks.add(jade);
        devFrameworks.add(jason);
        
        // Testing tools
        List<Map<String, Object>> testingTools = new ArrayList<>();
        Map<String, Object> junit = new HashMap<>();
        junit.put("name", "JUnit");
        junit.put("description", "Java testing framework");
        junit.put("language", "Java");
        junit.put("url", "https://junit.org/");
        junit.put("score", 0.95);
        junit.put("justification", "Standard testing framework for Java-based agents");
        testingTools.add(junit);
        
        // Deployment tools
        List<Map<String, Object>> deployTools = new ArrayList<>();
        Map<String, Object> docker = new HashMap<>();
        docker.put("name", "Docker");
        docker.put("description", "Container platform");
        docker.put("url", "https://www.docker.com/");
        docker.put("score", 0.92);
        docker.put("justification", "Ideal for containerizing and orchestrating multiple agents");
        deployTools.add(docker);
        
        // Add all categories to recommendations
        recommendations.put("development", devFrameworks);
        recommendations.put("testing", testingTools);
        recommendations.put("deployment", deployTools);
        
        return recommendations;
    }
    
    @Override
    public Map<String, Object> getTelemetryInsights(Context context) {
        Map<String, Object> insights = new HashMap<>();
        
        // Performance metrics
        Map<String, Object> performanceMetrics = new HashMap<>();
        performanceMetrics.put("averageResponseTime", 120); // ms
        performanceMetrics.put("throughput", 5000); // requests/sec
        performanceMetrics.put("errorRate", 0.02); // 2%
        
        // Resource usage
        Map<String, Object> resourceUsage = new HashMap<>();
        resourceUsage.put("averageCpuUsage", 45); // %
        resourceUsage.put("averageMemoryUsage", 1024); // MB
        resourceUsage.put("networkBandwidth", 5); // MB/s
        
        // Common issues
        List<Map<String, String>> commonIssues = new ArrayList<>();
        Map<String, String> issue1 = new HashMap<>();
        issue1.put("issue", "Agent communication deadlocks");
        issue1.put("solution", "Implement timeout mechanisms and circuit breakers");
        
        Map<String, String> issue2 = new HashMap<>();
        issue2.put("issue", "Resource contention");
        issue2.put("solution", "Implement resource allocation protocols");
        
        commonIssues.add(issue1);
        commonIssues.add(issue2);
        
        // Build the insights object
        insights.put("performanceMetrics", performanceMetrics);
        insights.put("resourceUsage", resourceUsage);
        insights.put("commonIssues", commonIssues);
        insights.put("recommendedOptimizations", getRecommendedOptimizations(context));
        
        return insights;
    }
    
    /**
     * Calculates similarity between a project and the given context.
     */
    private double calculateSimilarityScore(Map<String, Object> project, Context context) {
        double score = 0.0;
        
        // Domain similarity
        if (context.getDomainArea() != null && context.getDomainArea().equals(project.get("domain"))) {
            score += 0.5;
        }
        
        // Architecture similarity
        if (context.getCustomParameters() != null && 
            context.getCustomParameters().get("preferredArchitecture") != null &&
            context.getCustomParameters().get("preferredArchitecture").equals(project.get("architecture"))) {
            score += 0.3;
        }
        
        // Scale similarity (based on agent count)
        if (context.getComplexity() > 0) {
            // Simple heuristic: higher complexity means more agents
            int expectedAgents = context.getComplexity() * 5;
            int actualAgents = (int) project.get("agentCount");
            
            double agentDifference = Math.abs(expectedAgents - actualAgents) / (double) expectedAgents;
            score += 0.2 * (1 - Math.min(agentDifference, 1.0));
        }
        
        return score;
    }
    
    /**
     * Scores a communication protocol based on how well it matches the context.
     */
    private double scoreProtocolForContext(CommunicationProtocol protocol, Context context) {
        double score = 0.0;
        
        // Match distributed execution requirements
        if (context.isDistributedExecution() && protocol.isDistributed()) {
            score += 0.4;
        } else if (!context.isDistributedExecution() && !protocol.isDistributed()) {
            score += 0.3;
        }
        
        
        
        return Math.min(1.0, score);
    }
    
    /**
     * Scores a coordination pattern based on how well it matches the context.
     */
    private double scorePatternForContext(CoordinationPattern pattern, Context context, List<String> agentTypes) {
        double score = 0.0;
        
        // Match based on complexity
        if (pattern.complexityLevel() == context.getComplexity()) {
            score += 0.3;
        } else {
            score += 0.3 - (0.1 * Math.abs(pattern.complexityLevel() - context.getComplexity()));
        }
        
        // Match based on agent types
        if (pattern.supportedAgentTypes() != null && agentTypes != null) {
            Set<String> patternAgentTypes = new HashSet<>(pattern.supportedAgentTypes());
            Set<String> contextAgentTypes = new HashSet<>(agentTypes);
            
            double intersectionSize = 0;
            for (String agentType : contextAgentTypes) {
                if (patternAgentTypes.contains(agentType)) {
                    intersectionSize++;
                }
            }
            
            if (!contextAgentTypes.isEmpty()) {
                score += 0.4 * (intersectionSize / contextAgentTypes.size());
            }
        }
        
        // Match based on domain
        if (context.getDomainArea() != null && pattern.domains() != null) {
            if (pattern.domains().contains(context.getDomainArea())) {
                score += 0.3;
            }
        }
        
        return Math.min(1.0, score);
    }
    
    /**
     * Scores an agent definition based on how well it matches the context.
     */
    private double scoreAgentForContext(AgentDefinition agent, Context context) {
        double score = 0.0;
        
        // Match based on complexity
        if (agent.getComplexityLevel() == context.getComplexity()) {
            score += 0.3;
        } else {
            score += 0.3 - (0.1 * Math.abs(agent.getComplexityLevel() - context.getComplexity()));
        }
        
        // Match based on required capabilities
        if (context.getRequiredCapabilities() != null && agent.getCapabilities() != null) {
            for (String capability : context.getRequiredCapabilities()) {
                if (agent.getCapabilities().contains(capability)) {
                    score += 0.2;
                }
            }
        }
       
        
        return Math.min(1.0, score);
    }
    
    /**
     * Generates justification text for architecture recommendations.
     */
    private String generateJustification(Context context) {
        StringBuilder justification = new StringBuilder();
        justification.append("This architecture is recommended based on: ");
        
        if (context.isDistributedExecution()) {
            justification.append("distributed execution requirements, ");
        } else {
            justification.append("centralized execution requirements, ");
        }
        
        justification.append("task complexity level ").append(context.getComplexity()).append(", ");
        
        if (context.getDomainArea() != null) {
            justification.append("domain-specific needs in ").append(context.getDomainArea()).append(", ");
        }
        
        justification.append("and best practices for similar systems.");
        
        return justification.toString();
    }
    
    /**
     * Provides recommended optimizations based on telemetry data.
     */
    private List<Map<String, String>> getRecommendedOptimizations(Context context) {
        List<Map<String, String>> optimizations = new ArrayList<>();
        
        Map<String, String> opt1 = new HashMap<>();
        opt1.put("area", "Communication");
        opt1.put("recommendation", "Implement message batching to reduce overhead");
        
        Map<String, String> opt2 = new HashMap<>();
        opt2.put("area", "Resource Usage");
        opt2.put("recommendation", "Implement dynamic resource allocation");
        
        Map<String, String> opt3 = new HashMap<>();
        opt3.put("area", "Monitoring");
        opt3.put("recommendation", "Add proactive anomaly detection");
        
        optimizations.add(opt1);
        optimizations.add(opt2);
        optimizations.add(opt3);
        
        return optimizations;
    }
}
