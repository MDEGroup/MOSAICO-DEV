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

import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.service.MetricService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for Metric operations
 * It handles HTTP requests and responses for the Metric entity.
 */
public class MetricController {

  Logger logger = LoggerFactory.getLogger(MetricController.class);

  /*
   * This class is used to handle HTTP requests and responses for the Metric entity.
   * It uses the MetricService to perform CRUD operations on the Metric entity.
   */
  @Autowired
  /*
   * The MetricService is used to perform CRUD operations on the Metric entity.
   * It is autowired by Spring.
   */
  private final MetricService service;

  /*
   * This constructor is used to inject the MetricService dependency into the
   * MetricController class.
   * The @Autowired annotation is used to indicate that the MetricService bean
   * should be injected.
   * 
   * @param metricService the MetricService bean to be injected
   */
  public MetricController(@Autowired MetricService metricService) {
    this.service = metricService;
  }

  /*
   * This method handles GET requests to retrieve all metrics from the repository.
   * 
   * @return a list of all metrics in the repository
   */
  @GetMapping("/metric")
  public List<Metric> all() {
    logger.info("GET /metric");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific metric by ID.
   * 
   * @param id the ID of the metric to retrieve
   * @return the requested metric
   */
  @GetMapping("/metric/{id}")
  public Metric one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing metric in the repository.
   * 
   * @param newMetric the updated metric
   * @param id the ID of the metric to update
   * @return the updated metric
   */
  @PutMapping("/metric/{id}")
  public Metric replaceMetric(@RequestBody Metric newMetric, @PathVariable String id) {
    return service.save(newMetric);
  }

  /*
   * This method handles DELETE requests to delete a metric from the repository.
   * 
   * @param id the id of the metric to be deleted
   */
  @DeleteMapping("/metric/{id}")
  public void deleteMetric(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new metric in the repository.
   * 
   * @param newMetric the metric to be created
   * @return the created metric
   */
  @PostMapping("/metric")
  public Metric newMetric(@RequestBody Metric newMetric) {
    return service.save(newMetric);
  }
}
