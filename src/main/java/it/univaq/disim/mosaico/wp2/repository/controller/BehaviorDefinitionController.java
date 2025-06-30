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

import it.univaq.disim.mosaico.wp2.repository.data.BehaviorDefinition;
import it.univaq.disim.mosaico.wp2.repository.service.BehaviorDefinitionService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for BehaviorDefinition operations
 * It handles HTTP requests and responses for the BehaviorDefinition entity.
 */
public class BehaviorDefinitionController {

  Logger logger = LoggerFactory.getLogger(BehaviorDefinitionController.class);

  /*
   * This class is used to handle HTTP requests and responses for the BehaviorDefinition entity.
   * It uses the BehaviorDefinitionService to perform CRUD operations on the BehaviorDefinition entity.
   */
  @Autowired
  /*
   * The BehaviorDefinitionService is used to perform CRUD operations on the BehaviorDefinition entity.
   * It is autowired by Spring.
   */
  private final BehaviorDefinitionService service;

  /*
   * This constructor is used to inject the BehaviorDefinitionService dependency into the
   * BehaviorDefinitionController class.
   * The @Autowired annotation is used to indicate that the BehaviorDefinitionService bean
   * should be injected.
   * 
   * @param behaviorDefinitionService the BehaviorDefinitionService bean to be injected
   */
  public BehaviorDefinitionController(@Autowired BehaviorDefinitionService behaviorDefinitionService) {
    this.service = behaviorDefinitionService;
  }

  /*
   * This method handles GET requests to retrieve all behavior definitions from the repository.
   * 
   * @return a list of all behavior definitions in the repository
   */
  @GetMapping("/behavior-definition")
  public List<BehaviorDefinition> all() {
    logger.info("GET /behavior-definition");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific behavior definition by ID.
   * 
   * @param id the ID of the behavior definition to retrieve
   * @return the requested behavior definition
   */
  @GetMapping("/behavior-definition/{id}")
  public BehaviorDefinition one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing behavior definition in the repository.
   * 
   * @param newBehaviorDefinition the updated behavior definition
   * @param id the ID of the behavior definition to update
   * @return the updated behavior definition
   */
  @PutMapping("/behavior-definition/{id}")
  public BehaviorDefinition replaceBehaviorDefinition(@RequestBody BehaviorDefinition newBehaviorDefinition, @PathVariable String id) {
    return service.save(newBehaviorDefinition);
  }

  /*
   * This method handles DELETE requests to delete a behavior definition from the repository.
   * 
   * @param id the id of the behavior definition to be deleted
   */
  @DeleteMapping("/behavior-definition/{id}")
  public void deleteBehaviorDefinition(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new behavior definition in the repository.
   * 
   * @param newBehaviorDefinition the behavior definition to be created
   * @return the created behavior definition
   */
  @PostMapping("/behavior-definition")
  public BehaviorDefinition newBehaviorDefinition(@RequestBody BehaviorDefinition newBehaviorDefinition) {
    return service.save(newBehaviorDefinition);
  }
}
