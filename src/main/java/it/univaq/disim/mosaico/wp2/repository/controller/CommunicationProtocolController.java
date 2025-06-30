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

import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.service.CommunicationProtocolService;

@RestController
/*
 * Copyright 2025 Mosaico
 * This class is the controller for CommunicationProtocol operations
 * It handles HTTP requests and responses for the CommunicationProtocol entity.
 */
public class CommunicationProtocolController {

  Logger logger = LoggerFactory.getLogger(CommunicationProtocolController.class);

  /*
   * This class is used to handle HTTP requests and responses for the CommunicationProtocol entity.
   * It uses the CommunicationProtocolService to perform CRUD operations on the CommunicationProtocol entity.
   */
  @Autowired
  /*
   * The CommunicationProtocolService is used to perform CRUD operations on the CommunicationProtocol entity.
   * It is autowired by Spring.
   */
  private final CommunicationProtocolService service;

  /*
   * This constructor is used to inject the CommunicationProtocolService dependency into the
   * CommunicationProtocolController class.
   * The @Autowired annotation is used to indicate that the CommunicationProtocolService bean
   * should be injected.
   * 
   * @param communicationProtocolService the CommunicationProtocolService bean to be injected
   */
  public CommunicationProtocolController(@Autowired CommunicationProtocolService communicationProtocolService) {
    this.service = communicationProtocolService;
  }

  /*
   * This method handles GET requests to retrieve all communication protocols from the repository.
   * 
   * @return a list of all communication protocols in the repository
   */
  @GetMapping("/communication-protocol")
  public List<CommunicationProtocol> all() {
    logger.info("GET /communication-protocol");
    return service.findAll();
  }

  /*
   * This method handles GET requests to retrieve a specific communication protocol by ID.
   * 
   * @param id the ID of the communication protocol to retrieve
   * @return the requested communication protocol
   */
  @GetMapping("/communication-protocol/{id}")
  public CommunicationProtocol one(@PathVariable String id) {
    return service.findById(id);
  }

  /*
   * This method handles PUT requests to update an existing communication protocol in the repository.
   * 
   * @param newCommunicationProtocol the updated communication protocol
   * @param id the ID of the communication protocol to update
   * @return the updated communication protocol
   */
  @PutMapping("/communication-protocol/{id}")
  public CommunicationProtocol replaceCommunicationProtocol(@RequestBody CommunicationProtocol newCommunicationProtocol, @PathVariable String id) {
    return service.save(newCommunicationProtocol);
  }

  /*
   * This method handles DELETE requests to delete a communication protocol from the repository.
   * 
   * @param id the id of the communication protocol to be deleted
   */
  @DeleteMapping("/communication-protocol/{id}")
  public void deleteCommunicationProtocol(@PathVariable String id) {
    service.deleteById(id);
  }

  /*
   * This method handles POST requests to create a new communication protocol in the repository.
   * 
   * @param newCommunicationProtocol the communication protocol to be created
   * @return the created communication protocol
   */
  @PostMapping("/communication-protocol")
  public CommunicationProtocol newCommunicationProtocol(@RequestBody CommunicationProtocol newCommunicationProtocol) {
    return service.save(newCommunicationProtocol);
  }
}
