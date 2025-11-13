package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;
import it.univaq.disim.mosaico.wp2.repository.repository.CoordinationPatternNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.CoordinationPatternRepository;
import it.univaq.disim.mosaico.wp2.repository.service.CoordinationPatternService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the CoordinationPattern entity in the repository.
 * It provides methods to perform CRUD operations on the CoordinationPattern entity.
 * It uses the CoordinationPatternRepository to interact with the persistence store.
 */
@Service
public class CoordinationPatternServiceImpl implements CoordinationPatternService {

    /*
     * The coordinationPatternRepository is used to perform CRUD operations on the CoordinationPattern entity. It is autowired by Spring.
    * The CoordinationPatternRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final CoordinationPatternRepository coordinationPatternRepository;

    /*
     * This constructor is used to inject the CoordinationPatternRepository dependency into the CoordinationPatternServiceImpl class.
     * The @Autowired annotation is used to indicate that the CoordinationPatternRepository bean should be injected.
     */
    public CoordinationPatternServiceImpl(@Autowired CoordinationPatternRepository coordinationPatternRepository) {
        this.coordinationPatternRepository = coordinationPatternRepository;
    }

    @Override
    /*
     * Retrieves all coordination patterns from the repository.
     * @return a list of all coordination patterns
     */
    public List<CoordinationPattern> findAll() {
        return coordinationPatternRepository.findAll();
    }

    @Override
    /*
     * Retrieves a coordination pattern by its id from the repository.
     * @param id - the id of the coordination pattern to be retrieved
     * @return the coordination pattern with the given id
     * @throws CoordinationPatternNotFoundException - if the coordination pattern does not exist
     */
    public CoordinationPattern findById(String id) {
        return coordinationPatternRepository.findById(id)
                .orElseThrow(() -> new CoordinationPatternNotFoundException(id));
    }

    @Override
    /*
     * Saves the given coordination pattern to the persistence store. If the coordination pattern already exists, it is updated.
     * @param coordinationPattern - the coordination pattern to be saved
     * @return the saved coordination pattern
     */
    public CoordinationPattern save(CoordinationPattern coordinationPattern) {
        return coordinationPatternRepository.save(coordinationPattern);
    }

    @Override
    /*
     * Deletes the coordination pattern with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the coordination pattern to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        coordinationPatternRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given coordination pattern in the persistence store. If the coordination pattern does not exist, it throws a CoordinationPatternNotFoundException.
     * @param coordinationPattern - the coordination pattern to be updated
     * @return the updated coordination pattern
     * @throws CoordinationPatternNotFoundException - if the coordination pattern does not exist
     */
    public CoordinationPattern update(CoordinationPattern coordinationPattern) {
        if (!coordinationPatternRepository.existsById(coordinationPattern.id())) {
            throw new CoordinationPatternNotFoundException(coordinationPattern.id());
        }
        // Update the coordination pattern in the repository
        return coordinationPatternRepository.save(coordinationPattern); 
    }
}
