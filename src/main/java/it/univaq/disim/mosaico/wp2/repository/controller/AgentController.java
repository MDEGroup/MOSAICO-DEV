package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.dto.AgentSearchResult;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;

/**
 * Controller for Agent operations following MOSAICO taxonomy.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
@RequestMapping("/api/agents")
public class AgentController {

    Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    
    private final AgentService agentService;
    
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }
    
    /**
     * Get all agents.
     */
    @GetMapping
    public ResponseEntity<List<Agent>> getAllAgents() {
        logger.info("GET /api/agents");
        List<Agent> agents = agentService.findAll();
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Get agent by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Agent> getAgentById(@PathVariable String id) {
        logger.info("GET /api/agents/{}", id);
        Optional<Agent> agent = agentService.findById(id);
        return agent.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new agent.
     */
    @PostMapping
    public ResponseEntity<Agent> createAgent(@RequestBody Agent agent) {
        logger.info("POST /api/agents for agent: {}", agent.getName());
        Agent savedAgent = agentService.save(agent);
        return ResponseEntity.ok(savedAgent);
    }
    
    /**
     * Update existing agent.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Agent> updateAgent(@PathVariable String id, @RequestBody Agent agent) {
        logger.info("PUT /api/agents/{}", id);
        Optional<Agent> existingAgent = agentService.findById(id);
        if (existingAgent.isPresent()) {
            agent.setId(id); // client may not set the ID in the request body
            Agent updatedAgent = agentService.save(agent);
            return ResponseEntity.ok(updatedAgent);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete agent.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable String id) {
        logger.info("DELETE /api/agents/{}", id);
        agentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Find agents by name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Agent>> findAgentsByName(@RequestParam String name) {
        logger.info("GET /api/agents/search/name?name={}", name);
        List<Agent> agents = agentService.findByName(name);
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Find agents by role.
     */
    @GetMapping("/search/role")
    public ResponseEntity<List<Agent>> findAgentsByRole(@RequestParam String role) {
        logger.info("GET /api/agents/search/role?role={}", role);
        List<Agent> agents = agentService.findByRole(role);
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Find agents by I/O modality.
     */
    @GetMapping("/search/io-modality")
    public ResponseEntity<List<Agent>> findAgentsByIOModality(@RequestParam IOModality ioModality) {
        logger.info("GET /api/agents/search/io-modality?ioModality={}", ioModality);
        List<Agent> agents = agentService.findByIOModality(ioModality);
        return ResponseEntity.ok(agents);
    }
    @PostMapping("/search")
    public ResponseEntity<List<AgentSearchResult>> semanticSearch(@RequestBody SemanticSearchRequest request) {
        String query = request.getQuery();
        int topK = request.getTopK() == null ? 5 : request.getTopK();
        Map<String, Object> filters = request.getFilters() == null ? Map.of() : request.getFilters();
        List<AgentSearchResult> results = agentService.semanticSearchWithScores(query, filters, topK);
        results.forEach(z -> logger.info(z.toString()));
        return ResponseEntity.ok(results);
    }
}