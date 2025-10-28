package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.langfuse.client.resources.projects.types.Project;
import com.langfuse.client.resources.projects.types.Projects.Builder;

import java.util.List;
import java.util.Map;

/**
 * REST controller for Langfuse project management.
 */
@RestController
@RequestMapping("/api/langfuse/projects")
@ConditionalOnBean(LangfuseService.class)
public class LangfuseProjectController {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseProjectController.class);

    private final LangfuseService langfuseProjectService;

    public LangfuseProjectController(LangfuseService langfuseProjectService) {
        this.langfuseProjectService = langfuseProjectService;
    }

    /**
     * Get all projects from Langfuse.
     * GET /api/langfuse/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        logger.info("GET /api/langfuse/projects");
        
        if (!langfuseProjectService.isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return null;
        }

        List<Project> projects = langfuseProjectService.getProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get a specific project by ID.
     * GET /api/langfuse/projects/{projectId}
     */
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId) {
        logger.info("GET /api/langfuse/projects/{}", projectId);
        
        if (!langfuseProjectService.isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return ResponseEntity.notFound().build();
        }

        Project project = langfuseProjectService.getProjectById(projectId);
        
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(project);
    }



   

    /**
     * Create a new project in Langfuse.
     * POST /api/langfuse/projects
     * Body: { "name": "Project Name", "description": "Optional description" }
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Map<String, String> requestBody) {
        String projectName = requestBody.get("name");
        String description = requestBody.get("description");
        
        logger.info("POST /api/langfuse/projects - name: {}", projectName);
        
        if (!langfuseProjectService.isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return ResponseEntity.badRequest()
                    .body(null);
        }

        if (projectName == null || projectName.trim().isEmpty()) {
            logger.warn("Project name is required");
            return ResponseEntity.badRequest()
                    .body(null);
        }

        Project project = langfuseProjectService.createProject(projectName, description);
        
        if (project == null) {
            return ResponseEntity.internalServerError()
                    .body(null);
        }
        
        return ResponseEntity.status(201).body(project);
    }

    /**
     * Health check endpoint to verify Langfuse connectivity.
     * GET /api/langfuse/projects/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.info("GET /api/langfuse/projects/health");
        
        boolean enabled = langfuseProjectService.isEnabled();
        
        return ResponseEntity.ok(Map.of(
                "enabled", enabled,
                "status", enabled ? "connected" : "disabled"
        ));
    }
}
