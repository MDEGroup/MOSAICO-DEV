package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;

/**
 * Service interface for Benchmark operations.
 */
public interface BenchmarkService {
    
    List<Benchmark> findAll();
    Optional<Benchmark> findById(String id);
    Benchmark save(Benchmark benchmark);
    void deleteById(String id);
    
    Benchmark findByDatasetRef(String datasetRef);
    Benchmark findByProtocolVersion(String protocolVersion);
}