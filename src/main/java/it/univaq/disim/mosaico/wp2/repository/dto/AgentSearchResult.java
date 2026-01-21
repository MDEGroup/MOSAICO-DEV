package it.univaq.disim.mosaico.wp2.repository.dto;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;

/**
 * DTO that wraps an Agent with its similarity score from semantic search.
 */
public class AgentSearchResult {

    private Agent agent;
    private Double similarityScore;

    public AgentSearchResult() {}

    public AgentSearchResult(Agent agent, Double similarityScore) {
        this.agent = agent;
        this.similarityScore = similarityScore;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }
}
