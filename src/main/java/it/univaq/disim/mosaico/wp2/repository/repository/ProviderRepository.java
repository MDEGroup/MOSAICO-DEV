package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import java.util.List;

/**
 * JPA Repository interface for Provider entities.
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {

    List<Provider> findByName(String name);

    Provider findByContactUrl(String contactUrl);
}