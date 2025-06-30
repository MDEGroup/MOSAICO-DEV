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

import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.service.CoordinationPatternService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for CoordinationPattern operations
 * It handles HTTP requests and responses for the CoordinationPattern entity.
 */
public class CoordinationPatternController {

  Logger logger = LoggerFactory.getLogger(CoordinationPatternController.class);

  /*
   * This class is used to handle HTTP requests and responses for the CoordinationPattern entity.
   * It uses the CoordinationPatternService to perform CRUD operations on the CoordinationPattern entity.
   */
  @Autowired
  /*
   * The CoordinationPatternService is used to perform CRUD operations on the CoordinationPattern entity.
   * It is autowired by Spring.
   */
  private final CoordinationPatternService service;

  /*
   * This constructor is used to inject the CoordinationPatternService dependency into the
   * CoordinationPatternController class.
   * The @Autowired annotation is used to indicate that the CoordinationPatternService bean
   * should be injected.
   * 
   * @param coordinationPatternService the CoordinationPatternService bean to be injected
   */
  public CoordinationPatternController(@Autowired CoordinationPatternService coordinationPatternService) {
    this.service = coordinationPatternService;
  }

  /*
   * This method handles GET requests to retrieve all coordination patterns from the repository.
   * 
   * @return a list of all coordination patterns in the repository
   */
  @GetMapping("/coordination-pattern")
  public List<CoordinationPattern> all() {
    logger.info("GET /coordination-pattern");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific coordination pattern by ID.
   * 
   * @param id the ID of the coordination pattern to retrieve
   * @return the requested coordination pattern
   */
  @GetMapping("/coordination-pattern/{id}")
  public CoordinationPattern one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing coordination pattern in the repository.
   * 
   * @param newCoordinationPattern the updated coordination pattern
   * @param id the ID of the coordination pattern to update
   * @return the updated coordination pattern
   */
  @PutMapping("/coordination-pattern/{id}")
  public CoordinationPattern replaceCoordinationPattern(@RequestBody CoordinationPattern newCoordinationPattern, @PathVariable String id) {
    return service.save(newCoordinationPattern);
  }

  /*
   * This method handles DELETE requests to delete a coordination pattern from the repository.
   * 
   * @param id the id of the coordination pattern to be deleted
   */
  @DeleteMapping("/coordination-pattern/{id}")
  public void deleteCoordinationPattern(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new coordination pattern in the repository.
   * 
   * @param newCoordinationPattern the coordination pattern to be created
   * @return the created coordination pattern
   */
  @PostMapping("/coordination-pattern")
  public CoordinationPattern newCoordinationPattern(@RequestBody CoordinationPattern newCoordinationPattern) {
    return service.save(newCoordinationPattern);
  }
}
