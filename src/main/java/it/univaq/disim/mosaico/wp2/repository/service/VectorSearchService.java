package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import java.util.Map;

public interface VectorSearchService {
 
    /**
     * Save an agent and index its textual representation in the vector store.
     */
    public Agent indexAgent(Agent saved);

    /**
     * Perform a semantic similarity search and return the matched document
     * contents.
     */
    public Map<String, String> semanticSearch(String query, Map<String, Object> filters, int topK);
}
