package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Model;

@Service
public interface ModelService {
    /*
     * This interface defines the contract for the ModelService.
     */

    /*
     * This method retrieves all models from the repository.
     * @return a list of all models
     */
    public List<Model> findAll();
    /*
     * This method retrieves a model by its id from the repository.
     * @param id - the id of the model to be retrieved
     * @return the model with the given id
     * @throws ModelNotFoundException - if the model does not exist
     */
    public Model findById(String id);
    /*
     * This method saves the given model to the persistence store. If the model already exists, it is updated.
     * @param model - the model to be saved
     * @return the saved model
     */
    public Model save(Model model);
    /* 
     * This method deletes a model by its id from the repository. If the model does not exist, it does nothing.
     * @param id - the id of the model to be deleted
     */
    public void deleteById(String id);
    /*
     * This method updates the given model in the persistence store. If the model does not exist, it throws a ModelNotFoundException.   
     * @param model - the model to be updated
     * @return the updated model
     * @throws ModelNotFoundException - if the model does not exist
     */
    public Model update(Model model);

}
