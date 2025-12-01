package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Tool;
import it.univaq.disim.mosaico.wp2.repository.repository.ToolNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.ToolRepository;
import it.univaq.disim.mosaico.wp2.repository.service.ToolService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the Tool entity in the repository.
 * It provides methods to perform CRUD operations on the Tool entity.
 * It uses the ToolRepository to interact with the persistence store.
 */
@Service
public class ToolServiceImpl implements ToolService {

    /*
     * The toolRepository is used to perform CRUD operations on the Tool entity. It is autowired by Spring.
    * The ToolRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final ToolRepository toolRepository;

    /*
     * This constructor is used to inject the ToolRepository dependency into the ToolServiceImpl class.
     * The @Autowired annotation is used to indicate that the ToolRepository bean should be injected.
     */
    public ToolServiceImpl(@Autowired ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    @Override
    /*
     * Retrieves all tools from the repository.
     * @return a list of all tools
     */
    public List<Tool> findAll() {
        return toolRepository.findAll();
    }

    @Override
    /*
     * Retrieves a tool by its id from the repository.
     * @param id - the id of the tool to be retrieved
     * @return the tool with the given id
     * @throws ToolNotFoundException - if the tool does not exist
     */
    public Tool findById(String id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new ToolNotFoundException(id));
    }

    @Override
    /*
     * Saves the given tool to the persistence store. If the tool already exists, it is updated.
     * @param tool - the tool to be saved
     * @return the saved tool
     */
    public Tool save(Tool tool) {
        return toolRepository.save(tool);
    }

    @Override
    /*
     * Deletes the tool with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the tool to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        toolRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given tool in the persistence store. If the tool does not exist, it throws a ToolNotFoundException.
     * @param tool - the tool to be updated
     * @return the updated tool
     * @throws ToolNotFoundException - if the tool does not exist
     */
    public Tool update(Tool tool) {
        if (!toolRepository.existsById(tool.id())) {
            throw new ToolNotFoundException(tool.id());
        }
        // Update the tool in the repository
        return toolRepository.save(tool); 
    }
}
