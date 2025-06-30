package it.univaq.disim.mosaico.wp2.repository.repository;

public class ToolNotFoundException extends RuntimeException {
    public ToolNotFoundException(String id) {
        super("Could not find tool definition " + id);
    }
}