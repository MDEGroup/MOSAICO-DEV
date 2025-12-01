package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.AgentDefinition;

@Repository
public interface AgentDefinitionRepository extends JpaRepository<AgentDefinition, String> {
    // Questo repository estende JpaRepository per operazioni CRUD sull'entità AgentDefinition
    
    List<AgentDefinition> findByName(String name);
}