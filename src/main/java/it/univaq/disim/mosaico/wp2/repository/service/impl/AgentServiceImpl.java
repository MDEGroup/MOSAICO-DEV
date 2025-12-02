package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;
import it.univaq.disim.mosaico.wp2.repository.service.VectorSearchService;

/**
 * Implementation of AgentService.
 */
@Service
public class AgentServiceImpl implements AgentService {
    
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private VectorSearchService vectorSearchService;
    @Override
    public List<Agent> findAll() {
        return agentRepository.findAll();
    }
    
    @Override
    public Optional<Agent> findById(String id) {
        return agentRepository.findById(id);
    }
    
    @Override
    public Agent save(Agent agent) {
        Agent savedAgent = agentRepository.save(agent);
        vectorSearchService.indexAgent(savedAgent);
        return savedAgent;
    }
    
    @Override
    public void deleteById(String id) {
        agentRepository.deleteById(id);
    }
    
    @Override
    public List<Agent> findByName(String name) {
        return agentRepository.findByName(name);
    }
    
    @Override
    public List<Agent> findByProvider(String providerId) {
        // Use the repository method implemented for the migration pass
        return agentRepository.findByProvider_Id(providerId);
    }
    
    @Override
    public List<Agent> findByRole(String role) {
        return agentRepository.findByRole(role);
    }
    
    @Override
    public List<Agent> findByIOModality(IOModality ioModality) {
        // This would require custom query implementation
        return agentRepository.findAll().stream()
                .filter(agent -> agent.getIoModalities().contains(ioModality))
                .toList();
    }

    @Override
    public List<Agent> semanticSearc(String version) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'semanticSearc'");
    }

    @Override
    public List<Agent> semanticSearch(String query, Map<String, Object> filters, int topK) {
        filters.put("entityType", "Agent");
        List<String> results = vectorSearchService.semanticSearch(query, filters, topK);
        for (String content : results) {
            System.out.println("Semantic search result content: " + content);
        }
        return null;
    }
    
}