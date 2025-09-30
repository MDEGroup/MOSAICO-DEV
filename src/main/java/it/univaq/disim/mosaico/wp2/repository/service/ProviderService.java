package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;

/**
 * Service interface for Provider operations.
 */
public interface ProviderService {
    
    List<Provider> findAll();
    Optional<Provider> findById(String id);
    Provider save(Provider provider);
    void deleteById(String id);
    
    List<Provider> findByName(String name);
    Provider findByContactUrl(String contactUrl);
}