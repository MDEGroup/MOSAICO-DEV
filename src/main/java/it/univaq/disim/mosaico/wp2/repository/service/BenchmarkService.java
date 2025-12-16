package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;

/**
 * Service interface for Benchmark operations.
 */
public interface BenchmarkService {
    List<Benchmark> findAll();
    Optional<Benchmark> findById(String id);
    Benchmark save(Benchmark benchmark);
    void deleteById(String id);
    Benchmark findByDatasetRef(String datasetRef);
    Benchmark findByProtocolVersion(String protocolVersion);
    List<Benchmark> findByEvaluates_Id(String agentId);
    List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent);
    void computeKPIs(Benchmark benchmark, Agent agent);
    List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent, List<MetricProvider> metricProviders);
}