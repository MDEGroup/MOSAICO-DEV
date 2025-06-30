package it.univaq.disim.mosaico.wp2.repository.repository;

public class BehaviorDefinitionNotFoundException extends RuntimeException {
    public BehaviorDefinitionNotFoundException(String id) {
        super("Could not find behavior definition " + id);
    }
}