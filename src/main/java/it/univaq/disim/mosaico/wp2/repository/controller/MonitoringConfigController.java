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

import it.univaq.disim.mosaico.wp2.repository.data.MonitoringConfig;
import it.univaq.disim.mosaico.wp2.repository.service.MonitoringConfigService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for MonitoringConfig operations
 * It handles HTTP requests and responses for the MonitoringConfig entity.
 */
public class MonitoringConfigController {

  Logger logger = LoggerFactory.getLogger(MonitoringConfigController.class);

  /*
   * This class is used to handle HTTP requests and responses for the MonitoringConfig entity.
   * It uses the MonitoringConfigService to perform CRUD operations on the MonitoringConfig entity.
   */
  @Autowired
  /*
   * The MonitoringConfigService is used to perform CRUD operations on the MonitoringConfig entity.
   * It is autowired by Spring.
   */
  private final MonitoringConfigService service;

  /*
   * This constructor is used to inject the MonitoringConfigService dependency into the
   * MonitoringConfigController class.
   * The @Autowired annotation is used to indicate that the MonitoringConfigService bean
   * should be injected.
   * 
   * @param monitoringConfigService the MonitoringConfigService bean to be injected
   */
  public MonitoringConfigController(@Autowired MonitoringConfigService monitoringConfigService) {
    this.service = monitoringConfigService;
  }

  /*
   * This method handles GET requests to retrieve all monitoring configurations from the repository.
   * 
   * @return a list of all monitoring configurations in the repository
   */
  @GetMapping("/monitoring-config")
  public List<MonitoringConfig> all() {
    logger.info("GET /monitoring-config");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific monitoring configuration by ID.
   * 
   * @param id the ID of the monitoring configuration to retrieve
   * @return the requested monitoring configuration
   */
  @GetMapping("/monitoring-config/{id}")
  public MonitoringConfig one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing monitoring configuration in the repository.
   * 
   * @param newMonitoringConfig the updated monitoring configuration
   * @param id the ID of the monitoring configuration to update
   * @return the updated monitoring configuration
   */
  @PutMapping("/monitoring-config/{id}")
  public MonitoringConfig replaceMonitoringConfig(@RequestBody MonitoringConfig newMonitoringConfig, @PathVariable String id) {
    return service.save(newMonitoringConfig);
  }

  /*
   * This method handles DELETE requests to delete a monitoring configuration from the repository.
   * 
   * @param id the id of the monitoring configuration to be deleted
   */
  @DeleteMapping("/monitoring-config/{id}")
  public void deleteMonitoringConfig(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new monitoring configuration in the repository.
   * 
   * @param newMonitoringConfig the monitoring configuration to be created
   * @return the created monitoring configuration
   */
  @PostMapping("/monitoring-config")
  public MonitoringConfig newMonitoringConfig(@RequestBody MonitoringConfig newMonitoringConfig) {
    return service.save(newMonitoringConfig);
  }
}
