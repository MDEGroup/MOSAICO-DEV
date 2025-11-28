package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {
    Logger logger = LoggerFactory.getLogger(VectorSearchService.class);

    private final VectorStore vectorStore;
    private final AgentRepository agentRepository;
    private final ProviderRepository providerRepository;

    public VectorSearchService(VectorStore vectorStore, AgentRepository agentRepository, ProviderRepository providerRepository) {
        this.vectorStore = vectorStore;
        this.agentRepository = agentRepository;
        this.providerRepository = providerRepository;
    }

    /**
     * Save an agent and index its textual representation in the vector store.
     */
    public Agent saveAndIndex(Agent agent) {
        // Ensure the provider relationship is managed: resolve existing provider or persist new one.
        Provider p = agent.getProvider();
        if (p != null) {
            if (p.getId() != null) {
                var existing = providerRepository.findById(p.getId());
                if (existing.isPresent()) {
                    agent.setProvider(existing.get());
                } else if (p.getName() != null || p.getContactUrl() != null) {
                    // Persist provider if it contains useful metadata. Ensure id is assigned (no @GeneratedValue).
                    if (p.getId() == null) {
                        p.setId(UUID.randomUUID().toString());
                    }
                    Provider savedProvider = providerRepository.save(p);
                    agent.setProvider(savedProvider);
                } else {
                    // No usable provider data; null the relation to avoid FK problems
                    logger.warn("Provider with id {} not found and payload has no metadata — nulling provider on agent save", p.getId());
                    agent.setProvider(null);
                }
            } else {
                // No id: if provider contains metadata, persist it and link; otherwise ignore
                if (p.getName() != null || p.getContactUrl() != null) {
                    if (p.getId() == null) {
                        p.setId(UUID.randomUUID().toString());
                    }
                    Provider savedProvider = providerRepository.save(p);
                    agent.setProvider(savedProvider);
                } else {
                    agent.setProvider(null);
                }
            }
        }

        Agent saved = agentRepository.save(agent);
        logger.info("Indexing agent with id: {}", saved.getId());
        String content = buildAgentText(saved);
        Document doc = new Document(content);
        // Optionally attach metadata like id
        doc.getMetadata().put("entityId", saved.getId());

        try {
            vectorStore.add(List.of(doc));
        } catch (Exception ex) {
            // Embedding/vector store may be unavailable (dev environment). Log and continue.
            logger.warn("Vector store add failed (agent indexed locally but not indexed in vector store): {}", ex.getMessage());
        }
        return saved;
    }

    /**
     * Perform a semantic similarity search and return the matched document contents.
     */
    public List<String> semanticSearch(String query, int topK) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(topK)
        );
        return docs.stream().map(Document::getContent).collect(Collectors.toList());
    }

    private String buildAgentText(Agent a) {
        StringBuilder sb = new StringBuilder();
        if (a.getName() != null) sb.append(a.getName()).append(" ");
        if (a.getDescription() != null) sb.append(a.getDescription()).append(" ");
        if (a.getRole() != null) sb.append(a.getRole()).append(" ");
        // add other fields you consider relevant
        return sb.toString().trim();
    }
}
