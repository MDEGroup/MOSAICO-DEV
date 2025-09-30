package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;

/**
 * Service interface for Skill operations.
 */
public interface SkillService {
    
    List<Skill> findAll();
    Optional<Skill> findById(String id);
    Skill save(Skill skill);
    void deleteById(String id);
    
    List<Skill> findByName(String name);
    List<Skill> findByLevel(ProficiencyLevel level);
    List<Skill> findByMinLevel(ProficiencyLevel minLevel);
}