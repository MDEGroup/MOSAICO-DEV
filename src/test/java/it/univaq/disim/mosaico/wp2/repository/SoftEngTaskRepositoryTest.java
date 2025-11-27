package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.SoftEngTask;
import it.univaq.disim.mosaico.wp2.repository.data.enums.SwebokKAId;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ArtifactType;
import it.univaq.disim.mosaico.wp2.repository.repository.SoftEngTaskRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SoftEngTaskRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
public class SoftEngTaskRepositoryTest {

    @Autowired
    private SoftEngTaskRepository softEngTaskRepository;
    
    private SoftEngTask testTask1;
    private SoftEngTask testTask2;
    
    @BeforeEach
    void setUp() {
        softEngTaskRepository.deleteAll();
        
        testTask1 = new SoftEngTask(
            "task1",
            "Code Review",
            "Review Java code for quality and standards",
            SwebokKAId.TESTING,
            List.of(SwebokKAId.QUALITY),
            List.of(ArtifactType.CODE),
            List.of(ArtifactType.DOCUMENTATION),
            List.of("English", "Italian"),
            List.of("Java", "Python")
        );
        
        testTask2 = new SoftEngTask(
            "task2",
            "Unit Testing", 
            "Write comprehensive unit tests",
            SwebokKAId.TESTING,
            List.of(SwebokKAId.QUALITY, SwebokKAId.DESIGN),
            List.of(ArtifactType.CODE),
            List.of(ArtifactType.TEST),
            List.of("English"),
            List.of("Java", "JUnit")
        );
    }
    
    @Test
    void testSaveAndFindById() {
        SoftEngTask savedTask = softEngTaskRepository.save(testTask1);
        
        Optional<SoftEngTask> foundTask = softEngTaskRepository.findById(savedTask.id());
        
        assertTrue(foundTask.isPresent());
        assertEquals(testTask1.name(), foundTask.get().name());
        assertEquals(testTask1.primaryKA(), foundTask.get().primaryKA());
    }
    
    @Test
    void testFindByName() {
        softEngTaskRepository.save(testTask1);
        
        List<SoftEngTask> tasks = softEngTaskRepository.findByName("Code Review");
        
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals(testTask1.name(), tasks.get(0).name());
    }
    
    @Test
    void testFindByPrimaryKA() {
        softEngTaskRepository.save(testTask1);
        softEngTaskRepository.save(testTask2);
        
        List<SoftEngTask> testingTasks = softEngTaskRepository.findByPrimaryKA(SwebokKAId.TESTING);
        
        assertEquals(2, testingTasks.size());
    }
    
    @Test
    void testFindBySecondaryKAsContaining() {
        softEngTaskRepository.save(testTask1);
        softEngTaskRepository.save(testTask2);
        
        List<SoftEngTask> qualityTasks = softEngTaskRepository.findBySecondaryKAsContaining(SwebokKAId.QUALITY);
        List<SoftEngTask> designTasks = softEngTaskRepository.findBySecondaryKAsContaining(SwebokKAId.DESIGN);
        
        assertEquals(2, qualityTasks.size());
        assertEquals(1, designTasks.size());
        assertEquals("Unit Testing", designTasks.get(0).name());
    }
    
    @Test
    void testDeleteById() {
        SoftEngTask savedTask = softEngTaskRepository.save(testTask1);
        
        softEngTaskRepository.deleteById(savedTask.id());
        
        Optional<SoftEngTask> foundTask = softEngTaskRepository.findById(savedTask.id());
        assertFalse(foundTask.isPresent());
    }
}