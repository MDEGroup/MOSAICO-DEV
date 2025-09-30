package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.SoftEngTask;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import it.univaq.disim.mosaico.wp2.repository.repository.SoftEngTaskRepository;
import it.univaq.disim.mosaico.wp2.repository.service.SoftEngTaskService;

/**
 * Implementation of SoftEngTaskService.
 */
@Service
public class SoftEngTaskServiceImpl implements SoftEngTaskService {
    
    @Autowired
    private SoftEngTaskRepository softEngTaskRepository;
    
    @Override
    public List<SoftEngTask> findAll() {
        return softEngTaskRepository.findAll();
    }
    
    @Override
    public Optional<SoftEngTask> findById(String id) {
        return softEngTaskRepository.findById(id);
    }
    
    @Override
    public SoftEngTask save(SoftEngTask task) {
        return softEngTaskRepository.save(task);
    }
    
    @Override
    public void deleteById(String id) {
        softEngTaskRepository.deleteById(id);
    }
    
    @Override
    public List<SoftEngTask> findByName(String name) {
        return softEngTaskRepository.findByName(name);
    }
    
    @Override
    public List<SoftEngTask> findByPrimaryKA(SwebokKAId primaryKA) {
        return softEngTaskRepository.findByPrimaryKA(primaryKA);
    }
    
    @Override
    public List<SoftEngTask> findBySecondaryKA(SwebokKAId secondaryKA) {
        return softEngTaskRepository.findBySecondaryKAsContaining(secondaryKA);
    }
}