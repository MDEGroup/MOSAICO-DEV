package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.repository.SkillRepository;
import it.univaq.disim.mosaico.wp2.repository.service.SkillService;

/**
 * Implementation of SkillService.
 */
@Service
public class SkillServiceImpl implements SkillService {
    
    @Autowired
    private SkillRepository skillRepository;
    
    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }
    
    @Override
    public Optional<Skill> findById(String id) {
        return skillRepository.findById(id);
    }
    
    @Override
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }
    
    @Override
    public void deleteById(String id) {
        skillRepository.deleteById(id);
    }
    
    @Override
    public List<Skill> findByName(String name) {
        return skillRepository.findByName(name);
    }
    
    @Override
    public List<Skill> findByLevel(ProficiencyLevel level) {
        return skillRepository.findByLevel(level);
    }
    
    @Override
    public List<Skill> findByMinLevel(ProficiencyLevel minLevel) {
        return skillRepository.findByLevelGreaterThanEqual(minLevel);
    }
}