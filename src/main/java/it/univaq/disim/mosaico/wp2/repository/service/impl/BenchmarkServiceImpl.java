package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.service.MetricService;

/**
 * Implementation of BenchmarkService.
 */
@Service
public class BenchmarkServiceImpl implements BenchmarkService {
    @Autowired
    private LangfuseService langfuseService;
    @Autowired
    private final MetricProviderRegistry registry;

    @Autowired
    private MetricService metricService;
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

    @Override
    public List<Benchmark> findByEvaluates_Id(String agentId) {
        return benchmarkRepository.findByEvaluates_Id(agentId);
    }



    @Override
    public List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent, List<MetricProvider> metricProviders) {
        List<Benchmark> benchmarks = findByEvaluates_Id(agent.getId());
        List<Metric> metrics = new ArrayList<>();
        for (Benchmark bm : benchmarks) {
            List<TraceWithFullDetails> traces = langfuseService.getRunBenchmarkTraces(agent, bm.getDatasetRef(),
                    bm.getRunName());

            for (TraceWithFullDetails trace : traces) {
                for (MetricProvider provider : metricProviders) {
                    metrics.add(provider.compute(agent,
                            trace.getAdditionalProperties().get("expected").toString(),
                            trace.getOutput().orElse("").toString(),
                            trace));
                }
            }
        }
        return metrics;
    }

    @Override
    public void computeKPIs(Benchmark benchmark, Agent agent) {
        for (PerformanceKPI measure : benchmark.getMeasures()) {
            
        }
    }

    @Override
    public List<Metric> computeBenchmarkMetrics(Benchmark benchmark, Agent agent) {
                Map<Class<? extends MetricKey>, Double> values = new HashMap<>();

        for (var key : benchmark.getMeasures()) {
            double v = registry.providerFor(key).compute(agent).value();
            values.put(key, v);
        }

        return kpi.formula().eval(values);
    }
}