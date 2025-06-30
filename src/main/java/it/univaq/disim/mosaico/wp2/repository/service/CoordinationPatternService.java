package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;

@Service
public interface CoordinationPatternService {
    /*
     * This interface defines the contract for the CoordinationPatternService.
     */

    /*
     * This method retrieves all coordination patterns from the repository.
     * @return a list of all coordination patterns
     */
    public List<CoordinationPattern> findAll();
    
    /*
     * This method retrieves a coordination pattern by its id from the repository.
     * @param id - the id of the coordination pattern to be retrieved
     * @return the coordination pattern with the given id
     * @throws CoordinationPatternNotFoundException - if the coordination pattern does not exist
     */
    public CoordinationPattern findById(String id);
    
    /*
     * This method saves the given coordination pattern to the persistence store. If the coordination pattern already exists, it is updated.
     * @param coordinationPattern - the coordination pattern to be saved
     * @return the saved coordination pattern
     */
    public CoordinationPattern save(CoordinationPattern coordinationPattern);
    
    /* 
     * This method deletes a coordination pattern by its id from the repository. If the coordination pattern does not exist, it does nothing.
     * @param id - the id of the coordination pattern to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given coordination pattern in the persistence store. If the coordination pattern does not exist, it throws a CoordinationPatternNotFoundException.   
     * @param coordinationPattern - the coordination pattern to be updated
     * @return the updated coordination pattern
     * @throws CoordinationPatternNotFoundException - if the coordination pattern does not exist
     */
    public CoordinationPattern update(CoordinationPattern coordinationPattern);
}
