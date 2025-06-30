package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Tool;

@Repository
public interface ToolRepository extends MongoRepository<Tool, String> {
    
    List<Tool> findByName(String name);
}