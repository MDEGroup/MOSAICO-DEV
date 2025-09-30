package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.ProviderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for ProviderService.
 */
@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;
    
    @InjectMocks
    private ProviderServiceImpl providerService;
    
    private Provider testProvider1;
    private Provider testProvider2;
    
    @BeforeEach
    void setUp() {
        testProvider1 = new Provider(
            "provider1",
            "OpenAI",
            "AI company providing language models",
            "https://openai.com"
        );
        
        testProvider2 = new Provider(
            "provider2",
            "Google",
            "Technology company with AI services",
            "https://google.com"
        );
    }
    
    @Test
    void testSaveProvider() {
        when(providerRepository.save(testProvider1)).thenReturn(testProvider1);
        
        Provider savedProvider = providerService.save(testProvider1);
        
        assertNotNull(savedProvider);
        assertEquals(testProvider1.name(), savedProvider.name());
        verify(providerRepository, times(1)).save(testProvider1);
    }
    
    @Test
    void testFindById() {
        when(providerRepository.findById("provider1")).thenReturn(Optional.of(testProvider1));
        
        Optional<Provider> foundProvider = providerService.findById("provider1");
        
        assertTrue(foundProvider.isPresent());
        assertEquals(testProvider1.name(), foundProvider.get().name());
        verify(providerRepository, times(1)).findById("provider1");
    }
    
    @Test
    void testFindByIdNotFound() {
        when(providerRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        Optional<Provider> foundProvider = providerService.findById("nonexistent");
        
        assertFalse(foundProvider.isPresent());
        verify(providerRepository, times(1)).findById("nonexistent");
    }
    
    @Test
    void testFindAll() {
        List<Provider> providers = List.of(testProvider1, testProvider2);
        when(providerRepository.findAll()).thenReturn(providers);
        
        List<Provider> foundProviders = providerService.findAll();
        
        assertEquals(2, foundProviders.size());
        assertEquals(testProvider1.name(), foundProviders.get(0).name());
        assertEquals(testProvider2.name(), foundProviders.get(1).name());
        verify(providerRepository, times(1)).findAll();
    }
    
    @Test
    void testFindByName() {
        when(providerRepository.findByName("OpenAI")).thenReturn(List.of(testProvider1));
        
        List<Provider> openAIProviders = providerService.findByName("OpenAI");
        
        assertEquals(1, openAIProviders.size());
        assertEquals(testProvider1.name(), openAIProviders.get(0).name());
        verify(providerRepository, times(1)).findByName("OpenAI");
    }
    
    @Test
    void testDeleteById() {
        doNothing().when(providerRepository).deleteById("provider1");
        
        providerService.deleteById("provider1");
        
        verify(providerRepository, times(1)).deleteById("provider1");
    }
    
    @Test
    void testUpdateProvider() {
        Provider updatedProvider = new Provider(
            "provider1",
            "OpenAI Updated",
            "Updated AI company description",
            "https://openai.com/updated"
        );
        
        when(providerRepository.save(updatedProvider)).thenReturn(updatedProvider);
        
        Provider result = providerService.save(updatedProvider);
        
        assertEquals("OpenAI Updated", result.name());
        assertEquals("Updated AI company description", result.description());
        verify(providerRepository, times(1)).save(updatedProvider);
    }
    
    @Test
    void testFindByNameEmptyResult() {
        when(providerRepository.findByName("NonExistent")).thenReturn(List.of());
        
        List<Provider> providers = providerService.findByName("NonExistent");
        
        assertTrue(providers.isEmpty());
        verify(providerRepository, times(1)).findByName("NonExistent");
    }
}