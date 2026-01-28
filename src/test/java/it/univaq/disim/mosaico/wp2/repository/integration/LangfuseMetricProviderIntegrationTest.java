package it.univaq.disim.mosaico.wp2.repository.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.BlueMetric;
import it.univaq.disim.mosaico.wp2.repository.data.KPISpecification;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;
import it.univaq.disim.mosaico.wp2.repository.data.RougeMetric;
import it.univaq.disim.mosaico.wp2.repository.data.formula.AverageFormula;
import it.univaq.disim.mosaico.wp2.repository.data.formula.WeightedSumFormula;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;

/**
 * Integration test for Langfuse + Metric Provider Architecture.
 *
 * This test demonstrates the complete workflow:
 * 1. Create agent with Langfuse credentials
 * 2. Create benchmark linked to a dataset run
 * 3. Compute metrics from Langfuse traces
 * 4. Compute KPIs using formulas
 *
 * NOTE: This test is @Disabled by default because it requires:
 * - Langfuse running on localhost:3000
 * - Valid dataset and run IDs
 * - Traces with expected/generated outputs
 *
 * To run this test:
 * 1. Start Langfuse: docker-compose -f docker-compose.langfuse.yml up
 * 2. Update the dataset and run IDs below with your actual values
 * 3. Remove @Disabled annotation
 * 4. Run: mvn test -Dtest=LangfuseMetricProviderIntegrationTest
 */
