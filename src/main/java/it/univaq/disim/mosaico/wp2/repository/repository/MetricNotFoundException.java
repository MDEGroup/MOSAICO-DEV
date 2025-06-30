package it.univaq.disim.mosaico.wp2.repository.repository;

public class MetricNotFoundException extends RuntimeException {
    public MetricNotFoundException(String id) {
        super("Could not find metric " + id);
    }
}