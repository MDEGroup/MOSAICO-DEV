package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;

@Repository
public interface AgentDefinitionRepository extends MongoRepository<AgentDefinition, String> {
    // Questo repository estende MongoRepository per operazioni CRUD sull'entità AgentDefinition
    
    List<AgentDefinition> findByName(String name);
}