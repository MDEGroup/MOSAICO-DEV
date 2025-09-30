package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.SoftEngTask;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;

/**
 * Service interface for SoftEngTask operations.
 */
public interface SoftEngTaskService {
    
    List<SoftEngTask> findAll();
    Optional<SoftEngTask> findById(String id);
    SoftEngTask save(SoftEngTask task);
    void deleteById(String id);
    
    List<SoftEngTask> findByName(String name);
    List<SoftEngTask> findByPrimaryKA(SwebokKAId primaryKA);
    List<SoftEngTask> findBySecondaryKA(SwebokKAId secondaryKA);
}