package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Task;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    
    List<Task> findByName(String name);
}