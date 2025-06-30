package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.BehaviorDefinition;

@Service
public interface BehaviorDefinitionService {
    /*
     * This interface defines the contract for the BehaviorDefinitionService.
     */

    /*
     * This method retrieves all behavior definitions from the repository.
     * @return a list of all behavior definitions
     */
    public List<BehaviorDefinition> findAll();
    
    /*
     * This method retrieves a behavior definition by its id from the repository.
     * @param id - the id of the behavior definition to be retrieved
     * @return the behavior definition with the given id
     * @throws BehaviorDefinitionNotFoundException - if the behavior definition does not exist
     */
    public BehaviorDefinition findById(String id);
    
    /*
     * This method saves the given behavior definition to the persistence store. If the behavior definition already exists, it is updated.
     * @param behaviorDefinition - the behavior definition to be saved
     * @return the saved behavior definition
     */
    public BehaviorDefinition save(BehaviorDefinition behaviorDefinition);
    
    /* 
     * This method deletes a behavior definition by its id from the repository. If the behavior definition does not exist, it does nothing.
     * @param id - the id of the behavior definition to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given behavior definition in the persistence store. If the behavior definition does not exist, it throws a BehaviorDefinitionNotFoundException.   
     * @param behaviorDefinition - the behavior definition to be updated
     * @return the updated behavior definition
     * @throws BehaviorDefinitionNotFoundException - if the behavior definition does not exist
     */
    public BehaviorDefinition update(BehaviorDefinition behaviorDefinition);
}
