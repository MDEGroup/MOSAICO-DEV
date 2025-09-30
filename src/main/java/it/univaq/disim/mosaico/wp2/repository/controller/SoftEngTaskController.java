package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.SoftEngTask;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import it.univaq.disim.mosaico.wp2.repository.service.SoftEngTaskService;

/**
 * Controller for SoftEngTask operations following MOSAICO taxonomy.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
@RequestMapping("/api/soft-eng-tasks")
public class SoftEngTaskController {

    Logger logger = LoggerFactory.getLogger(SoftEngTaskController.class);
    
    @Autowired
    private final SoftEngTaskService softEngTaskService;
    
    public SoftEngTaskController(@Autowired SoftEngTaskService softEngTaskService) {
        this.softEngTaskService = softEngTaskService;
    }
    
    /**
     * Get all software engineering tasks.
     */
    @GetMapping
    public ResponseEntity<List<SoftEngTask>> getAllTasks() {
        logger.info("GET /api/soft-eng-tasks");
        List<SoftEngTask> tasks = softEngTaskService.findAll();
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Get task by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SoftEngTask> getTaskById(@PathVariable String id) {
        logger.info("GET /api/soft-eng-tasks/{}", id);
        Optional<SoftEngTask> task = softEngTaskService.findById(id);
        return task.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new task.
     */
    @PostMapping
    public ResponseEntity<SoftEngTask> createTask(@RequestBody SoftEngTask task) {
        logger.info("POST /api/soft-eng-tasks for task: {}", task.name());
        SoftEngTask savedTask = softEngTaskService.save(task);
        return ResponseEntity.ok(savedTask);
    }
    
    /**
     * Update existing task.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SoftEngTask> updateTask(@PathVariable String id, @RequestBody SoftEngTask task) {
        logger.info("PUT /api/soft-eng-tasks/{}", id);
        Optional<SoftEngTask> existingTask = softEngTaskService.findById(id);
        if (existingTask.isPresent()) {
            SoftEngTask updatedTask = softEngTaskService.save(task);
            return ResponseEntity.ok(updatedTask);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete task.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        logger.info("DELETE /api/soft-eng-tasks/{}", id);
        softEngTaskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Find tasks by name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<SoftEngTask>> findTasksByName(@RequestParam String name) {
        logger.info("GET /api/soft-eng-tasks/search/name?name={}", name);
        List<SoftEngTask> tasks = softEngTaskService.findByName(name);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Find tasks by primary knowledge area.
     */
    @GetMapping("/search/primary-ka")
    public ResponseEntity<List<SoftEngTask>> findTasksByPrimaryKA(@RequestParam SwebokKAId primaryKA) {
        logger.info("GET /api/soft-eng-tasks/search/primary-ka?primaryKA={}", primaryKA);
        List<SoftEngTask> tasks = softEngTaskService.findByPrimaryKA(primaryKA);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Find tasks by secondary knowledge area.
     */
    @GetMapping("/search/secondary-ka")
    public ResponseEntity<List<SoftEngTask>> findTasksBySecondaryKA(@RequestParam SwebokKAId secondaryKA) {
        logger.info("GET /api/soft-eng-tasks/search/secondary-ka?secondaryKA={}", secondaryKA);
        List<SoftEngTask> tasks = softEngTaskService.findBySecondaryKA(secondaryKA);
        return ResponseEntity.ok(tasks);
    }
}