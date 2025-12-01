package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.AgentUsage;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AgentUsage entities.
 */
@Repository
public interface AgentUsageRepository extends JpaRepository<AgentUsage, String> {
    
    // use relationship path to agent id
    List<AgentUsage> findByAgent_Id(String agentId);
    List<AgentUsage> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AgentUsage> findByAgent_IdAndTimestampBetween(String agentId, LocalDateTime start, LocalDateTime end);
}