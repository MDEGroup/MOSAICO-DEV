package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.Map;

/**
 * Request payload for /api/vector/agents/search endpoint.
 */
public class SemanticSearchRequest {

    private String query;
    private Map<String, Object> filters;
    private Integer topK;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }
}