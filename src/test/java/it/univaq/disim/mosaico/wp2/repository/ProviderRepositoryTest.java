package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ProviderRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ProviderRepositoryTest {

    @Autowired
    private ProviderRepository providerRepository;
    
    private Provider testProvider;
    
    @BeforeEach
    void setUp() {
        providerRepository.deleteAll();
        
        testProvider = new Provider(
            "OpenAI",
            "AI services provider",
            "https://openai.com"
        );
    }
    
    @Test
    void testSaveAndFindById() {
        Provider savedProvider = providerRepository.save(testProvider);
        
        Optional<Provider> foundProvider = providerRepository.findById(savedProvider.id());
        
        assertTrue(foundProvider.isPresent());
        assertEquals(testProvider.name(), foundProvider.get().name());
        assertEquals(testProvider.contactUrl(), foundProvider.get().contactUrl());
    }
    
    @Test
    void testFindByName() {
        providerRepository.save(testProvider);
        
        List<Provider> providers = providerRepository.findByName("OpenAI");
        
        assertFalse(providers.isEmpty());
        assertEquals(1, providers.size());
        assertEquals(testProvider.name(), providers.get(0).name());
    }
    
    @Test
    void testFindByContactUrl() {
        providerRepository.save(testProvider);
        
        Provider foundProvider = providerRepository.findByContactUrl("https://openai.com");
        
        assertNotNull(foundProvider);
        assertEquals(testProvider.name(), foundProvider.name());
    }
    
    @Test
    void testFindByNameEmpty() {
        List<Provider> providers = providerRepository.findByName("NonExistent");
        
        assertTrue(providers.isEmpty());
    }
    
    @Test
    void testDeleteById() {
        Provider savedProvider = providerRepository.save(testProvider);
        
        providerRepository.deleteById(savedProvider.id());
        
        Optional<Provider> foundProvider = providerRepository.findById(savedProvider.id());
        assertFalse(foundProvider.isPresent());
    }
}