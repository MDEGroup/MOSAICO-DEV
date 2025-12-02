package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;

/**
 * Service interface for Agent operations.
 */
public interface AgentService {
    
    List<Agent> findAll();
    Optional<Agent> findById(String id);
    Agent save(Agent agent);
    void deleteById(String id);
    
    List<Agent> findByName(String name);
    List<Agent> findByProvider(String providerId);
    List<Agent> findByRole(String role);
    List<Agent> findByIOModality(IOModality ioModality);
    List<Agent> semanticSearc(String version);
    List<Agent> semanticSearch(String query, Map<String,Object> filters, int topK);
}