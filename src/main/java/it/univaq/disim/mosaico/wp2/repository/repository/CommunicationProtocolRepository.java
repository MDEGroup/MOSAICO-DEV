package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.CommunicationProtocol;

@Repository
public interface CommunicationProtocolRepository extends JpaRepository<CommunicationProtocol, String> {
    
    List<CommunicationProtocol> findByName(String name);
}