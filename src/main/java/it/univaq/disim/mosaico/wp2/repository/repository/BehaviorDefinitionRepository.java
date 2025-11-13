package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.BehaviorDefinition;

@Repository
public interface BehaviorDefinitionRepository extends JpaRepository<BehaviorDefinition, String> {
    
    List<BehaviorDefinition> findByName(String name);
}