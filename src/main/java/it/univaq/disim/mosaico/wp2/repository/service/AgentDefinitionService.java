package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;

@Service
public interface AgentDefinitionService {
    /*
     * This interface defines the contract for the AgentDefinitionService.
     */

    /*
     * This method retrieves all agent definitions from the repository.
     * @return a list of all agent definitions
     */
    public List<AgentDefinition> findAll();
    
    /*
     * This method retrieves an agent definition by its id from the repository.
     * @param id - the id of the agent definition to be retrieved
     * @return the agent definition with the given id
     * @throws AgentDefinitionNotFoundException - if the agent definition does not exist
     */
    public AgentDefinition findById(String id);
    
    /*
     * This method saves the given agent definition to the persistence store. If the agent definition already exists, it is updated.
     * @param agentDefinition - the agent definition to be saved
     * @return the saved agent definition
     */
    public AgentDefinition save(AgentDefinition agentDefinition);
    
    /* 
     * This method deletes an agent definition by its id from the repository. If the agent definition does not exist, it does nothing.
     * @param id - the id of the agent definition to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given agent definition in the persistence store. If the agent definition does not exist, it throws a AgentDefinitionNotFoundException.   
     * @param agentDefinition - the agent definition to be updated
     * @return the updated agent definition
     * @throws AgentDefinitionNotFoundException - if the agent definition does not exist
     */
    public AgentDefinition update(AgentDefinition agentDefinition);
}
