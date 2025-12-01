package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    
    List<Task> findByName(String name);
}