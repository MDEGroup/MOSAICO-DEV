package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.ToolDefinition;

@Repository
public interface ToolDefinitionRepository extends MongoRepository<ToolDefinition, String> {
    
    List<ToolDefinition> findByName(String name);
}