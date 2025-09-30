package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.repository.SkillRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.SkillServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for SkillService.
 */
@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    
    @InjectMocks
    private SkillServiceImpl skillService;
    
    private Skill testSkill1;
    private Skill testSkill2;
    
    @BeforeEach
    void setUp() {
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
            ProficiencyLevel.WORKING,
            LocalDateTime.now(),
            List.of("task3")
        );
    }
    
    @Test
    void testSaveSkill() {
        when(skillRepository.save(testSkill1)).thenReturn(testSkill1);
        
        Skill savedSkill = skillService.save(testSkill1);
        
        assertNotNull(savedSkill);
        assertEquals(testSkill1.name(), savedSkill.name());
        verify(skillRepository, times(1)).save(testSkill1);
    }
    
    @Test
    void testFindById() {
        when(skillRepository.findById("skill1")).thenReturn(Optional.of(testSkill1));
        
        Optional<Skill> foundSkill = skillService.findById("skill1");
        
        assertTrue(foundSkill.isPresent());
        assertEquals(testSkill1.name(), foundSkill.get().name());
        verify(skillRepository, times(1)).findById("skill1");
    }
    
    @Test
    void testFindByIdNotFound() {
        when(skillRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        Optional<Skill> foundSkill = skillService.findById("nonexistent");
        
        assertFalse(foundSkill.isPresent());
        verify(skillRepository, times(1)).findById("nonexistent");
    }
    
    @Test
    void testFindAll() {
        List<Skill> skills = List.of(testSkill1, testSkill2);
        when(skillRepository.findAll()).thenReturn(skills);
        
        List<Skill> foundSkills = skillService.findAll();
        
        assertEquals(2, foundSkills.size());
        assertEquals(testSkill1.name(), foundSkills.get(0).name());
        assertEquals(testSkill2.name(), foundSkills.get(1).name());
        verify(skillRepository, times(1)).findAll();
    }
    
    @Test
    void testFindByName() {
        when(skillRepository.findByName("Java Programming")).thenReturn(List.of(testSkill1));
        
        List<Skill> javaSkills = skillService.findByName("Java Programming");
        
        assertEquals(1, javaSkills.size());
        assertEquals(testSkill1.name(), javaSkills.get(0).name());
        verify(skillRepository, times(1)).findByName("Java Programming");
    }
    
    @Test
    void testFindByLevel() {
        when(skillRepository.findByLevel(ProficiencyLevel.EXPERT)).thenReturn(List.of(testSkill1));
        
        List<Skill> expertSkills = skillService.findByLevel(ProficiencyLevel.EXPERT);
        
        assertEquals(1, expertSkills.size());
        assertEquals(ProficiencyLevel.EXPERT, expertSkills.get(0).level());
        verify(skillRepository, times(1)).findByLevel(ProficiencyLevel.EXPERT);
    }
    
    @Test
    void testFindByMinLevel() {
        when(skillRepository.findByLevelGreaterThanEqual(ProficiencyLevel.WORKING))
            .thenReturn(List.of(testSkill1, testSkill2));
        
        List<Skill> advancedSkills = skillService.findByMinLevel(ProficiencyLevel.WORKING);
        
        assertEquals(2, advancedSkills.size());
        verify(skillRepository, times(1)).findByLevelGreaterThanEqual(ProficiencyLevel.WORKING);
    }
    
    @Test
    void testDeleteById() {
        doNothing().when(skillRepository).deleteById("skill1");
        
        skillService.deleteById("skill1");
        
        verify(skillRepository, times(1)).deleteById("skill1");
    }
    
    @Test
    void testUpdateSkill() {
        Skill updatedSkill = new Skill(
            "skill1",
            "Advanced Java Programming",
            "Advanced object-oriented programming concepts",
            ProficiencyLevel.EXPERT,
            LocalDateTime.now(),
            List.of("task1", "task2", "task4")
        );
        
        when(skillRepository.save(updatedSkill)).thenReturn(updatedSkill);
        
        Skill result = skillService.save(updatedSkill);
        
        assertEquals("Advanced Java Programming", result.name());
        assertEquals("Advanced object-oriented programming concepts", result.description());
        verify(skillRepository, times(1)).save(updatedSkill);
    }
    
    @Test
    void testFindByNameEmptyResult() {
        when(skillRepository.findByName("NonExistent")).thenReturn(List.of());
        
        List<Skill> skills = skillService.findByName("NonExistent");
        
        assertTrue(skills.isEmpty());
        verify(skillRepository, times(1)).findByName("NonExistent");
    }
}