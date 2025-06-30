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

import it.univaq.disim.mosaico.wp2.repository.data.ToolDefinition;
import it.univaq.disim.mosaico.wp2.repository.service.ToolDefinitionService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for ToolDefinition operations
 * It handles HTTP requests and responses for the ToolDefinition entity.
 */
public class ToolDefinitionController {

  Logger logger = LoggerFactory.getLogger(ToolDefinitionController.class);

  /*
   * This class is used to handle HTTP requests and responses for the ToolDefinition entity.
   * It uses the ToolDefinitionService to perform CRUD operations on the ToolDefinition entity.
   */
  @Autowired
  /*
   * The ToolDefinitionService is used to perform CRUD operations on the ToolDefinition entity.
   * It is autowired by Spring.
   */
  private final ToolDefinitionService service;

  /*
   * This constructor is used to inject the ToolDefinitionService dependency into the
   * ToolDefinitionController class.
   * The @Autowired annotation is used to indicate that the ToolDefinitionService bean
   * should be injected.
   * 
   * @param toolDefinitionService the ToolDefinitionService bean to be injected
   */
  public ToolDefinitionController(@Autowired ToolDefinitionService toolDefinitionService) {
    this.service = toolDefinitionService;
  }

  /*
   * This method handles GET requests to retrieve all tool definitions from the repository.
   * 
   * @return a list of all tool definitions in the repository
   */
  @GetMapping("/tool-definition")
  public List<ToolDefinition> all() {
    logger.info("GET /tool-definition");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific tool definition by ID.
   * 
   * @param id the ID of the tool definition to retrieve
   * @return the requested tool definition
   */
  @GetMapping("/tool-definition/{id}")
  public ToolDefinition one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing tool definition in the repository.
   * 
   * @param newToolDefinition the updated tool definition
   * @param id the ID of the tool definition to update
   * @return the updated tool definition
   */
  @PutMapping("/tool-definition/{id}")
  public ToolDefinition replaceToolDefinition(@RequestBody ToolDefinition newToolDefinition, @PathVariable String id) {
    return service.save(newToolDefinition);
  }

  /*
   * This method handles DELETE requests to delete a tool definition from the repository.
   * 
   * @param id the id of the tool definition to be deleted
   */
  @DeleteMapping("/tool-definition/{id}")
  public void deleteToolDefinition(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new tool definition in the repository.
   * 
   * @param newToolDefinition the tool definition to be created
   * @return the created tool definition
   */
  @PostMapping("/tool-definition")
  public ToolDefinition newToolDefinition(@RequestBody ToolDefinition newToolDefinition) {
    return service.save(newToolDefinition);
  }
}
