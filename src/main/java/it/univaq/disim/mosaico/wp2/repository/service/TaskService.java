package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Task;

@Service
public interface TaskService {
    /*
     * This interface defines the contract for the TaskService.
     */

    /*
     * This method retrieves all tasks from the repository.
     * @return a list of all tasks
     */
    public List<Task> findAll();
    
    /*
     * This method retrieves a task by its id from the repository.
     * @param id - the id of the task to be retrieved
     * @return the task with the given id
     * @throws TaskNotFoundException - if the task does not exist
     */
    public Task findById(String id);
    
    /*
     * This method saves the given task to the persistence store. If the task already exists, it is updated.
     * @param task - the task to be saved
     * @return the saved task
     */
    public Task save(Task task);
    
    /* 
     * This method deletes a task by its id from the repository. If the task does not exist, it does nothing.
     * @param id - the id of the task to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given task in the persistence store. If the task does not exist, it throws a TaskNotFoundException.   
     * @param task - the task to be updated
     * @return the updated task
     * @throws TaskNotFoundException - if the task does not exist
     */
    public Task update(Task task);
}
