package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.InteractionProtocol;
import java.util.List;

/**
 * JPA Repository interface for InteractionProtocol entities.
 */
@Repository
public interface InteractionProtocolRepository extends JpaRepository<InteractionProtocol, String> {

    List<InteractionProtocol> findByName(String name);

    List<InteractionProtocol> findByVersion(String version);

    InteractionProtocol findBySpecUrl(String specUrl);
}