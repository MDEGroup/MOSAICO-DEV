package it.univaq.disim.mosaico.wp2.repository.demo;


import it.univaq.disim.mosaico.wp2.repository.data.*;
import it.univaq.disim.mosaico.wp2.repository.data.enums.*;
import it.univaq.disim.mosaico.wp2.repository.dsl.DslParseResult;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaParser;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.impl.BenchmarkRunService;
import it.univaq.disim.mosaico.wp2.repository.service.impl.TraceMetricsAggregator;
import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Demo that uses real data from Langfuse to run benchmarks on 3 summarization agents
 * (GitHub description generation from README files).
 *
 * Evaluated agents:
 * 1. sum_agent (baseline) - llama3.2:3b with temperature=0
 * 2. variant1 - llama3.1:8b with temperature=0.8 (creative)
 * 3. variant2 - mistral:7b with temperature=0.2 (deterministic)
 *
 * The complete flow includes:
 * - Agent and Benchmark entity creation
 * - Trace retrieval from Langfuse
 * - Metrics calculation (ROUGE, BLEU, cosine similarity)
 * - KPI evaluation via DSL
 * - Performance comparison between agents
 * - Complete benchmark run execution
 *
 * NOTE: To run this demo, Langfuse must be reachable
 * with the credentials specified in application.properties
 *
 * Execution:
 *   mvn spring-boot:run -Dspring-boot.run.profiles=demo-benchmark
 * or:
 *   java -jar target/repository.jar --spring.profiles.active=demo-benchmark
 */
@SpringBootApplication
@ComponentScan(basePackages = "it.univaq.disim.mosaico.wp2.repository")
public class RealLangfuseBenchmarkingDemo {

    public static void main(String[] args) {
        SpringApplication.run(RealLangfuseBenchmarkingDemo.class, args);
    }
}

/**
 * Runner component that executes the demo when the "demo-benchmark" profile is active.
 */
