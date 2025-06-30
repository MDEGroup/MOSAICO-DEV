package it.univaq.disim.mosaico.wp2.repository.repository;

public class CommunicationProtocolNotFoundException extends RuntimeException {
    public CommunicationProtocolNotFoundException(String id) {
        super("Could not find communication protocol " + id);
    }
}