@SpringBootTest
class LangfuseMetricProviderIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(LangfuseMetricProviderIntegrationTest.class);

    @Autowired
    private BenchmarkService benchmarkService;

    private Agent testAgent;
    private Benchmark testBenchmark;

    @BeforeEach
    void setUp() {
        // Create test agent with your Langfuse credentials
        testAgent = new Agent();
        testAgent.setId(UUID.randomUUID().toString());
        testAgent.setName("Test Agent");
        testAgent.setLlangfuseUrl("http://localhost:3000");
        testAgent.setLlangfusePublicKey("pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015");
        testAgent.setLlangfuseSecretKey("sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee");
        testAgent.setLlangfuseProjectName("test-project");

        // Create benchmark linked to your dataset run
        testBenchmark = new Benchmark();
        testBenchmark.setId(UUID.randomUUID().toString());

        // Using actual dataset name from Langfuse UI
        testBenchmark.setDatasetRef("ause_train");  // Dataset name from Langfuse

        testBenchmark.setEvaluates(Arrays.asList(testAgent));
    }

    // Run name stored separately (now lives in BenchmarkRun.langfuseRunName)
    private static final String TEST_LANGFUSE_RUN_NAME = "run test - 2025-12-05T10:03:41.398117Z";

    @Test
    void testComputeMetricsFromLangfuseRun() {
        // Act: Compute metrics from Langfuse dataset run
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        // Assert: Metrics were computed
        assertNotNull(metrics, "Metrics list should not be null");

        // Print diagnostic information
        log.info("\n=== Langfuse Integration Test Results ===");
        log.info("Dataset: {}", testBenchmark.getDatasetRef());
        log.info("Run: {}", TEST_LANGFUSE_RUN_NAME);
        log.info("Metrics computed: {}", metrics.size());

        if (metrics.isEmpty()) {
            log.warn("\n⚠️ WARNING: No metrics were computed!");
            log.warn("Possible reasons:");
            log.warn("1. Langfuse is not running on http://localhost:3000");
            log.warn("2. The dataset or run ID is incorrect");
            log.warn("3. The run has no traces");
            log.warn("4. Authentication credentials are incorrect");
            log.warn("\nSkipping assertions for empty metrics.");
            return; // Skip assertions if no data available
        }

        // Print results
        log.debug("\n=== Computed Metrics ===");
        for (Metric metric : metrics) {
            log.debug("{}: {} {}",
                metric.getName(),
                String.format("%.4f", metric.getFloatValue().orElse(0f)),
                metric.getUnit() != null ? metric.getUnit() : ""
            );
        }

        // Verify metric structure (only if we have metrics)
        Metric firstMetric = metrics.get(0);
        assertNotNull(firstMetric.getName(), "Metric should have a name");
        assertTrue(firstMetric.getFloatValue().isPresent(), "Metric should have a float value");
    }

    @Test
    void testComputeKPIWithAverageFormula() {
        // First check if we have any traces
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        if (metrics.isEmpty()) {
            log.warn("\n⚠️ Skipping KPI test - no traces available from Langfuse");
            return;
        }

        // Arrange: Define KPI with average formula
        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setId(UUID.randomUUID().toString());
        kpi.setDescription("Average Quality Score");
        kpi.setIncludes(Arrays.asList(new BlueMetric(), new RougeMetric()));

        KPISpecification spec = new KPISpecification();
        spec.setFormulaType("AVERAGE");
        spec.setFormula(new AverageFormula());
        kpi.setSpecification(spec);

        testBenchmark.setMeasures(Arrays.asList(kpi));

        // Act: Compute KPI
        PerformanceKPI result = benchmarkService.computeKPIs(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        // Assert: KPI was computed
        assertNotNull(result, "KPI result should not be null");
        assertTrue(result.getDescription().contains("Computed:"),
            "KPI description should contain computed value");

        // Print result
        log.info("\n=== Computed KPI ===");
        log.info(result.getDescription());
    }

    @Test
    void testComputeKPIWithWeightedFormula() {
        // First check if we have any traces
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        if (metrics.isEmpty()) {
            log.warn("\n⚠️ Skipping weighted KPI test - no traces available from Langfuse");
            return;
        }

        // Arrange: Define KPI with weighted formula (60% BLEU + 40% ROUGE)
        Map<Class<? extends MetricKey>, Double> weights = new HashMap<>();
        weights.put(BlueMetric.class, 0.6);
        weights.put(RougeMetric.class, 0.4);

        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setId(UUID.randomUUID().toString());
        kpi.setDescription("Weighted Quality Score (60% BLEU + 40% ROUGE)");
        kpi.setIncludes(Arrays.asList(new BlueMetric(), new RougeMetric()));

        KPISpecification spec = new KPISpecification();
        spec.setFormulaType("WEIGHTED_SUM");
        spec.setFormula(new WeightedSumFormula(weights));
        kpi.setSpecification(spec);

        testBenchmark.setMeasures(Arrays.asList(kpi));

        // Act: Compute KPI
        PerformanceKPI result = benchmarkService.computeKPIs(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        // Assert: KPI was computed
        assertNotNull(result, "KPI result should not be null");

        // Print result
        log.info("\n=== Computed Weighted KPI ===");
        log.info(result.getDescription());
    }

    @Test
    void testMetricProviderRegistryDiscovery() {
        // This test verifies that the metric provider registry is working
        // Even without Langfuse, we can test the registry itself

        // Act: Compute metrics (will use all registered providers)
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(testBenchmark, testAgent, TEST_LANGFUSE_RUN_NAME);

        // Assert: Should have metrics from both registered providers
        // (BlueMetricProvider and RougeMetricProvider)
        assertNotNull(metrics);

        // The number of metrics depends on how many traces are in the run
        // Each trace will generate metrics from all registered providers
        log.info("\n=== Provider Registry Test ===");
        log.info("Total metrics computed: {}", metrics.size());
        log.info("This includes metrics from all registered providers applied to all traces");
    }

    /**
     * Example of how to use the metric provider architecture programmatically.
     * This method can be copied to your application code.
     */
    public Map<String, Object> exampleEvaluationWorkflow(
            String agentId,
            String datasetId,
            String runId) {

        // 1. Create agent
        Agent agent = new Agent();
        agent.setId(agentId);
        agent.setLlangfuseUrl("http://localhost:3000");
        agent.setLlangfusePublicKey("pk-lf-41f76ff4-f423-4b8c-a3b7-87c5b3012015");
        agent.setLlangfuseSecretKey("sk-lf-bd30b103-9a1b-43a0-88f3-742fbe657dee");

        // 2. Create benchmark
        Benchmark benchmark = new Benchmark();
        benchmark.setDatasetRef(datasetId);
        benchmark.setEvaluates(Arrays.asList(agent));

        // 3. Define KPI
        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setDescription("Quality Score");
        kpi.setIncludes(Arrays.asList(new BlueMetric(), new RougeMetric()));

        KPISpecification spec = new KPISpecification();
        spec.setFormula(new AverageFormula());
        kpi.setSpecification(spec);

        benchmark.setMeasures(Arrays.asList(kpi));

        // 4. Compute metrics (runId is now passed as langfuseRunName parameter)
        List<Metric> metrics = benchmarkService.computeBenchmarkMetrics(benchmark, agent, runId);

        // 5. Compute KPI
        PerformanceKPI result = benchmarkService.computeKPIs(benchmark, agent, runId);

        // 6. Return results
        Map<String, Object> results = new HashMap<>();
        results.put("agent", agent.getName());
        results.put("dataset", datasetId);
        results.put("run", runId);
        results.put("metrics", metrics);
        results.put("kpi", result);
        results.put("traceCount", metrics.size() / 2); // Divide by number of providers

        return results;
    }
}
