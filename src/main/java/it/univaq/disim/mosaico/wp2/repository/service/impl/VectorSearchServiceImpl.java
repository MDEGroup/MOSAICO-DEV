package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.service.VectorSearchService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VectorSearchServiceImpl implements VectorSearchService {
    Logger logger = LoggerFactory.getLogger(VectorSearchServiceImpl.class);

    private final VectorStore vectorStore;
    private final AgentRepository agentRepository;
    private final ProviderRepository providerRepository;

    public VectorSearchServiceImpl(VectorStore vectorStore, AgentRepository agentRepository,
            ProviderRepository providerRepository) {
        this.vectorStore = vectorStore;
        this.agentRepository = agentRepository;
        this.providerRepository = providerRepository;
    }

    /**
     * Save an agent and index its textual representation in the vector store.
     */
    @Override
    public Agent indexAgent(Agent saved) {
        Agent persisted = persistAgent(saved);

        logger.info("Indexing agent with id: {}", persisted.getId());
        String content = buildAgentText(persisted);
        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("entityType", "Agent");
        metadata.put("entityId", persisted.getId());
        Document doc = new Document(content, metadata);

        try {
            vectorStore.add(List.of(doc));
        } catch (Exception ex) {
            // Embedding/vector store may be unavailable (dev environment). Log and
            // continue.
            logger.warn("Vector store add failed (agent indexed locally but not indexed in vector store): {}",
                    ex.getMessage());
        }
        return persisted;
    }

    private Agent persistAgent(Agent agent) {
        if (agent == null) {
            return null;
        }

        if (agent.getProvider() != null) {
            handleProviderPersistence(agent);
        }

        return agentRepository.save(agent);
    }

    private void handleProviderPersistence(Agent agent) {
        var provider = agent.getProvider();
        if (provider.getId() != null) {
            providerRepository.findById(provider.getId()).ifPresent(agent::setProvider);
        } else {
            var persistedProvider = providerRepository.save(provider);
            agent.setProvider(persistedProvider);
        }
    }

    private String buildFilterExpression(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }

        return filters.entrySet().stream()
                .map(e -> {
                    String key = e.getKey();
                    String value = e.getValue().toString();
                    // escape apici singoli nel valore, per sicurezza
                    String escapedValue = value.replace("'", "''");
                    return key + " == '" + escapedValue + "'";
                })
                .collect(Collectors.joining(" AND "));
    }

    /**
     * Perform a semantic similarity search and return the matched document
     * contents.
     */
    @Override
    public Map<String, String> semanticSearch(String query, Map<String, Object> filters, int topK) {
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK).withFilterExpression(buildFilterExpression(filters)));

        return docs.stream()
                .filter(doc -> matchesFilters(doc, filters))
                .collect(Collectors.toMap(
                    doc -> (String) doc.getMetadata().get("entityId"), // key = id from metadata
                    Document::getContent,                         // value = content
                    (existing, replacement) -> existing,          // in case of duplicate ids, keep the first
                    LinkedHashMap::new                            // preserve order of results
            ));
    }

    private boolean matchesFilters(Document document, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }

        Map<String, Object> metadata = document.getMetadata();
        if (metadata == null || metadata.isEmpty()) {
            return false;
        }

        return filters.entrySet().stream()
                .allMatch(entry -> Objects.equals(metadata.get(entry.getKey()), entry.getValue()));
    }

    private String buildAgentText(Agent a) {
        StringBuilder sb = new StringBuilder();
        if (a.getName() != null)
            sb.append(a.getName()).append(" ");
        if (a.getDescription() != null)
            sb.append(a.getDescription()).append(" ");
        if (a.getRole() != null)
            sb.append(a.getRole()).append(" ");
        // add other fields you consider relevant
        return sb.toString().trim();
    }
}
