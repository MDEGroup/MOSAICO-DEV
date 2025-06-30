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

import it.univaq.disim.mosaico.wp2.repository.data.Task;
import it.univaq.disim.mosaico.wp2.repository.service.TaskService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for Task operations
 * It handles HTTP requests and responses for the Task entity.
 */
public class TaskController {

  Logger logger = LoggerFactory.getLogger(TaskController.class);

  /*
   * This class is used to handle HTTP requests and responses for the Task entity.
   * It uses the TaskService to perform CRUD operations on the Task entity.
   */
  @Autowired
  /*
   * The TaskService is used to perform CRUD operations on the Task entity.
   * It is autowired by Spring.
   */
  private final TaskService service;

  /*
   * This constructor is used to inject the TaskService dependency into the
   * TaskController class.
   * The @Autowired annotation is used to indicate that the TaskService bean
   * should be injected.
   * 
   * @param taskService the TaskService bean to be injected
   */
  public TaskController(@Autowired TaskService taskService) {
    this.service = taskService;
  }

  /*
   * This method handles GET requests to retrieve all tasks from the repository.
   * 
   * @return a list of all tasks in the repository
   */
  @GetMapping("/task")
  public List<Task> all() {
    logger.info("GET /task");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific task by ID.
   * 
   * @param id the ID of the task to retrieve
   * @return the requested task
   */
  @GetMapping("/task/{id}")
  public Task one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing task in the repository.
   * 
   * @param newTask the updated task
   * @param id the ID of the task to update
   * @return the updated task
   */
  @PutMapping("/task/{id}")
  public Task replaceTask(@RequestBody Task newTask, @PathVariable String id) {
    return service.save(newTask);
  }

  /*
   * This method handles DELETE requests to delete a task from the repository.
   * 
   * @param id the id of the task to be deleted
   */
  @DeleteMapping("/task/{id}")
  public void deleteTask(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new task in the repository.
   * 
   * @param newTask the task to be created
   * @return the created task
   */
  @PostMapping("/task")
  public Task newTask(@RequestBody Task newTask) {
    return service.save(newTask);
  }
}
