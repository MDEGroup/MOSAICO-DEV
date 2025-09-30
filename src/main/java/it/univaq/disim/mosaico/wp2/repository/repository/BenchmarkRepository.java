package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;

/**
 * Repository interface for Benchmark entities.
 */
@Repository
public interface BenchmarkRepository extends MongoRepository<Benchmark, String> {
    
    Benchmark findByDatasetRef(String datasetRef);
    Benchmark findByProtocolVersion(String protocolVersion);
}