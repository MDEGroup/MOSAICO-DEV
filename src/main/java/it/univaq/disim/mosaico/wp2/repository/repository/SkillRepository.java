package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import java.util.List;

/**
 * Repository interface for Skill entities.
 */
@Repository
public interface SkillRepository extends MongoRepository<Skill, String> {
    
    List<Skill> findByName(String name);
    List<Skill> findByLevel(ProficiencyLevel level);
    List<Skill> findByLevelGreaterThanEqual(ProficiencyLevel minLevel);
}