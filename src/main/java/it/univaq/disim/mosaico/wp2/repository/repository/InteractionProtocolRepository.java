package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.InteractionProtocol;
import java.util.List;

/**
 * Repository interface for InteractionProtocol entities.
 */
@Repository
public interface InteractionProtocolRepository extends MongoRepository<InteractionProtocol, String> {
    
    List<InteractionProtocol> findByName(String name);
    List<InteractionProtocol> findByVersion(String version);
    InteractionProtocol findBySpecUrl(String specUrl);
}