package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;
import it.univaq.disim.mosaico.wp2.repository.service.ProviderService;

/**
 * Implementation of ProviderService.
 */
@Service
public class ProviderServiceImpl implements ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Override
    public List<Provider> findAll() {
        return providerRepository.findAll();
    }
    
    @Override
    public Optional<Provider> findById(String id) {
        return providerRepository.findById(id);
    }
    
    @Override
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }
    
    @Override
    public void deleteById(String id) {
        providerRepository.deleteById(id);
    }
    
    @Override
    public List<Provider> findByName(String name) {
        return providerRepository.findByName(name);
    }
    
    @Override
    public Provider findByContactUrl(String contactUrl) {
        return providerRepository.findByContactUrl(contactUrl);
    }
}