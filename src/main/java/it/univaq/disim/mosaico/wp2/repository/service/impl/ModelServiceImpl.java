package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Model;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.ModelRepository;
import it.univaq.disim.mosaico.wp2.repository.service.ModelService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the Model entity in the repository.
 * It provides methods to perform CRUD operations on the Model entity.
 * It uses the ModelRepository to interact with the persistence store.
 */
@Service
public class ModelServiceImpl implements ModelService {

    @Autowired
    /*
     * The modelRepository is used to perform CRUD operations on the Model entity. It is autowired by Spring.
     * The ModelRepository interface extends the MongoRepository interface, which provides methods for CRUD operations.
     */
    private final ModelRepository modelRepository;


    /*
     * This constructor is used to inject the ModelRepository dependency into the ModelServiceImpl class.
     * The @Autowired annotation is used to indicate that the ModelRepository bean should be injected.
     */
    public ModelServiceImpl(@Autowired ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    /*
     * Retrieves all models from the repository.
     * @return a list of all models
     */
    public List<Model> findAll() {
        return modelRepository.findAll();
    }

    @Override
    /*
     * Retrieves a model by its id from the repository.
     * @param id - the id of the model to be retrieved
     * @return the model with the given id
     * @throws ModelNotFoundException - if the model does not exist
     */
    public Model findById(String id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new ModelNotFoundException("Model not found with id: " + id));
    }

    @Override
    /*
     * Saves the given model to the persistence store. If the model already exists, it is updated.
     * @param model - the model to be saved
     * @return the saved model
     */
    public Model save(Model model) {
        return modelRepository.save(model);
    }

    /*
     * Deletes the entity with the given id. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the entity to be deleted
     * 
     * @throws IllegalArgumentException - in case the given id is null
     */
    @Override
    /*
     * Deletes the model with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the model to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        modelRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given model in the persistence store. If the model does not exist, it throws a ModelNotFoundException.
     * @param model - the model to be updated
     * @return the updated model
     * @throws ModelNotFoundException - if the model does not exist
     */
    public Model update(Model model) {
        if (!modelRepository.existsById(model.id())) {
            throw new ModelNotFoundException("Model not found with id: " + model.id());
        }
        // Update the model in the repository
        // You can add any additional logic here if needed
       return modelRepository.save(model); 
    }
}
