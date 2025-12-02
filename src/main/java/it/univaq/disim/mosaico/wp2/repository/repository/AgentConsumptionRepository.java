package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.AgentConsumption;

/**
 * Repository interface for AgentConsumption entities.
 */
@Repository
public interface AgentConsumptionRepository extends JpaRepository<AgentConsumption, String> {
}
