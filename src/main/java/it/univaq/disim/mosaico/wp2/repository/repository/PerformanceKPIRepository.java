package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;

/**
 * Repository interface for PerformanceKPI entities.
 */
@Repository
public interface PerformanceKPIRepository extends MongoRepository<PerformanceKPI, String> {
    
    PerformanceKPI findByDescription(String description);
}