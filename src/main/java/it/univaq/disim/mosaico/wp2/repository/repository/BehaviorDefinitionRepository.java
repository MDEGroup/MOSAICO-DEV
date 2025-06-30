package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.BehaviorDefinition;

@Repository
public interface BehaviorDefinitionRepository extends MongoRepository<BehaviorDefinition, String> {
    
    List<BehaviorDefinition> findByName(String name);
}