package it.univaq.disim.mosaico.wp2.repository.repository;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String id) {
        super("Could not find tool definition " + id);
    }
}