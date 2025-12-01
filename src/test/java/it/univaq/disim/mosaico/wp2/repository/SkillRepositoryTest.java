package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.repository.SkillRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SkillRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
public class SkillRepositoryTest {

    @Autowired
    private SkillRepository skillRepository;
    
    private Skill testSkill1;
    private Skill testSkill2;
    
    @BeforeEach
    void setUp() {
        skillRepository.deleteAll();
        
        testSkill1 = new Skill(
            "skill1",
            "Java Programming",
            "Object-oriented programming in Java",
            ProficiencyLevel.EXPERT,
            LocalDateTime.now(),
            List.of("task1", "task2")
        );
        
        testSkill2 = new Skill(
            "skill2", 
            "Python Programming",
            "Scripting and data analysis in Python",
            ProficiencyLevel.AWARENESS,
            LocalDateTime.now(),
            List.of("task3")
        );
    }
    
    @Test
    void testSaveAndFindById() {
        Skill savedSkill = skillRepository.save(testSkill1);
        
        Optional<Skill> foundSkill = skillRepository.findById(savedSkill.id());
        
        assertTrue(foundSkill.isPresent());
        assertEquals(testSkill1.name(), foundSkill.get().name());
        assertEquals(testSkill1.level(), foundSkill.get().level());
    }
    
    @Test
    void testFindByName() {
        skillRepository.save(testSkill1);
        
        List<Skill> skills = skillRepository.findByName("Java Programming");
        
        assertFalse(skills.isEmpty());
        assertEquals(1, skills.size());
        assertEquals(testSkill1.name(), skills.get(0).name());
    }
    
    @Test
    void testFindByLevel() {
        skillRepository.save(testSkill1);
        skillRepository.save(testSkill2);
        
        List<Skill> expertSkills = skillRepository.findByLevel(ProficiencyLevel.EXPERT);
        List<Skill> awarenessSkills = skillRepository.findByLevel(ProficiencyLevel.AWARENESS);
        
        assertEquals(1, expertSkills.size());
        assertEquals(1, awarenessSkills.size());
        assertEquals("Java Programming", expertSkills.get(0).name());
        assertEquals("Python Programming", awarenessSkills.get(0).name());
    }
    
    @Test
    void testFindByLevelGreaterThanEqual() {
        skillRepository.save(testSkill1);
        skillRepository.save(testSkill2);
        List<Skill> advancedSkills = skillRepository.findByLevelGreaterThanEqual(ProficiencyLevel.AWARENESS);
        
        // Should include both since AWARENESS <= both AWARENESS and EXPERT
        assertEquals(2, advancedSkills.size());
    }
    
    @Test
    void testDeleteById() {
        Skill savedSkill = skillRepository.save(testSkill1);
        
        skillRepository.deleteById(savedSkill.id());
        
        Optional<Skill> foundSkill = skillRepository.findById(savedSkill.id());
        assertFalse(foundSkill.isPresent());
    }
}