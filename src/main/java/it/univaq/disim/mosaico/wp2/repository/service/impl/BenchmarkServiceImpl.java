package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;

/**
 * Implementation of BenchmarkService.
 */
@Service
public class BenchmarkServiceImpl implements BenchmarkService {
    
    @Autowired
    private BenchmarkRepository benchmarkRepository;
    
    @Override
    public List<Benchmark> findAll() {
        return benchmarkRepository.findAll();
    }
    
    @Override
    public Optional<Benchmark> findById(String id) {
        return benchmarkRepository.findById(id);
    }
    
    @Override
    public Benchmark save(Benchmark benchmark) {
        return benchmarkRepository.save(benchmark);
    }
    
    @Override
    public void deleteById(String id) {
        benchmarkRepository.deleteById(id);
    }
    
    @Override
    public Benchmark findByDatasetRef(String datasetRef) {
        return benchmarkRepository.findByDatasetRef(datasetRef);
    }
    
    @Override
    public Benchmark findByProtocolVersion(String protocolVersion) {
        return benchmarkRepository.findByProtocolVersion(protocolVersion);
    }
}