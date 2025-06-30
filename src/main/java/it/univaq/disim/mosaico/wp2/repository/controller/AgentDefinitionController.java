package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.service.AgentDefinitionService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for AgentDefinition operations
 * It handles HTTP requests and responses for the AgentDefinition entity.
 */
public class AgentDefinitionController {

  Logger logger = LoggerFactory.getLogger(AgentDefinitionController.class);

  /*
   * This class is used to handle HTTP requests and responses for the AgentDefinition entity.
   * It uses the AgentDefinitionService to perform CRUD operations on the AgentDefinition entity.
   */
  @Autowired
  /*
   * The AgentDefinitionService is used to perform CRUD operations on the AgentDefinition entity.
   * It is autowired by Spring.
   */
  private final AgentDefinitionService service;

  /*
   * This constructor is used to inject the AgentDefinitionService dependency into the
   * AgentDefinitionController class.
   * The @Autowired annotation is used to indicate that the AgentDefinitionService bean
   * should be injected.
   * 
   * @param agentDefinitionService the AgentDefinitionService bean to be injected
   */
  public AgentDefinitionController(@Autowired AgentDefinitionService agentDefinitionService) {
    this.service = agentDefinitionService;
  }

  /*
   * This method handles GET requests to retrieve all agent definitions from the repository.
   * 
   * @return a list of all agent definitions in the repository
   */
  @GetMapping("/agent-definition")
  public List<AgentDefinition> all() {
    logger.info("GET /agent-definition");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific agent definition by ID.
   * 
   * @param id the ID of the agent definition to retrieve
   * @return the requested agent definition
   */
  @GetMapping("/agent-definition/{id}")
  public AgentDefinition one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing agent definition in the repository.
   * 
   * @param newAgentDefinition the updated agent definition
   * @param id the ID of the agent definition to update
   * @return the updated agent definition
   */
  @PutMapping("/agent-definition/{id}")
  public AgentDefinition replaceAgentDefinition(@RequestBody AgentDefinition newAgentDefinition, @PathVariable String id) {
    return service.save(newAgentDefinition);
  }

  /*
   * This method handles DELETE requests to delete an agent definition from the repository.
   * 
   * @param id the id of the agent definition to be deleted
   */
  @DeleteMapping("/agent-definition/{id}")
  public void deleteAgentDefinition(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new agent definition in the repository.
   * 
   * @param newAgentDefinition the agent definition to be created
   * @return the created agent definition
   */
  @PostMapping("/agent-definition")
  public AgentDefinition newAgentDefinition(@RequestBody AgentDefinition newAgentDefinition) {
    return service.save(newAgentDefinition);
  }
}
