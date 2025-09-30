package it.univaq.disim.mosaico.wp2.repository.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;

/**
 * Controller for Benchmark operations following MOSAICO taxonomy.
 * 
 * Copyright 2025 Mosaico
 */
@RestController
@RequestMapping("/api/benchmarks")
public class BenchmarkController {

    Logger logger = LoggerFactory.getLogger(BenchmarkController.class);
    
    @Autowired
    private final BenchmarkService benchmarkService;
    
    public BenchmarkController(@Autowired BenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }
    
    /**
     * Get all benchmarks.
     */
    @GetMapping
    public ResponseEntity<List<Benchmark>> getAllBenchmarks() {
        logger.info("GET /api/benchmarks");
        List<Benchmark> benchmarks = benchmarkService.findAll();
        return ResponseEntity.ok(benchmarks);
    }
    
    /**
     * Get benchmark by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Benchmark> getBenchmarkById(@PathVariable String id) {
        logger.info("GET /api/benchmarks/{}", id);
        Optional<Benchmark> benchmark = benchmarkService.findById(id);
        return benchmark.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create new benchmark.
     */
    @PostMapping
    public ResponseEntity<Benchmark> createBenchmark(@RequestBody Benchmark benchmark) {
        logger.info("POST /api/benchmarks for benchmark with datasetRef: {}", benchmark.datasetRef());
        Benchmark savedBenchmark = benchmarkService.save(benchmark);
        return ResponseEntity.ok(savedBenchmark);
    }
    
    /**
     * Update existing benchmark.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Benchmark> updateBenchmark(@PathVariable String id, @RequestBody Benchmark benchmark) {
        logger.info("PUT /api/benchmarks/{}", id);
        Optional<Benchmark> existingBenchmark = benchmarkService.findById(id);
        if (existingBenchmark.isPresent()) {
            Benchmark updatedBenchmark = benchmarkService.save(benchmark);
            return ResponseEntity.ok(updatedBenchmark);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Delete benchmark.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBenchmark(@PathVariable String id) {
        logger.info("DELETE /api/benchmarks/{}", id);
        benchmarkService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Find benchmark by dataset reference.
     */
    @GetMapping("/search/dataset-ref")
    public ResponseEntity<Benchmark> findBenchmarkByDatasetRef(@RequestParam String datasetRef) {
        logger.info("GET /api/benchmarks/search/dataset-ref?datasetRef={}", datasetRef);
        Benchmark benchmark = benchmarkService.findByDatasetRef(datasetRef);
        return benchmark != null ? ResponseEntity.ok(benchmark) : ResponseEntity.notFound().build();
    }
    
    /**
     * Find benchmark by protocol version.
     */
    @GetMapping("/search/protocol-version")
    public ResponseEntity<Benchmark> findBenchmarkByProtocolVersion(@RequestParam String protocolVersion) {
        logger.info("GET /api/benchmarks/search/protocol-version?protocolVersion={}", protocolVersion);
        Benchmark benchmark = benchmarkService.findByProtocolVersion(protocolVersion);
        return benchmark != null ? ResponseEntity.ok(benchmark) : ResponseEntity.notFound().build();
    }
}