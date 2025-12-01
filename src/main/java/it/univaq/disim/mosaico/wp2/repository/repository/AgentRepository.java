package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import java.util.List;

/**
 * JPA Repository interface for Agent entities.
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {

    List<Agent> findByName(String name);

    /**
     * Temporary method to find agents by provider id. Since Provider is kept transient
     * inside Agent for the first migration pass, this method may be implemented later
     * with a proper join once Provider is converted to a JPA entity.
     */
    // find agents by provider id via the relationship provider.id
    List<Agent> findByProvider_Id(String providerId);

    List<Agent> findByRole(String role);

    List<Agent> findByObjective(String objective);
}