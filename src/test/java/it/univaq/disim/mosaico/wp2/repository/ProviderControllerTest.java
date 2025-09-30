package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.univaq.disim.mosaico.wp2.repository.controller.ProviderController;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.service.ProviderService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for ProviderController.
 */
@WebMvcTest(ProviderController.class)
public class ProviderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private ProviderService providerService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Provider testProvider;
    
    @BeforeEach
    void setUp() {
        testProvider = new Provider(
            "provider1",
            "OpenAI",
            "AI company providing language models",
            "https://openai.com"
        );
    }
    
    @Test
    void testGetAllProviders() throws Exception {
        when(providerService.findAll()).thenReturn(List.of(testProvider));
        
        mockMvc.perform(get("/api/providers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("OpenAI"));
        
        verify(providerService, times(1)).findAll();
    }
    
    @Test
    void testGetProviderById() throws Exception {
        when(providerService.findById("provider1")).thenReturn(Optional.of(testProvider));
        
        mockMvc.perform(get("/api/providers/provider1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("OpenAI"))
                .andExpect(jsonPath("$.id").value("provider1"));
        
        verify(providerService, times(1)).findById("provider1");
    }
    
    @Test
    void testGetProviderByIdNotFound() throws Exception {
        when(providerService.findById("nonexistent")).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/api/providers/nonexistent"))
                .andExpect(status().isNotFound());
        
        verify(providerService, times(1)).findById("nonexistent");
    }
    
    @Test
    void testCreateProvider() throws Exception {
        when(providerService.save(any(Provider.class))).thenReturn(testProvider);
        
        mockMvc.perform(post("/api/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProvider)))
                .andExpect(status().isOk())  // Controller returns 200, not 201
                .andExpect(jsonPath("$.name").value("OpenAI"));
        
        verify(providerService, times(1)).save(any(Provider.class));
    }
    
    @Test
    void testDeleteProvider() throws Exception {
        doNothing().when(providerService).deleteById("provider1");
        
        mockMvc.perform(delete("/api/providers/provider1"))
                .andExpect(status().isNoContent());
        
        verify(providerService, times(1)).deleteById("provider1");
    }
}