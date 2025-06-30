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

import it.univaq.disim.mosaico.wp2.repository.data.Tool;
import it.univaq.disim.mosaico.wp2.repository.service.ToolService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for Tool operations
 * It handles HTTP requests and responses for the Tool entity.
 */
public class ToolController {

  Logger logger = LoggerFactory.getLogger(ToolController.class);

  /*
   * This class is used to handle HTTP requests and responses for the Tool entity.
   * It uses the ToolService to perform CRUD operations on the Tool entity.
   */
  @Autowired
  /*
   * The ToolService is used to perform CRUD operations on the Tool entity.
   * It is autowired by Spring.
   */
  private final ToolService service;

  /*
   * This constructor is used to inject the ToolService dependency into the
   * ToolController class.
   * The @Autowired annotation is used to indicate that the ToolService bean
   * should be injected.
   * 
   * @param toolService the ToolService bean to be injected
   */
  public ToolController(@Autowired ToolService toolService) {
    this.service = toolService;
  }

  /*
   * This method handles GET requests to retrieve all tools from the repository.
   * 
   * @return a list of all tools in the repository
   */
  @GetMapping("/tool")
  public List<Tool> all() {
    logger.info("GET /tool");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific tool by ID.
   * 
   * @param id the ID of the tool to retrieve
   * @return the requested tool
   */
  @GetMapping("/tool/{id}")
  public Tool one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing tool in the repository.
   * 
   * @param newTool the updated tool
   * @param id the ID of the tool to update
   * @return the updated tool
   */
  @PutMapping("/tool/{id}")
  public Tool replaceTool(@RequestBody Tool newTool, @PathVariable String id) {
    return service.save(newTool);
  }

  /*
   * This method handles DELETE requests to delete a tool from the repository.
   * 
   * @param id the id of the tool to be deleted
   */
  @DeleteMapping("/tool/{id}")
  public void deleteTool(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new tool in the repository.
   * 
   * @param newTool the tool to be created
   * @return the created tool
   */
  @PostMapping("/tool")
  public Tool newTool(@RequestBody Tool newTool) {
    return service.save(newTool);
  }
}
