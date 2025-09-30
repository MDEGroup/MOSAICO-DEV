package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import java.util.List;

/**
 * Repository interface for Provider entities.
 */
@Repository
public interface ProviderRepository extends MongoRepository<Provider, String> {
    
    List<Provider> findByName(String name);
    Provider findByContactUrl(String contactUrl);
}