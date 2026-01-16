package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;
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
    @Autowired
    private ProviderRepository providerRepository;
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
        handleProviderLifecycle(agent);
        Agent savedAgent = agentRepository.save(agent);
        vectorSearchService.indexAgent(savedAgent);
        return savedAgent;
    }

    private void handleProviderLifecycle(Agent agent) {
        Provider provider = agent.getProvider();
        if (provider == null) {
            return;
        }

        String providerId = provider.getId();
        if (providerId != null && providerRepository.existsById(providerId)) {
            // Ensure we attach the managed entity to avoid transient state
            providerRepository.findById(providerId)
                    .ifPresent(agent::setProvider);
            return;
        }

        Provider persisted = providerRepository.save(provider);
        agent.setProvider(persisted);
    }
    
    @Override
    public void deleteById(String id) {
        agentRepository.deleteById(id);
        vectorSearchService.removeAgent(id);
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
    public List<Agent> semanticSearch(String query, Map<String, Object> filters, int topK) {
        Map<String, String> semanticResMap = vectorSearchService.semanticSearch(query, filters, topK);
        if (semanticResMap == null || semanticResMap.isEmpty()) {
            return List.of();
        }
        List<Agent> results = agentRepository.findAllById(
                semanticResMap.keySet()
        );
        return results;
    }
}