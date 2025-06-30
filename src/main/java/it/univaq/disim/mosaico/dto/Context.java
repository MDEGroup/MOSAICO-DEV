package it.univaq.disim.mosaico.dto;

import java.util.List;
import java.util.Map;
import java.time.Instant;

/**
 * DTO class representing the user context for model recommendations.
 * This class is used to capture the user's current context, requirements,
 * and preferences to enable intelligent recommendations of suitable models
 * for specific tasks or scenarios.
 * 
 * Copyright 2025 Mosaico
 */
public class Context {
    
    // User information
    private String userId;
    private String userName;
    private String userRole;
    private List<String> userSkills;
    
    // Task information
    private String taskId;
    private String taskName;
    private String taskDescription;
    private String taskType;  // e.g., "data-analysis", "text-generation", "agent-coordination"
    private List<String> requiredCapabilities;
    private int complexity;   // 1-5 scale for task complexity
    private String domainArea;  // e.g., "healthcare", "finance", "education"
    
    // Environmental constraints
    private Map<String, Object> systemResources;  // Available computational resources
    private List<String> availableTools;
    private Map<String, Object> timeConstraints;
    private boolean distributedExecution;
    
    // Preferences
    private List<String> preferredModelTypes;
    private List<String> preferredAuthors;
    private String preferredLicense;
    private Map<String, Integer> metricWeights;  // Weights for different performance metrics
    
    // Historical data
    private List<String> previouslyUsedModels;
    private Map<String, Object> previousTaskResults;
    private Instant lastActivity;
    
    // Custom parameters
    private Map<String, Object> customParameters;

    // Constructors
    public Context() {}
    
    public Context(String userId, String taskId, String taskType) {
        this.userId = userId;
        this.taskId = taskId;
        this.taskType = taskType;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public List<String> getUserSkills() {
        return userSkills;
    }
    
    public void setUserSkills(List<String> userSkills) {
        this.userSkills = userSkills;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getTaskDescription() {
        return taskDescription;
    }
    
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
    
    public String getTaskType() {
        return taskType;
    }
    
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }
    
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }
    
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    public String getDomainArea() {
        return domainArea;
    }
    
    public void setDomainArea(String domainArea) {
        this.domainArea = domainArea;
    }
    
    public Map<String, Object> getSystemResources() {
        return systemResources;
    }
    
    public void setSystemResources(Map<String, Object> systemResources) {
        this.systemResources = systemResources;
    }
    
    public List<String> getAvailableTools() {
        return availableTools;
    }
    
    public void setAvailableTools(List<String> availableTools) {
        this.availableTools = availableTools;
    }
    
    public Map<String, Object> getTimeConstraints() {
        return timeConstraints;
    }
    
    public void setTimeConstraints(Map<String, Object> timeConstraints) {
        this.timeConstraints = timeConstraints;
    }
    
    public boolean isDistributedExecution() {
        return distributedExecution;
    }
    
    public void setDistributedExecution(boolean distributedExecution) {
        this.distributedExecution = distributedExecution;
    }
    
    public List<String> getPreferredModelTypes() {
        return preferredModelTypes;
    }
    
    public void setPreferredModelTypes(List<String> preferredModelTypes) {
        this.preferredModelTypes = preferredModelTypes;
    }
    
    public List<String> getPreferredAuthors() {
        return preferredAuthors;
    }
    
    public void setPreferredAuthors(List<String> preferredAuthors) {
        this.preferredAuthors = preferredAuthors;
    }
    
    public String getPreferredLicense() {
        return preferredLicense;
    }
    
    public void setPreferredLicense(String preferredLicense) {
        this.preferredLicense = preferredLicense;
    }
    
    public Map<String, Integer> getMetricWeights() {
        return metricWeights;
    }
    
    public void setMetricWeights(Map<String, Integer> metricWeights) {
        this.metricWeights = metricWeights;
    }
    
    public List<String> getPreviouslyUsedModels() {
        return previouslyUsedModels;
    }
    
    public void setPreviouslyUsedModels(List<String> previouslyUsedModels) {
        this.previouslyUsedModels = previouslyUsedModels;
    }
    
    public Map<String, Object> getPreviousTaskResults() {
        return previousTaskResults;
    }
    
    public void setPreviousTaskResults(Map<String, Object> previousTaskResults) {
        this.previousTaskResults = previousTaskResults;
    }
    
    public Instant getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(Instant lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public Map<String, Object> getCustomParameters() {
        return customParameters;
    }
    
    public void setCustomParameters(Map<String, Object> customParameters) {
        this.customParameters = customParameters;
    }
    
    // Utility methods
    public void addRequiredCapability(String capability) {
        if (this.requiredCapabilities == null) {
            this.requiredCapabilities = new java.util.ArrayList<>();
        }
        this.requiredCapabilities.add(capability);
    }
    
    public void addUserSkill(String skill) {
        if (this.userSkills == null) {
            this.userSkills = new java.util.ArrayList<>();
        }
        this.userSkills.add(skill);
    }
    
    public void addAvailableTool(String tool) {
        if (this.availableTools == null) {
            this.availableTools = new java.util.ArrayList<>();
        }
        this.availableTools.add(tool);
    }
    
    public void addCustomParameter(String key, Object value) {
        if (this.customParameters == null) {
            this.customParameters = new java.util.HashMap<>();
        }
        this.customParameters.put(key, value);
    }
}
