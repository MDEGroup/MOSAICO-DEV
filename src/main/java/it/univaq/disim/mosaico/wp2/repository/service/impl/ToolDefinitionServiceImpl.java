package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.ToolDefinition;
import it.univaq.disim.mosaico.wp2.repository.repository.ToolDefinitionNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.ToolDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.service.ToolDefinitionService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the ToolDefinition entity in the repository.
 * It provides methods to perform CRUD operations on the ToolDefinition entity.
 * It uses the ToolDefinitionRepository to interact with the persistence store.
 */
@Service
public class ToolDefinitionServiceImpl implements ToolDefinitionService {

    /*
     * The toolDefinitionRepository is used to perform CRUD operations on the ToolDefinition entity. It is autowired by Spring.
    * The ToolDefinitionRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final ToolDefinitionRepository toolDefinitionRepository;

    /*
     * This constructor is used to inject the ToolDefinitionRepository dependency into the ToolDefinitionServiceImpl class.
     * The @Autowired annotation is used to indicate that the ToolDefinitionRepository bean should be injected.
     */
    public ToolDefinitionServiceImpl(@Autowired ToolDefinitionRepository toolDefinitionRepository) {
        this.toolDefinitionRepository = toolDefinitionRepository;
    }

    @Override
    /*
     * Retrieves all tool definitions from the repository.
     * @return a list of all tool definitions
     */
    public List<ToolDefinition> findAll() {
        return toolDefinitionRepository.findAll();
    }

    @Override
    /*
     * Retrieves a tool definition by its id from the repository.
     * @param id - the id of the tool definition to be retrieved
     * @return the tool definition with the given id
     * @throws ToolDefinitionNotFoundException - if the tool definition does not exist
     */
    public ToolDefinition findById(String id) {
        return toolDefinitionRepository.findById(id)
                .orElseThrow(() -> new ToolDefinitionNotFoundException(id));
    }

    @Override
    /*
     * Saves the given tool definition to the persistence store. If the tool definition already exists, it is updated.
     * @param toolDefinition - the tool definition to be saved
     * @return the saved tool definition
     */
    public ToolDefinition save(ToolDefinition toolDefinition) {
        return toolDefinitionRepository.save(toolDefinition);
    }

    @Override
    /*
     * Deletes the tool definition with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the tool definition to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        toolDefinitionRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given tool definition in the persistence store. If the tool definition does not exist, it throws a ToolDefinitionNotFoundException.
     * @param toolDefinition - the tool definition to be updated
     * @return the updated tool definition
     * @throws ToolDefinitionNotFoundException - if the tool definition does not exist
     */
    public ToolDefinition update(ToolDefinition toolDefinition) {
        if (!toolDefinitionRepository.existsById(toolDefinition.id())) {
            throw new ToolDefinitionNotFoundException(toolDefinition.id());
        }
        // Update the tool definition in the repository
        return toolDefinitionRepository.save(toolDefinition); 
    }
}
