package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Tool;

@Service
public interface ToolService {
    /*
     * This interface defines the contract for the ToolService.
     */

    /*
     * This method retrieves all tools from the repository.
     * @return a list of all tools
     */
    public List<Tool> findAll();
    
    /*
     * This method retrieves a tool by its id from the repository.
     * @param id - the id of the tool to be retrieved
     * @return the tool with the given id
     * @throws ToolNotFoundException - if the tool does not exist
     */
    public Tool findById(String id);
    
    /*
     * This method saves the given tool to the persistence store. If the tool already exists, it is updated.
     * @param tool - the tool to be saved
     * @return the saved tool
     */
    public Tool save(Tool tool);
    
    /* 
     * This method deletes a tool by its id from the repository. If the tool does not exist, it does nothing.
     * @param id - the id of the tool to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given tool in the persistence store. If the tool does not exist, it throws a ToolNotFoundException.   
     * @param tool - the tool to be updated
     * @return the updated tool
     * @throws ToolNotFoundException - if the tool does not exist
     */
    public Tool update(Tool tool);
}
