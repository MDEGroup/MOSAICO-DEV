package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;

@Service
public interface CommunicationProtocolService {
    /*
     * This interface defines the contract for the CommunicationProtocolService.
     */

    /*
     * This method retrieves all communication protocols from the repository.
     * @return a list of all communication protocols
     */
    public List<CommunicationProtocol> findAll();
    
    /*
     * This method retrieves a communication protocol by its id from the repository.
     * @param id - the id of the communication protocol to be retrieved
     * @return the communication protocol with the given id
     * @throws CommunicationProtocolNotFoundException - if the communication protocol does not exist
     */
    public CommunicationProtocol findById(String id);
    
    /*
     * This method saves the given communication protocol to the persistence store. If the communication protocol already exists, it is updated.
     * @param communicationProtocol - the communication protocol to be saved
     * @return the saved communication protocol
     */
    public CommunicationProtocol save(CommunicationProtocol communicationProtocol);
    
    /* 
     * This method deletes a communication protocol by its id from the repository. If the communication protocol does not exist, it does nothing.
     * @param id - the id of the communication protocol to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given communication protocol in the persistence store. If the communication protocol does not exist, it throws a CommunicationProtocolNotFoundException.   
     * @param communicationProtocol - the communication protocol to be updated
     * @return the updated communication protocol
     * @throws CommunicationProtocolNotFoundException - if the communication protocol does not exist
     */
    public CommunicationProtocol update(CommunicationProtocol communicationProtocol);
}
