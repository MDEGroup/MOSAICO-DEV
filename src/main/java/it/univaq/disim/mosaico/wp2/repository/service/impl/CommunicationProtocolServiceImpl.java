package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;
import it.univaq.disim.mosaico.wp2.repository.repository.CommunicationProtocolNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.CommunicationProtocolRepository;
import it.univaq.disim.mosaico.wp2.repository.service.CommunicationProtocolService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the CommunicationProtocol entity in the repository.
 * It provides methods to perform CRUD operations on the CommunicationProtocol entity.
 * It uses the CommunicationProtocolRepository to interact with the persistence store.
 */
@Service
public class CommunicationProtocolServiceImpl implements CommunicationProtocolService {

    /*
     * The communicationProtocolRepository is used to perform CRUD operations on the CommunicationProtocol entity. It is autowired by Spring.
    * The CommunicationProtocolRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final CommunicationProtocolRepository communicationProtocolRepository;

    /*
     * This constructor is used to inject the CommunicationProtocolRepository dependency into the CommunicationProtocolServiceImpl class.
     * The @Autowired annotation is used to indicate that the CommunicationProtocolRepository bean should be injected.
     */
    public CommunicationProtocolServiceImpl(@Autowired CommunicationProtocolRepository communicationProtocolRepository) {
        this.communicationProtocolRepository = communicationProtocolRepository;
    }

    @Override
    /*
     * Retrieves all communication protocols from the repository.
     * @return a list of all communication protocols
     */
    public List<CommunicationProtocol> findAll() {
        return communicationProtocolRepository.findAll();
    }

    @Override
    /*
     * Retrieves a communication protocol by its id from the repository.
     * @param id - the id of the communication protocol to be retrieved
     * @return the communication protocol with the given id
     * @throws CommunicationProtocolNotFoundException - if the communication protocol does not exist
     */
    public CommunicationProtocol findById(String id) {
        return communicationProtocolRepository.findById(id)
                .orElseThrow(() -> new CommunicationProtocolNotFoundException(id));
    }

    @Override
    /*
     * Saves the given communication protocol to the persistence store. If the communication protocol already exists, it is updated.
     * @param communicationProtocol - the communication protocol to be saved
     * @return the saved communication protocol
     */
    public CommunicationProtocol save(CommunicationProtocol communicationProtocol) {
        return communicationProtocolRepository.save(communicationProtocol);
    }

    @Override
    /*
     * Deletes the communication protocol with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the communication protocol to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        communicationProtocolRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given communication protocol in the persistence store. If the communication protocol does not exist, it throws a CommunicationProtocolNotFoundException.
     * @param communicationProtocol - the communication protocol to be updated
     * @return the updated communication protocol
     * @throws CommunicationProtocolNotFoundException - if the communication protocol does not exist
     */
    public CommunicationProtocol update(CommunicationProtocol communicationProtocol) {
        if (!communicationProtocolRepository.existsById(communicationProtocol.id())) {
            throw new CommunicationProtocolNotFoundException(communicationProtocol.id());
        }
        // Update the communication protocol in the repository
        return communicationProtocolRepository.save(communicationProtocol); 
    }
}
