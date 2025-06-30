package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentDefinitionNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentDefinitionRepository;
import it.univaq.disim.mosaico.wp2.repository.service.AgentDefinitionService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the AgentDefinition entity in the repository.
 * It provides methods to perform CRUD operations on the AgentDefinition entity.
 * It uses the AgentDefinitionRepository to interact with the persistence store.
 */
@Service
public class AgentDefinitionServiceImpl implements AgentDefinitionService {

    /*
     * The agentDefinitionRepository is used to perform CRUD operations on the AgentDefinition entity. It is autowired by Spring.
     * The AgentDefinitionRepository interface extends the MongoRepository interface, which provides methods for CRUD operations.
     */
    private final AgentDefinitionRepository agentDefinitionRepository;

    /*
     * This constructor is used to inject the AgentDefinitionRepository dependency into the AgentDefinitionServiceImpl class.
     * The @Autowired annotation is used to indicate that the AgentDefinitionRepository bean should be injected.
     */
    public AgentDefinitionServiceImpl(@Autowired AgentDefinitionRepository agentDefinitionRepository) {
        this.agentDefinitionRepository = agentDefinitionRepository;
    }

    @Override
    /*
     * Retrieves all agent definitions from the repository.
     * @return a list of all agent definitions
     */
    public List<AgentDefinition> findAll() {
        return agentDefinitionRepository.findAll();
    }

    @Override
    /*
     * Retrieves an agent definition by its id from the repository.
     * @param id - the id of the agent definition to be retrieved
     * @return the agent definition with the given id
     * @throws AgentDefinitionNotFoundException - if the agent definition does not exist
     */
    public AgentDefinition findById(String id) {
        return agentDefinitionRepository.findById(id)
                .orElseThrow(() -> new AgentDefinitionNotFoundException(id));
    }

    @Override
    /*
     * Saves the given agent definition to the persistence store. If the agent definition already exists, it is updated.
     * @param agentDefinition - the agent definition to be saved
     * @return the saved agent definition
     */
    public AgentDefinition save(AgentDefinition agentDefinition) {
        return agentDefinitionRepository.save(agentDefinition);
    }

    @Override
    /*
     * Deletes the agent definition with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the agent definition to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        agentDefinitionRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given agent definition in the persistence store. If the agent definition does not exist, it throws a AgentDefinitionNotFoundException.
     * @param agentDefinition - the agent definition to be updated
     * @return the updated agent definition
     * @throws AgentDefinitionNotFoundException - if the agent definition does not exist
     */
    public AgentDefinition update(AgentDefinition agentDefinition) {
        if (!agentDefinitionRepository.existsById(agentDefinition.id())) {
            throw new AgentDefinitionNotFoundException(agentDefinition.id());
        }
        // Update the agent definition in the repository
        return agentDefinitionRepository.save(agentDefinition); 
    }
}
