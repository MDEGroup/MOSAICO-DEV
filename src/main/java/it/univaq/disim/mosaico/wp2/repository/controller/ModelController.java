package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.service.ModelService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the main controller for the repository
 * It handles HTTP requests and responses for the Model entity.
 */
public class ModelController {

  Logger logger = LoggerFactory.getLogger(ModelController.class);

  
  /*
   * This class is used to handle HTTP requests and responses for the Model
   * entity.
   * It uses the ModelRepository to perform CRUD operations on the Model entity.
   */
  @Autowired
  /*
   * The ModelService is used to perform CRUD operations on the Model entity.
   * It is autowired by Spring.
   */
  private final ModelService repository;

  /*
   * This constructor is used to inject the ModelRepository dependency into the
   * MainController class.
   * The @Autowired annotation is used to indicate that the ModelRepository bean
   * should be injected.
   * 
   * @param modelRepository the ModelRepository bean to be injected
   */
  public ModelController(@Autowired ModelService modelRepository) {
    this.repository = modelRepository;
  }

  /*
   * This method handles GET requests to retrieve all models from the repository.
   * 
   * @return a list of all models in the repository
   */
  @GetMapping("/model")
  public List<Model> all() {
    logger.info("GET /model JURI");
    return repository.findAll();
  }

  /*
   * This method handles POST requests to create a new model in the repository.
   * 
   * @param newModel the model to be created
   * 
   * @return the created model
   */
  @GetMapping("/model/{id}")
  Model one(@PathVariable String id) {
    return repository.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing model in the
   * repository.
   * 
   * @param newModel the updated model
   * 
   * @return the updated model
   */
  @PutMapping("/model/{id}")
  Model replaceEmployee(@RequestBody Model newEmployee, @PathVariable String id) {
    return repository.save(newEmployee);
  }

  /*
   * This method handles DELETE requests to delete a model from the repository.
   * 
   * @param id the id of the model to be deleted
   * 
   * @return void
   * 
   * @throws EmployeeNotFoundException if the model is not found
   */
  @DeleteMapping("/model/{id}")
  void deleteEmployee(@PathVariable String id) {
    repository.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new model in the repository.
   * 
   * @param newModel the model to be created
   * 
   * @return the created model
   */
  @PostMapping("/model")
  Model newEmployee(@RequestBody Model newEmployee) {
    return repository.save(newEmployee);
  }

}
