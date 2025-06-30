package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.ToolDefinition;

@Service
public interface ToolDefinitionService {
    /*
     * This interface defines the contract for the ToolDefinitionService.
     */

    /*
     * This method retrieves all tool definitions from the repository.
     * @return a list of all tool definitions
     */
    public List<ToolDefinition> findAll();
    
    /*
     * This method retrieves a tool definition by its id from the repository.
     * @param id - the id of the tool definition to be retrieved
     * @return the tool definition with the given id
     * @throws ToolDefinitionNotFoundException - if the tool definition does not exist
     */
    public ToolDefinition findById(String id);
    
    /*
     * This method saves the given tool definition to the persistence store. If the tool definition already exists, it is updated.
     * @param toolDefinition - the tool definition to be saved
     * @return the saved tool definition
     */
    public ToolDefinition save(ToolDefinition toolDefinition);
    
    /* 
     * This method deletes a tool definition by its id from the repository. If the tool definition does not exist, it does nothing.
     * @param id - the id of the tool definition to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given tool definition in the persistence store. If the tool definition does not exist, it throws a ToolDefinitionNotFoundException.   
     * @param toolDefinition - the tool definition to be updated
     * @return the updated tool definition
     * @throws ToolDefinitionNotFoundException - if the tool definition does not exist
     */
    public ToolDefinition update(ToolDefinition toolDefinition);
}
