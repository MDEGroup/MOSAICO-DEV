package it.univaq.disim.mosaico.wp2.repository.repository;

public class AgentDefinitionNotFoundException extends RuntimeException {
    public AgentDefinitionNotFoundException(String id) {
        super("Could not find agent definition " + id);
    }
}