@Component
@Profile("demo-benchmark")
class BenchmarkDemoRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkDemoRunner.class);

    private static final String SEPARATOR = "═".repeat(80);
    private static final String MINI_SEP = "─".repeat(60);
    private static final String SECTION_SEP = "━".repeat(80);

    // Dataset configuration
    private static final String DATASET_NAME = "ause_train";

    // Agent variant run names
    private static final String BASELINE_RUN = "experiment_00 - 2026-01-22T10:44:46.144492Z";
    private static final String VARIANT1_RUN = "experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z";
    private static final String VARIANT2_RUN = "experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z";

    // Injected services
    private final LangfuseService langfuseService;
    private final LangfuseProperties langfuseProperties;
    private final KPIFormulaParser kpiParser;
    private final TraceMetricsAggregator traceMetricsAggregator;
    private final BenchmarkRunService benchmarkRunService;

    

    // Results storage (in-memory, no database persistence)
    private final Map<String, AgentBenchmarkResult> agentResults = new LinkedHashMap<>();

    // Benchmark reference
    private Benchmark benchmark;

    public BenchmarkDemoRunner(
            LangfuseService langfuseService,
            LangfuseProperties langfuseProperties,
            KPIFormulaParser kpiParser,
            TraceMetricsAggregator traceMetricsAggregator,
            BenchmarkRunService benchmarkRunService) {
        this.langfuseService = langfuseService;
        this.langfuseProperties = langfuseProperties;
        this.kpiParser = kpiParser;
        this.traceMetricsAggregator = traceMetricsAggregator;
        this.benchmarkRunService = benchmarkRunService;
    }

    @Override
    public void run(String... args) {
        runFullDemo();
    }

    public void runFullDemo() {
        printHeader("MOSAICO BENCHMARKING SYSTEM - DEMO WITH REAL LANGFUSE DATA");
        print("Execution date: " + Instant.now());
        print("Dataset: " + DATASET_NAME);
        print("");

        

        // Register custom metrics for the DSL parser
        kpiParser.registerMetricKeys(Set.of("ROUGE1_F", "ROUGEL_F", "COSINE_PRED_GOLD", "COSINE_PRED_SOURCE", "LEN_RATIO"));

        // Step 1: Create Agent entities
        step1_CreateAgentEntities();

        // Step 2: Create Benchmark definition
        step2_CreateBenchmarkDefinition();

        // Step 3: Fetch real data from Langfuse
        step3_FetchLangfuseData();

        // Step 4: Compute metrics for each agent
        step4_ComputeMetrics();

        // Step 5: Calculate KPIs using DSL
        step5_CalculateKPIs();

        // Step 6: Execute full benchmark run flow
        step6_ExecuteBenchmarkRun();

        // Step 7: Compare agents and generate report
        step7_CompareAgentsAndReport();

        printHeader("DEMO COMPLETED");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 1: CREATE AGENT ENTITIES
    // ═══════════════════════════════════════════════════════════════════════════

    private void step1_CreateAgentEntities() {
        printStep(1, "AGENT ENTITY CREATION");

        print("\nCreating 3 Agent instances representing the 3 summarization agents:");
        print("");

        // Agent 1: Baseline (sum_agent.py)
        Agent baselineAgent = createAgent(
            "agent-baseline",
            "SummarizationBot Baseline",
            "Baseline agent for GitHub description generation. Uses llama3.2:3b with temperature=0 for deterministic output.",
            "llama3.2:3b",
            Map.of("temperature", 0.0, "top_p", 1.0, "num_predict", 512)
        );
        agentResults.put(baselineAgent.getId(), new AgentBenchmarkResult(baselineAgent));

        // Agent 2: Variant 1 - Creative (sum_agent_variant1.py)
        Agent creativeAgent = createAgent(
            "agent-variant1-creative",
            "SummarizationBot Creative",
            "Creative variant with llama3.1:8b and temperature=0.8 for more varied and creative output.",
            "llama3.1:8b",
            Map.of("temperature", 0.8, "top_p", 0.95, "top_k", 50, "num_predict", 512)
        );
        agentResults.put(creativeAgent.getId(), new AgentBenchmarkResult(creativeAgent));

        // Agent 3: Variant 2 - Deterministic (sum_agent_variant2.py)
        Agent deterministicAgent = createAgent(
            "agent-variant2-deterministic",
            "SummarizationBot Deterministic",
            "Deterministic variant with mistral:7b and temperature=0.2 for precise and consistent output.",
            "mistral:7b",
            Map.of("temperature", 0.2, "top_p", 0.8, "top_k", 20, "num_predict", 256, "repeat_penalty", 1.1)
        );
        agentResults.put(deterministicAgent.getId(), new AgentBenchmarkResult(deterministicAgent));

        print("");
        print("Agents created: " + agentResults.size());
        agentResults.values().forEach(r -> {
            Agent a = r.agent;
            print("  - " + a.getName() + " [" + a.getId() + "]");
        });

        printEndStep();
    }

    private Agent createAgent(String id, String name, String description, String model, Map<String, Object> options) {
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName(name);
        agent.setDescription(description);
        agent.setVersion("1.0");
        agent.setLlangfuseUrl(langfuseProperties.getBaseUrl());
        agent.setLlangfusePublicKey(langfuseProperties.getPublicKey());
        agent.setLlangfuseSecretKey(langfuseProperties.getSecretKey());
        agent.setLlangfuseProjectName("mosaic-test-project");
        agent.setObjective("Generate concise and accurate descriptions for GitHub repositories based on README files");
        agent.setRole("Summarization Agent");
        agent.setDeployment(buildDeployment(model));
        agent.setSkills(buildSkills(id, model));
        agent.setExploits(buildTools(model));
        agent.setHas(buildMemories(id));
        agent.setSupports(buildProtocols());
        agent.setConsumptions(buildConsumptions(model, options));

        print("  Agent: " + name);
        print("    ├─ Model: " + model);
        print("    ├─ Options: " + options);
        print("    └─ Langfuse Project: " + agent.getLlangfuseProjectName());

        return agent;
    }

    private Deployment buildDeployment(String model) {
        String sanitizedModel = model.replace(":", "-").replace("/", "-");
        String image = "ghcr.io/mosaico/" + sanitizedModel + ":latest";
        return new Deployment(DeploymentMode.DOCKER, image);
    }

    private List<Skill> buildSkills(String agentId, String model) {
        LocalDateTime now = LocalDateTime.now();
        Skill summarization = new Skill(
            null,
            "Summarization",
            "Summarizes README into curated descriptions (" + model + ")",
            ProficiencyLevel.EXPERT,
            now,
            List.of("TASK_SUMMARY", agentId + "_README")
        );
        Skill evaluation = new Skill(
            null,
            "Self-Eval",
            "Evaluates coherence with ROUGE/BLEU metrics",
            ProficiencyLevel.ADVANCED,
            now,
            List.of("TASK_EVAL")
        );
        return List.of(summarization, evaluation);
    }

    private List<Tool> buildTools(String model) {
        Tool langfuseTool = new Tool(
            null,
            "Langfuse API",
            "Retrieves traces and metrics for the model " + model,
            "API_KEY",
            "traces.read,metrics.read",
            "120 rpm",
            "burst-200"
        );
        Tool githubTool = new Tool(
            null,
            "GitHub Ingestor",
            "Downloads README and repository metadata",
            "OAUTH_APP",
            "repo.read",
            "5 rps",
            "per-repo"
        );
        return List.of(langfuseTool, githubTool);
    }

    private List<Memory> buildMemories(String agentId) {
        Memory shortTerm = new Memory(
            null,
            MemoryType.SHORT_TERM,
            MemoryScope.AGENT,
            "redis://cache/" + agentId
        );
        Memory longTerm = new Memory(
            null,
            MemoryType.LONG_TERM,
            MemoryScope.SHARED,
            "postgres://langfuse/" + DATASET_NAME
        );
        return List.of(shortTerm, longTerm);
    }

    private List<InteractionProtocol> buildProtocols() {
        InteractionProtocol rest = new InteractionProtocol(
            null,
            "REST_CALLBACK",
            "1.0",
            langfuseProperties.getBaseUrl() + "/api/public/trace",
            "Receives trace completion notifications"
        );
        InteractionProtocol webhook = new InteractionProtocol(
            null,
            "LangfuseDatasetRun",
            "0.9",
            langfuseProperties.getBaseUrl() + "/api/public/datasets",
            "Publishes benchmark results"
        );
        return List.of(rest, webhook);
    }

    private List<AgentConsumption> buildConsumptions(String model, Map<String, Object> options) {
        List<InputParameter> params = options.entrySet().stream()
            .map(entry -> new InputParameter(entry.getKey(), String.valueOf(entry.getValue())))
            .collect(Collectors.toList());

        List<OutputStructure> outputs = List.of(
            new OutputStructure("summary", "markdown"),
            new OutputStructure("explanations", "json")
        );

        AgentConsumption consumption = new AgentConsumption(
            null,
            "model=" + model,
            params,
            outputs
        );

        return List.of(consumption);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 2: CREATE BENCHMARK DEFINITION
    // ═══════════════════════════════════════════════════════════════════════════

    private void step2_CreateBenchmarkDefinition() {
        printStep(2, "BENCHMARK DEFINITION CREATION");

        benchmark = new Benchmark();
        benchmark.setId("bench-summarization-001");
        benchmark.setMetadata("{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}");
        benchmark.setDatasetRef(DATASET_NAME);
        benchmark.setTaskDef("Generate a concise description (3-6 sentences) for a GitHub repository from the README file");
        benchmark.setFeatures("accuracy,fluency,conciseness,relevance");
        benchmark.setProtocolVersion("1.0");
        benchmark.setEvaluates(new ArrayList<>(agentResults.values().stream().map(r -> r.agent).toList()));

        print("\nBenchmark created:");
        print("  ├─ ID: " + benchmark.getId());
        print("  ├─ Dataset: " + benchmark.getDatasetRef());
        print("  ├─ Task: " + benchmark.getTaskDef());
        print("  ├─ Features: " + benchmark.getFeatures());
        print("  └─ Evaluated agents: " + benchmark.getEvaluates().size());

        print("\nDefined KPI Formulas:");
        print("  ├─ Overall Quality: WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)");
        print("  ├─ Text Similarity: AVERAGE(ROUGE, BLEU)");
        print("  ├─ Min Performance: MIN(ROUGE, BLEU, ACCURACY)");
        print("  └─ Quality Threshold: THRESHOLD(ROUGE, 0.3)");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 3: FETCH REAL DATA FROM LANGFUSE
    // ═══════════════════════════════════════════════════════════════════════════

    private void step3_FetchLangfuseData() {
        printStep(3, "FETCHING REAL DATA FROM LANGFUSE");

        print("\nConnecting to Langfuse: " + langfuseProperties.getBaseUrl());
        print("Dataset: " + DATASET_NAME);
        print("");

        // Map agent IDs to their experiment run names
        Map<String, String> agentRunMapping = Map.of(
            "agent-baseline", BASELINE_RUN,
            "agent-variant1-creative", VARIANT1_RUN,
            "agent-variant2-deterministic", VARIANT2_RUN
        );

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            String runName = agentRunMapping.get(agentResult.agent.getId());
            print("Fetching traces for: " + agentResult.agent.getName());
            print("  Run name: " + runName);

            try {
                List<TraceData> traces = langfuseService.fetchTracesFromRun(
                    agentResult.agent,
                    benchmark.getDatasetRef(),
                    runName
                );
                agentResult.traces = traces;
                print("  Traces retrieved: " + traces.size());

                // Show sample of metrics from Langfuse scores
                if (!traces.isEmpty()) {
                    TraceData sample = traces.get(0);
                    print("  Sample metrics (first trace):");
                    sample.langfuseScores.forEach((name, value) ->
                        print("    ├─ " + name + ": " + String.format("%.4f", value))
                    );
                }
            } catch (Exception e) {
                print("  ERROR fetching traces: " + e.getMessage());
                logger.error("Error fetching traces for agent {}: {}", agentResult.agent.getId(), e.getMessage(), e);
                agentResult.traces = new ArrayList<>();
            }
            print("");
        }

        printEndStep();
    }



    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 4: COMPUTE METRICS
    // ═══════════════════════════════════════════════════════════════════════════

    private void step4_ComputeMetrics() {
        printStep(4, "COMPUTING METRICS FOR EACH AGENT");

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            print("\nAgent: " + agentResult.agent.getName());
            print("Traces to process: " + agentResult.traces.size());

            if (agentResult.traces.isEmpty()) {
                print("  No traces available, using simulated metrics");
                agentResult.aggregatedMetrics = generateSimulatedMetrics(agentResult.agent.getId());
            } else {
                Map<String, Double> aggregated = traceMetricsAggregator.aggregate(agentResult.agent, agentResult.traces);
                if (aggregated.isEmpty()) {
                    print("  No metrics available from Langfuse or providers, using simulated metrics");
                    aggregated = generateSimulatedMetrics(agentResult.agent.getId());
                }
                agentResult.aggregatedMetrics = aggregated;
            }

            // Print metrics
            print("  Aggregated metrics:");
            agentResult.aggregatedMetrics.forEach((metric, value) ->
                print("    ├─ " + metric + ": " + String.format("%.4f", value))
            );

            // Store as MetricSnapshots
            agentResult.metricSnapshots = createMetricSnapshots(agentResult);
            print("  MetricSnapshots created: " + agentResult.metricSnapshots.size());
        }

        printEndStep();
    }

    // TODO: Verify if this is the right place for these methods. Consider generalizing in (LangfuseService, MetricService, BenchmarkService or similar)
    // FROM HERE
    private Map<String, Double> generateSimulatedMetrics(String agentId) {
        // Generate realistic simulated metrics based on agent type
        Map<String, Double> metrics = new LinkedHashMap<>();
        Random rand = new Random(agentId.hashCode());

        double baseRouge = switch (agentId) {
            case "agent-baseline" -> 0.35 + rand.nextDouble() * 0.1;
            case "agent-variant1-creative" -> 0.30 + rand.nextDouble() * 0.15;
            case "agent-variant2-deterministic" -> 0.38 + rand.nextDouble() * 0.08;
            default -> 0.30 + rand.nextDouble() * 0.1;
        };

        metrics.put("ROUGE", baseRouge);
        metrics.put("ROUGE1_F", baseRouge + rand.nextDouble() * 0.05);
        metrics.put("ROUGEL_F", baseRouge - rand.nextDouble() * 0.03);
        metrics.put("BLEU", baseRouge * (0.8 + rand.nextDouble() * 0.2));
        metrics.put("ACCURACY", 0.5 + rand.nextDouble() * 0.3);
        metrics.put("COSINE_PRED_GOLD", 0.4 + rand.nextDouble() * 0.35);
        metrics.put("COSINE_PRED_SOURCE", 0.3 + rand.nextDouble() * 0.4);
        metrics.put("LEN_RATIO", 0.8 + rand.nextDouble() * 0.4);

        return metrics;
    }

    private List<MetricSnapshot> createMetricSnapshots(AgentBenchmarkResult agentResult) {
        List<MetricSnapshot> snapshots = new ArrayList<>();
        String runId = "run-" + agentResult.agent.getId();

        for (Map.Entry<String, Double> metric : agentResult.aggregatedMetrics.entrySet()) {
            MetricType type = mapToMetricType(metric.getKey());
            MetricSnapshot snapshot = new MetricSnapshot(runId, type, metric.getKey(), metric.getValue());
            snapshot.setMetricName(metric.getKey());
            snapshot.setUnit("score");
            snapshots.add(snapshot);
        }

        return snapshots;
    }

    private MetricType mapToMetricType(String metricName) {
        return switch (metricName.toUpperCase()) {
            case "ROUGE", "ROUGE1_F", "ROUGEL_F" -> MetricType.ROUGE;
            case "BLEU" -> MetricType.BLEU;
            case "ACCURACY", "COSINE_PRED_GOLD" -> MetricType.ACCURACY;
            case "PRECISION" -> MetricType.PRECISION;
            case "RECALL" -> MetricType.RECALL;
            case "F1_SCORE" -> MetricType.F1_SCORE;
            default -> MetricType.ACCURACY;
        };
    }
    // TO HERE


    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 5: CALCULATE KPIs
    // ═══════════════════════════════════════════════════════════════════════════

    private void step5_CalculateKPIs() {
        printStep(5, "KPI CALCULATION VIA DSL");

        // Define KPI formulas
        Map<String, String> kpiFormulas = new LinkedHashMap<>();
        kpiFormulas.put("Overall Quality", "WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)");
        kpiFormulas.put("Text Similarity", "AVERAGE(ROUGE, BLEU)");
        kpiFormulas.put("Min Performance", "MIN(ROUGE, BLEU, ACCURACY)");
        kpiFormulas.put("Quality Threshold", "THRESHOLD(ROUGE, 0.3)");

        print("\nKPI Formulas to evaluate:");
        kpiFormulas.forEach((name, formula) -> print("  ├─ " + name + ": " + formula));
        print("");

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            print("\nAgent: " + agentResult.agent.getName());
            agentResult.kpiValues = new LinkedHashMap<>();

            for (Map.Entry<String, String> kpi : kpiFormulas.entrySet()) {
                String kpiName = kpi.getKey();
                String formula = kpi.getValue();

                DslParseResult parseResult = kpiParser.parse(formula);
                if (parseResult.isSuccess()) {
                    double value = parseResult.getFormula().evaluate(agentResult.aggregatedMetrics);
                    agentResult.kpiValues.put(kpiName, value);
                    print("  ├─ " + kpiName + ": " + String.format("%.4f", value));
                } else {
                    print("  ├─ " + kpiName + ": ERROR - " + parseResult.getErrorsAsString());
                }
            }
        }

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 6: EXECUTE BENCHMARK RUN FLOW
    // ═══════════════════════════════════════════════════════════════════════════

    private void step6_ExecuteBenchmarkRun() {
        printStep(6, "COMPLETE BENCHMARK RUN EXECUTION");

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            print("\n" + MINI_SEP);
            print("BENCHMARK RUN for: " + agentResult.agent.getName());
            print(MINI_SEP);

            // Create BenchmarkRun
            BenchmarkRun run = benchmarkRunService.createRun(benchmark, agentResult.agent);
            agentResult.benchmarkRun = run;

            print("\n1. BenchmarkRun Creation:");
            print("   ├─ Run ID: " + run.getId());
            print("   ├─ Benchmark: " + run.getBenchmarkId());
            print("   ├─ Agent: " + run.getAgentId());
            print("   ├─ Trigger: " + run.getTriggeredBy());
            print("   └─ Status: " + run.getStatus());

            // Start run
            print("\n2. Starting run (PENDING -> RUNNING):");
            benchmarkRunService.startRun(run);
            print("   ├─ Status: " + run.getStatus());
            print("   └─ Started at: " + run.getStartedAt());

            // Process traces
            print("\n3. Processing traces:");
            int tracesProcessed = agentResult.traces.size();
            int metricsComputed = agentResult.metricSnapshots.size();
            print("   ├─ Traces processed: " + tracesProcessed);
            print("   └─ Metrics computed: " + metricsComputed);

            // Create BenchmarkResults
            print("\n4. Creating BenchmarkResult for each trace:");
            List<BenchmarkResult> results = benchmarkRunService.buildBenchmarkResults(
                run,
                agentResult.traces,
                agentResult.kpiValues
            );
            agentResult.benchmarkResults = results;
            print("   └─ BenchmarkResults created: " + results.size());

            // Simulate alert evaluation
            print("\n5. Alert Evaluation:");
            double rougeValue = benchmarkRunService.getMetricValue(agentResult.aggregatedMetrics, "ROUGE");
            boolean alertTriggered = benchmarkRunService.isAlertTriggered(rougeValue, 0.3);
            print("   ├─ Condition: ROUGE < 0.3");
            print("   ├─ ROUGE value: " + String.format("%.4f", rougeValue));
            print("   └─ Alert triggered: " + (alertTriggered ? "YES" : "NO"));

            // Complete run
            print("\n6. Completing run (RUNNING -> COMPLETED):");
            benchmarkRunService.completeRun(run, tracesProcessed, metricsComputed);
            print("   ├─ Status: " + run.getStatus());
            print("   ├─ Completed at: " + run.getCompletedAt());
            print("   └─ Duration: " + run.getDurationMillis() + "ms");
        }

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 7: COMPARE AGENTS AND GENERATE REPORT
    // ═══════════════════════════════════════════════════════════════════════════

    private void step7_CompareAgentsAndReport() {
        printStep(7, "AGENT COMPARISON AND FINAL REPORT");

        print("\n" + SECTION_SEP);
        print("                    BENCHMARK RESULTS - AGENT COMPARISON");
        print(SECTION_SEP);

        // Print comparison table header
        print("");
        print(String.format("%-35s | %-15s | %-15s | %-15s",
            "Metric/KPI", "Baseline", "Creative", "Deterministic"));
        print("─".repeat(90));

        // Get agent results
        AgentBenchmarkResult baseline = agentResults.get("agent-baseline");
        AgentBenchmarkResult creative = agentResults.get("agent-variant1-creative");
        AgentBenchmarkResult deterministic = agentResults.get("agent-variant2-deterministic");

        // Metrics comparison
        Set<String> allMetrics = new LinkedHashSet<>();
        allMetrics.addAll(baseline.aggregatedMetrics.keySet());
        allMetrics.addAll(creative.aggregatedMetrics.keySet());
        allMetrics.addAll(deterministic.aggregatedMetrics.keySet());

        print("\nMETRICS:");
        for (String metric : allMetrics) {
            if (metric.equals("ROUGE") || metric.equals("BLEU") || metric.equals("ACCURACY") ||
                metric.equals("ROUGE1_F") || metric.equals("COSINE_PRED_GOLD")) {
                print(String.format("%-35s | %-15s | %-15s | %-15s",
                    metric,
                    formatValue(baseline.aggregatedMetrics.get(metric)),
                    formatValue(creative.aggregatedMetrics.get(metric)),
                    formatValue(deterministic.aggregatedMetrics.get(metric))
                ));
            }
        }

        // KPI comparison
        print("\nKPI:");
        Set<String> allKpis = new LinkedHashSet<>();
        allKpis.addAll(baseline.kpiValues.keySet());

        for (String kpi : allKpis) {
            print(String.format("%-35s | %-15s | %-15s | %-15s",
                kpi,
                formatValue(baseline.kpiValues.get(kpi)),
                formatValue(creative.kpiValues.get(kpi)),
                formatValue(deterministic.kpiValues.get(kpi))
            ));
        }

        print("─".repeat(90));

        // Determine winner
        print("\nCOMPARATIVE ANALYSIS:");
        print("");

        String overallQualityKpi = "Overall Quality";
        double baselineQuality = baseline.kpiValues.getOrDefault(overallQualityKpi, 0.0);
        double creativeQuality = creative.kpiValues.getOrDefault(overallQualityKpi, 0.0);
        double deterministicQuality = deterministic.kpiValues.getOrDefault(overallQualityKpi, 0.0);

        String winner;
        double maxQuality = Math.max(baselineQuality, Math.max(creativeQuality, deterministicQuality));
        if (maxQuality == deterministicQuality) {
            winner = "Deterministic (mistral:7b)";
        } else if (maxQuality == baselineQuality) {
            winner = "Baseline (llama3.2:3b)";
        } else {
            winner = "Creative (llama3.1:8b)";
        }

        print("  Best Overall Quality: " + winner + " with score " + String.format("%.4f", maxQuality));
        print("");

        // Summary
        print("  Baseline (llama3.2:3b, temp=0):");
        print("    - Pro: Consistent and reproducible results");
        print("    - Con: May lack variety in descriptions");
        print("");
        print("  Creative (llama3.1:8b, temp=0.8):");
        print("    - Pro: More varied and potentially creative descriptions");
        print("    - Con: Greater variability in results");
        print("");
        print("  Deterministic (mistral:7b, temp=0.2):");
        print("    - Pro: Good balance between consistency and quality");
        print("    - Con: Shorter output (num_predict=256)");

        // Run statistics
        print("\n" + SECTION_SEP);
        print("                           EXECUTION STATISTICS");
        print(SECTION_SEP);
        print("");

        for (AgentBenchmarkResult result : agentResults.values()) {
            BenchmarkRun run = result.benchmarkRun;
            print(result.agent.getName() + ":");
            print("  ├─ Run ID: " + run.getId());
            print("  ├─ Status: " + run.getStatus());
            print("  ├─ Traces: " + run.getTracesProcessed());
            print("  ├─ Metrics: " + run.getMetricsComputed());
            print("  └─ Duration: " + run.getDurationMillis() + "ms");
            print("");
        }

        print(SECTION_SEP);
        print("                              IMPORTANT NOTE");
        print(SECTION_SEP);
        print("");
        print("This demo did NOT persist data to the database.");
        print("To enable persistence, integrate with the repositories:");
        print("  - BenchmarkRunRepository");
        print("  - BenchmarkResultRepository");
        print("  - MetricSnapshotRepository");
        print("");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════════════════

   

    private String formatValue(Double value) {
        return value != null ? String.format("%.4f", value) : "N/A";
    }

    private void printHeader(String title) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  " + title);
        System.out.println(SEPARATOR + "\n");
    }

    private void printStep(int num, String title) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("  STEP " + num + ": " + title);
        System.out.println(SEPARATOR);
    }

    private void printEndStep() {
        System.out.println("\n" + MINI_SEP);
    }

    private void print(String text) {
        System.out.println(text);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INNER CLASSES FOR DATA STRUCTURES
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Container for trace data including Langfuse scores
     */

    /**
     * Container for agent benchmark results (in-memory, no persistence)
     */
    private static class AgentBenchmarkResult {
        Agent agent;
        List<TraceData> traces = new ArrayList<>();
        Map<String, Double> aggregatedMetrics = new LinkedHashMap<>();
        Map<String, Double> kpiValues = new LinkedHashMap<>();
        List<MetricSnapshot> metricSnapshots = new ArrayList<>();
        List<BenchmarkResult> benchmarkResults = new ArrayList<>();
        BenchmarkRun benchmarkRun;

        AgentBenchmarkResult(Agent agent) {
            this.agent = agent;
        }
    }
}
