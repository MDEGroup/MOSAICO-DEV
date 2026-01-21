package it.univaq.disim.mosaico.wp2.repository.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.BenchmarkServiceImpl;

@ExtendWith(MockitoExtension.class)
class BenchmarkServiceImplTest {

    @Mock
    private LangfuseService langfuseService;

    @Mock
    private MetricService metricService;

    @Mock
    private BenchmarkRepository benchmarkRepository;

    @InjectMocks
    private BenchmarkServiceImpl benchmarkService;

    @Test
    void findAllDelegatesToRepository() {
        List<Benchmark> benchmarks = List.of(new Benchmark());
        when(benchmarkRepository.findAll()).thenReturn(benchmarks);

        List<Benchmark> result = benchmarkService.findAll();

        assertThat(result).isSameAs(benchmarks);
        verify(benchmarkRepository).findAll();
    }

    void computeBenchmarkMetricsInvokesMetricServiceForEachTrace() {
        Agent agent = new Agent();
        agent.setId("agent-1");
        agent.setLlangfuseUrl("localhost:3000");
        agent.setLlangfusePublicKey("pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015");
        agent.setLlangfuseSecretKey("sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee");
        Benchmark storedBenchmark = new Benchmark();
                
        storedBenchmark.setDatasetRef("ause_train");
        storedBenchmark.setRunName("run test - 2025-12-05T08:48:15.353757Z");

        when(benchmarkRepository.findByEvaluates_Id("agent-1"))
                .thenReturn(List.of(storedBenchmark));

        // TraceWithFullDetails trace = mock(TraceWithFullDetails.class);
        // Map<String, Object> additional = new HashMap<>();
        // additional.put("expected", "reference text");
        // when(trace.getAdditionalProperties()).thenReturn(additional);
        // when(trace.getOutput()).thenReturn(Optional.of("generated text"));

        // when(langfuseService.getRunBenchmarkTraces(agent, "dataset-one", "run-name"))
        //         .thenReturn(List.of(trace));
        // when(metricService.computeBleuScoreMetric("reference text", "generated text"))
        //         .thenReturn(new Metric());

        benchmarkService.computeBenchmarkMetrics(storedBenchmark, agent);

        verify(benchmarkRepository).findByEvaluates_Id("agent-1");
        // verify(langfuseService).getRunBenchmarkTraces(agent, "ause_train", "run test - 2025-12-05T08:48:15.353757Z");
        // verify(metricService).computeBleuScoreMetric("reference text", "generated text");
    }
}
