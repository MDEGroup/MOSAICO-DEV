package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Task;
import it.univaq.disim.mosaico.wp2.repository.repository.TaskNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.TaskRepository;
import it.univaq.disim.mosaico.wp2.repository.service.TaskService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the Task entity in the repository.
 * It provides methods to perform CRUD operations on the Task entity.
 * It uses the TaskRepository to interact with the persistence store.
 */
@Service
public class TaskServiceImpl implements TaskService {

    /*
     * The taskRepository is used to perform CRUD operations on the Task entity. It is autowired by Spring.
    * The TaskRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final TaskRepository taskRepository;

    /*
     * This constructor is used to inject the TaskRepository dependency into the TaskServiceImpl class.
     * The @Autowired annotation is used to indicate that the TaskRepository bean should be injected.
     */
    public TaskServiceImpl(@Autowired TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    /*
     * Retrieves all tasks from the repository.
     * @return a list of all tasks
     */
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    /*
     * Retrieves a task by its id from the repository.
     * @param id - the id of the task to be retrieved
     * @return the task with the given id
     * @throws TaskNotFoundException - if the task does not exist
     */
    public Task findById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    /*
     * Saves the given task to the persistence store. If the task already exists, it is updated.
     * @param task - the task to be saved
     * @return the saved task
     */
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    /*
     * Deletes the task with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the task to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        taskRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given task in the persistence store. If the task does not exist, it throws a TaskNotFoundException.
     * @param task - the task to be updated
     * @return the updated task
     * @throws TaskNotFoundException - if the task does not exist
     */
    public Task update(Task task) {
        if (!taskRepository.existsById(task.id())) {
            throw new TaskNotFoundException(task.id());
        }
        // Update the task in the repository
        return taskRepository.save(task); 
    }
}
