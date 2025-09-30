package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import java.util.List;

/**
 * Repository interface for Agent entities.
 */
@Repository
public interface AgentRepository extends MongoRepository<Agent, String> {
    
    List<Agent> findByName(String name);
    List<Agent> findByProvider_Id(String providerId);
    List<Agent> findByRole(String role);
    List<Agent> findByObjective(String objective);
}