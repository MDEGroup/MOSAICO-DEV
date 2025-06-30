package it.univaq.disim.mosaico.wp2.repository.repository;

public class MonitoringConfigNotFoundException extends RuntimeException {
    public MonitoringConfigNotFoundException(String id) {
        super("Could not find monitoring configuration " + id);
    }
}