package it.univaq.disim.mosaico.wp2.repository.repository;

public class ToolDefinitionNotFoundException extends RuntimeException {
    public ToolDefinitionNotFoundException(String id) {
        super("Could not find tool definition " + id);
    }
}