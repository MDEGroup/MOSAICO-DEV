package it.univaq.disim.mosaico.wp2.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BenchmarkRepository.
 */
@DataMongoTest
@ActiveProfiles("test")
public class BenchmarkRepositoryTest {

    @Autowired
    private BenchmarkRepository benchmarkRepository;
    
    private Benchmark testBenchmark1;
    private Benchmark testBenchmark2;
    
    @BeforeEach
    void setUp() {
        benchmarkRepository.deleteAll();
        
        testBenchmark1 = createBenchmark(
            "Metadata for code review benchmark",
            "Features for evaluating code quality analysis",
            "dataset-codeql-001",
            "Evaluate agent performance on code review tasks",
            "v1.0"
        );
        
        testBenchmark2 = createBenchmark(
            "Metadata for testing benchmark", 
            "Features for unit test generation evaluation",
            "dataset-junit-002",
            "Evaluate agent performance on test generation",
            "v2.0"
        );
    }

    private Benchmark createBenchmark(String metadata, String features, String datasetRef, String taskDef, String protocolVersion) {
        Benchmark benchmark = new Benchmark();
        benchmark.setMetadata(metadata);
        benchmark.setFeatures(features);
        benchmark.setDatasetRef(datasetRef);
        benchmark.setTaskDef(taskDef);
        benchmark.setProtocolVersion(protocolVersion);
        return benchmark;
    }
    
    @Test
    void testSaveAndFindById() {
        Benchmark savedBenchmark = benchmarkRepository.save(testBenchmark1);
        
        Optional<Benchmark> foundBenchmark = benchmarkRepository.findById(savedBenchmark.getId());
        
        assertTrue(foundBenchmark.isPresent());
        assertEquals(testBenchmark1.getMetadata(), foundBenchmark.get().getMetadata());
        assertEquals(testBenchmark1.getDatasetRef(), foundBenchmark.get().getDatasetRef());
    }
    
    @Test
    void testFindByDatasetRef() {
        benchmarkRepository.save(testBenchmark1);
        
        Benchmark foundBenchmark = benchmarkRepository.findByDatasetRef("dataset-codeql-001");
        
        assertNotNull(foundBenchmark);
        assertEquals(testBenchmark1.getMetadata(), foundBenchmark.getMetadata());
        assertEquals("Evaluate agent performance on code review tasks", foundBenchmark.getTaskDef());
    }
    
    @Test
    void testFindByProtocolVersion() {
        benchmarkRepository.save(testBenchmark1);
        benchmarkRepository.save(testBenchmark2);
        
        Benchmark v1Benchmark = benchmarkRepository.findByProtocolVersion("v1.0");
        Benchmark v2Benchmark = benchmarkRepository.findByProtocolVersion("v2.0");
        
        assertNotNull(v1Benchmark);
        assertNotNull(v2Benchmark);
        assertEquals("dataset-codeql-001", v1Benchmark.getDatasetRef());
        assertEquals("dataset-junit-002", v2Benchmark.getDatasetRef());
    }
    
    @Test
    void testFindByDatasetRefNotFound() {
        benchmarkRepository.save(testBenchmark1);
        
        Benchmark foundBenchmark = benchmarkRepository.findByDatasetRef("non-existent-dataset");
        
        assertNull(foundBenchmark);
    }
    
    @Test
    void testDeleteById() {
        Benchmark savedBenchmark = benchmarkRepository.save(testBenchmark1);
        
        benchmarkRepository.deleteById(savedBenchmark.getId());
        
        Optional<Benchmark> foundBenchmark = benchmarkRepository.findById(savedBenchmark.getId());
        assertFalse(foundBenchmark.isPresent());
    }
    
    @Test
    void testFindAll() {
        benchmarkRepository.save(testBenchmark1);
        benchmarkRepository.save(testBenchmark2);
        
        List<Benchmark> allBenchmarks = benchmarkRepository.findAll();
        
        assertEquals(2, allBenchmarks.size());
        assertTrue(allBenchmarks.stream().anyMatch(b -> b.getDatasetRef().equals("dataset-codeql-001")));
        assertTrue(allBenchmarks.stream().anyMatch(b -> b.getDatasetRef().equals("dataset-junit-002")));
    }
}