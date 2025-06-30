package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.BehaviorDefinition;
import it.univaq.disim.mosaico.wp2.repository.repository.BehaviorDefinitionNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.BehaviorDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BehaviorDefinitionService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the BehaviorDefinition entity in the repository.
 * It provides methods to perform CRUD operations on the BehaviorDefinition entity.
 * It uses the BehaviorDefinitionRepository to interact with the persistence store.
 */
@Service
public class BehaviorDefinitionServiceImpl implements BehaviorDefinitionService {

    /*
     * The behaviorDefinitionRepository is used to perform CRUD operations on the BehaviorDefinition entity. It is autowired by Spring.
     * The BehaviorDefinitionRepository interface extends the MongoRepository interface, which provides methods for CRUD operations.
     */
    private final BehaviorDefinitionRepository behaviorDefinitionRepository;

    /*
     * This constructor is used to inject the BehaviorDefinitionRepository dependency into the BehaviorDefinitionServiceImpl class.
     * The @Autowired annotation is used to indicate that the BehaviorDefinitionRepository bean should be injected.
     */
    public BehaviorDefinitionServiceImpl(@Autowired BehaviorDefinitionRepository behaviorDefinitionRepository) {
        this.behaviorDefinitionRepository = behaviorDefinitionRepository;
    }

    @Override
    /*
     * Retrieves all behavior definitions from the repository.
     * @return a list of all behavior definitions
     */
    public List<BehaviorDefinition> findAll() {
        return behaviorDefinitionRepository.findAll();
    }

    @Override
    /*
     * Retrieves a behavior definition by its id from the repository.
     * @param id - the id of the behavior definition to be retrieved
     * @return the behavior definition with the given id
     * @throws BehaviorDefinitionNotFoundException - if the behavior definition does not exist
     */
    public BehaviorDefinition findById(String id) {
        return behaviorDefinitionRepository.findById(id)
                .orElseThrow(() -> new BehaviorDefinitionNotFoundException(id));
    }

    @Override
    /*
     * Saves the given behavior definition to the persistence store. If the behavior definition already exists, it is updated.
     * @param behaviorDefinition - the behavior definition to be saved
     * @return the saved behavior definition
     */
    public BehaviorDefinition save(BehaviorDefinition behaviorDefinition) {
        return behaviorDefinitionRepository.save(behaviorDefinition);
    }

    @Override
    /*
     * Deletes the behavior definition with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the behavior definition to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        behaviorDefinitionRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given behavior definition in the persistence store. If the behavior definition does not exist, it throws a BehaviorDefinitionNotFoundException.
     * @param behaviorDefinition - the behavior definition to be updated
     * @return the updated behavior definition
     * @throws BehaviorDefinitionNotFoundException - if the behavior definition does not exist
     */
    public BehaviorDefinition update(BehaviorDefinition behaviorDefinition) {
        if (!behaviorDefinitionRepository.existsById(behaviorDefinition.id())) {
            throw new BehaviorDefinitionNotFoundException(behaviorDefinition.id());
        }
        // Update the behavior definition in the repository
        return behaviorDefinitionRepository.save(behaviorDefinition); 
    }
}
