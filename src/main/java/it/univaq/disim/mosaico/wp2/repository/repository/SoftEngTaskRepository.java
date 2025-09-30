package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.SoftEngTask;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import java.util.List;

/**
 * Repository interface for SoftEngTask entities.
 */
@Repository
public interface SoftEngTaskRepository extends MongoRepository<SoftEngTask, String> {
    
    List<SoftEngTask> findByName(String name);
    List<SoftEngTask> findByPrimaryKA(SwebokKAId primaryKA);
    List<SoftEngTask> findBySecondaryKAsContaining(SwebokKAId secondaryKA);
}