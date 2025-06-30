package it.univaq.disim.mosaico.wp2.repository.repository;

public class CoordinationPatternNotFoundException extends RuntimeException {
    public CoordinationPatternNotFoundException(String id) {
        super("Could not find coordination pattern " + id);
    }
}