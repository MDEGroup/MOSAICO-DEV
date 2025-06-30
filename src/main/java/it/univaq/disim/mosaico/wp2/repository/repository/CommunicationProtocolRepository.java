package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;

@Repository
public interface CommunicationProtocolRepository extends MongoRepository<CommunicationProtocol, String> {
    
    List<CommunicationProtocol> findByName(String name);
}