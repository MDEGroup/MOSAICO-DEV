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
 * Demo che utilizza dati reali da Langfuse per eseguire il benchmark su 3 agenti
 * di summarization (generazione descrizioni GitHub da README files).
 *
 * Agenti valutati:
 * 1. sum_agent (baseline) - llama3.2:3b con temperature=0
 * 2. variant1 - llama3.1:8b con temperature=0.8 (creative)
 * 3. variant2 - mistral:7b con temperature=0.2 (deterministic)
 *
 * Il flusso completo include:
 * - Creazione entita' Agent e Benchmark
 * - Recupero traces da Langfuse
 * - Calcolo metriche (ROUGE, BLEU, cosine similarity)
 * - Valutazione KPI tramite DSL
 * - Confronto performance tra agenti
 * - Esecuzione benchmark run completo
 *
 * NOTA: Per eseguire questa demo, Langfuse deve essere raggiungibile
 * con le credenziali specificate in application.properties
 *
 * Esecuzione:
 *   mvn spring-boot:run -Dspring-boot.run.profiles=demo-benchmark
 * oppure:
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
 * Runner component che esegue la demo quando il profilo "demo-benchmark" e' attivo.
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

    

    // Results storage (in-memory, no DB persistence)
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
        printHeader("MOSAICO BENCHMARKING SYSTEM - DEMO CON DATI REALI LANGFUSE");
        print("Data esecuzione: " + Instant.now());
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

        printHeader("DEMO COMPLETATA");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 1: CREATE AGENT ENTITIES
    // ═══════════════════════════════════════════════════════════════════════════

    private void step1_CreateAgentEntities() {
        printStep(1, "CREAZIONE ENTITA' AGENT");

        print("\nCreo 3 istanze Agent che rappresentano i 3 agenti di summarization:");
        print("");

        // Agent 1: Baseline (sum_agent.py)
        Agent baselineAgent = createAgent(
            "agent-baseline",
            "SummarizationBot Baseline",
            "Agente baseline per generazione descrizioni GitHub. Usa llama3.2:3b con temperature=0 per output deterministici.",
            "llama3.2:3b",
            Map.of("temperature", 0.0, "top_p", 1.0, "num_predict", 512)
        );
        agentResults.put(baselineAgent.getId(), new AgentBenchmarkResult(baselineAgent));

        // Agent 2: Variant 1 - Creative (sum_agent_variant1.py)
        Agent creativeAgent = createAgent(
            "agent-variant1-creative",
            "SummarizationBot Creative",
            "Variante creativa con llama3.1:8b e temperature=0.8 per output piu' variati e creativi.",
            "llama3.1:8b",
            Map.of("temperature", 0.8, "top_p", 0.95, "top_k", 50, "num_predict", 512)
        );
        agentResults.put(creativeAgent.getId(), new AgentBenchmarkResult(creativeAgent));

        // Agent 3: Variant 2 - Deterministic (sum_agent_variant2.py)
        Agent deterministicAgent = createAgent(
            "agent-variant2-deterministic",
            "SummarizationBot Deterministic",
            "Variante deterministica con mistral:7b e temperature=0.2 per output precisi e consistenti.",
            "mistral:7b",
            Map.of("temperature", 0.2, "top_p", 0.8, "top_k", 20, "num_predict", 256, "repeat_penalty", 1.1)
        );
        agentResults.put(deterministicAgent.getId(), new AgentBenchmarkResult(deterministicAgent));

        print("");
        print("Agent creati: " + agentResults.size());
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
        agent.setObjective("Generare descrizioni concise e accurate per repository GitHub basandosi sui README files");
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
            "Sintetizza README in descrizioni curate (" + model + ")",
            ProficiencyLevel.EXPERT,
            now,
            List.of("TASK_SUMMARY", agentId + "_README")
        );
        Skill evaluation = new Skill(
            null,
            "Self-Eval",
            "Valuta la coerenza con le metriche ROUGE/BLEU",
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
            "Recupera traces e metriche per il modello " + model,
            "API_KEY",
            "traces.read,metrics.read",
            "120 rpm",
            "burst-200"
        );
        Tool githubTool = new Tool(
            null,
            "GitHub Ingestor",
            "Scarica README e metadati repository",
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
            "Riceve notifiche di completamento trace"
        );
        InteractionProtocol webhook = new InteractionProtocol(
            null,
            "LangfuseDatasetRun",
            "0.9",
            langfuseProperties.getBaseUrl() + "/api/public/datasets",
            "Pubblica risultati benchmark"
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
        printStep(2, "CREAZIONE DEFINIZIONE BENCHMARK");

        benchmark = new Benchmark();
        benchmark.setId("bench-summarization-001");
        benchmark.setMetadata("{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}");
        benchmark.setDatasetRef(DATASET_NAME);
        benchmark.setTaskDef("Genera una descrizione concisa (3-6 frasi) per un repository GitHub partendo dal README file");
        benchmark.setFeatures("accuracy,fluency,conciseness,relevance");
        benchmark.setProtocolVersion("1.0");
        benchmark.setEvaluates(new ArrayList<>(agentResults.values().stream().map(r -> r.agent).toList()));

        print("\nBenchmark creato:");
        print("  ├─ ID: " + benchmark.getId());
        print("  ├─ Dataset: " + benchmark.getDatasetRef());
        print("  ├─ Task: " + benchmark.getTaskDef());
        print("  ├─ Features: " + benchmark.getFeatures());
        print("  └─ Agenti valutati: " + benchmark.getEvaluates().size());

        print("\nKPI Formule definite:");
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
        printStep(3, "RECUPERO DATI REALI DA LANGFUSE");

        print("\nConnessione a Langfuse: " + langfuseProperties.getBaseUrl());
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
            print("Recupero traces per: " + agentResult.agent.getName());
            print("  Run name: " + runName);

            try {
                List<TraceData> traces = langfuseService.fetchTracesFromRun(agentResult.agent, runName, benchmark.getDatasetRef());
                agentResult.traces = traces;
                print("  Traces recuperate: " + traces.size());

                // Show sample of metrics from Langfuse scores
                if (!traces.isEmpty()) {
                    TraceData sample = traces.get(0);
                    print("  Esempio metriche (prima trace):");
                    sample.langfuseScores.forEach((name, value) ->
                        print("    ├─ " + name + ": " + String.format("%.4f", value))
                    );
                }
            } catch (Exception e) {
                print("  ERRORE nel recupero traces: " + e.getMessage());
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
        printStep(4, "CALCOLO METRICHE PER OGNI AGENTE");

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            print("\nAgent: " + agentResult.agent.getName());
            print("Traces da processare: " + agentResult.traces.size());

            if (agentResult.traces.isEmpty()) {
                print("  Nessuna trace disponibile, uso metriche simulate");
                agentResult.aggregatedMetrics = generateSimulatedMetrics(agentResult.agent.getId());
            } else {
                Map<String, Double> aggregated = traceMetricsAggregator.aggregate(agentResult.agent, agentResult.traces);
                if (aggregated.isEmpty()) {
                    print("  Nessuna metrica disponibile da Langfuse o provider, uso metriche simulate");
                    aggregated = generateSimulatedMetrics(agentResult.agent.getId());
                }
                agentResult.aggregatedMetrics = aggregated;
            }

            // Print metrics
            print("  Metriche aggregate:");
            agentResult.aggregatedMetrics.forEach((metric, value) ->
                print("    ├─ " + metric + ": " + String.format("%.4f", value))
            );

            // Store as MetricSnapshots
            agentResult.metricSnapshots = createMetricSnapshots(agentResult);
            print("  MetricSnapshots creati: " + agentResult.metricSnapshots.size());
        }

        printEndStep();
    }

    //VERIFICARE SE QUESTO è il posto giusto per questi metodi. Vedere se si può generalizzare in (LangfuseService, MetricService, BenchmarkService o simili)
    // DA QUI
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
    //FINO A QUI


    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 5: CALCULATE KPIs
    // ═══════════════════════════════════════════════════════════════════════════

    private void step5_CalculateKPIs() {
        printStep(5, "CALCOLO KPI TRAMITE DSL");

        // Define KPI formulas
        Map<String, String> kpiFormulas = new LinkedHashMap<>();
        kpiFormulas.put("Overall Quality", "WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)");
        kpiFormulas.put("Text Similarity", "AVERAGE(ROUGE, BLEU)");
        kpiFormulas.put("Min Performance", "MIN(ROUGE, BLEU, ACCURACY)");
        kpiFormulas.put("Quality Threshold", "THRESHOLD(ROUGE, 0.3)");

        print("\nFormule KPI da valutare:");
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
                    print("  ├─ " + kpiName + ": ERRORE - " + parseResult.getErrorsAsString());
                }
            }
        }

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // STEP 6: EXECUTE BENCHMARK RUN FLOW
    // ═══════════════════════════════════════════════════════════════════════════

    private void step6_ExecuteBenchmarkRun() {
        printStep(6, "ESECUZIONE BENCHMARK RUN COMPLETO");

        for (AgentBenchmarkResult agentResult : agentResults.values()) {
            print("\n" + MINI_SEP);
            print("BENCHMARK RUN per: " + agentResult.agent.getName());
            print(MINI_SEP);

            // Create BenchmarkRun
            BenchmarkRun run = benchmarkRunService.createRun(benchmark, agentResult.agent);
            agentResult.benchmarkRun = run;

            print("\n1. Creazione BenchmarkRun:");
            print("   ├─ Run ID: " + run.getId());
            print("   ├─ Benchmark: " + run.getBenchmarkId());
            print("   ├─ Agent: " + run.getAgentId());
            print("   ├─ Trigger: " + run.getTriggeredBy());
            print("   └─ Status: " + run.getStatus());

            // Start run
            print("\n2. Avvio run (PENDING -> RUNNING):");
            benchmarkRunService.startRun(run);
            print("   ├─ Status: " + run.getStatus());
            print("   └─ Started at: " + run.getStartedAt());

            // Process traces
            print("\n3. Elaborazione traces:");
            int tracesProcessed = agentResult.traces.size();
            int metricsComputed = agentResult.metricSnapshots.size();
            print("   ├─ Traces processate: " + tracesProcessed);
            print("   └─ Metriche calcolate: " + metricsComputed);

            // Create BenchmarkResults
            print("\n4. Creazione BenchmarkResult per ogni trace:");
            List<BenchmarkResult> results = benchmarkRunService.buildBenchmarkResults(
                run,
                agentResult.traces,
                agentResult.kpiValues
            );
            agentResult.benchmarkResults = results;
            print("   └─ BenchmarkResults creati: " + results.size());

            // Simulate alert evaluation
            print("\n5. Valutazione Alert:");
            double rougeValue = benchmarkRunService.getMetricValue(agentResult.aggregatedMetrics, "ROUGE");
            boolean alertTriggered = benchmarkRunService.isAlertTriggered(rougeValue, 0.3);
            print("   ├─ Condizione: ROUGE < 0.3");
            print("   ├─ Valore ROUGE: " + String.format("%.4f", rougeValue));
            print("   └─ Alert triggered: " + (alertTriggered ? "SI" : "NO"));

            // Complete run
            print("\n6. Completamento run (RUNNING -> COMPLETED):");
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
        printStep(7, "CONFRONTO AGENTI E REPORT FINALE");

        print("\n" + SECTION_SEP);
        print("                    RISULTATI BENCHMARK - CONFRONTO AGENTI");
        print(SECTION_SEP);

        // Print comparison table header
        print("");
        print(String.format("%-35s | %-15s | %-15s | %-15s",
            "Metrica/KPI", "Baseline", "Creative", "Deterministic"));
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

        print("\nMETRICHE:");
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
        print("\nANALISI COMPARATIVA:");
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

        print("  Migliore Overall Quality: " + winner + " con score " + String.format("%.4f", maxQuality));
        print("");

        // Summary
        print("  Baseline (llama3.2:3b, temp=0):");
        print("    - Pro: Risultati consistenti e riproducibili");
        print("    - Contro: Potrebbe mancare di varieta' nelle descrizioni");
        print("");
        print("  Creative (llama3.1:8b, temp=0.8):");
        print("    - Pro: Descrizioni piu' varie e potenzialmente creative");
        print("    - Contro: Maggiore variabilita' nei risultati");
        print("");
        print("  Deterministic (mistral:7b, temp=0.2):");
        print("    - Pro: Buon bilanciamento tra consistenza e qualita'");
        print("    - Contro: Output piu' brevi (num_predict=256)");

        // Run statistics
        print("\n" + SECTION_SEP);
        print("                           STATISTICHE ESECUZIONE");
        print(SECTION_SEP);
        print("");

        for (AgentBenchmarkResult result : agentResults.values()) {
            BenchmarkRun run = result.benchmarkRun;
            print(result.agent.getName() + ":");
            print("  ├─ Run ID: " + run.getId());
            print("  ├─ Status: " + run.getStatus());
            print("  ├─ Traces: " + run.getTracesProcessed());
            print("  ├─ Metriche: " + run.getMetricsComputed());
            print("  └─ Durata: " + run.getDurationMillis() + "ms");
            print("");
        }

        print(SECTION_SEP);
        print("                              NOTA IMPORTANTE");
        print(SECTION_SEP);
        print("");
        print("Questa demo NON ha persistito i dati nel database.");
        print("Per abilitare la persistenza, integrare con i repository:");
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
