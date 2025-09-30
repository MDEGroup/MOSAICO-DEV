package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.service.ProviderService;

/**
 * Controller for Provider operations following MOSAICO taxonomy.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    Logger logger = LoggerFactory.getLogger(ProviderController.class);
    
    @Autowired
    private final ProviderService providerService;
    
    public ProviderController(@Autowired ProviderService providerService) {
        this.providerService = providerService;
    }
    
    /**
     * Get all providers.
     */
    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        logger.info("GET /api/providers");
        List<Provider> providers = providerService.findAll();
        return ResponseEntity.ok(providers);
    }
    
    /**
     * Get provider by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProviderById(@PathVariable String id) {
        logger.info("GET /api/providers/{}", id);
        Optional<Provider> provider = providerService.findById(id);
        return provider.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new provider.
     */
    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody Provider provider) {
        logger.info("POST /api/providers for provider: {}", provider.name());
        Provider savedProvider = providerService.save(provider);
        return ResponseEntity.ok(savedProvider);
    }
    
    /**
     * Update existing provider.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Provider> updateProvider(@PathVariable String id, @RequestBody Provider provider) {
        logger.info("PUT /api/providers/{}", id);
        Optional<Provider> existingProvider = providerService.findById(id);
        if (existingProvider.isPresent()) {
            Provider updatedProvider = providerService.save(provider);
            return ResponseEntity.ok(updatedProvider);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete provider.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable String id) {
        logger.info("DELETE /api/providers/{}", id);
        providerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Find providers by name.
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Provider>> findProvidersByName(@RequestParam String name) {
        logger.info("GET /api/providers/search/name?name={}", name);
        List<Provider> providers = providerService.findByName(name);
        return ResponseEntity.ok(providers);
    }
}