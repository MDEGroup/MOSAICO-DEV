package it.univaq.disim.mosaico.wp2.repository.repository;

/*
 * Copyright 2025 Mosaico
 * This class manage exceptions for the repository
 */
public class EmployeeNotFoundException extends RuntimeException {
    /*
     * This class is used to handle exceptions when a model is not found in the repository.
     * 
     * @param id the id of the model that was not found
     */
    public EmployeeNotFoundException(String id) {
      super("Could not find model " + id);
    }
}
