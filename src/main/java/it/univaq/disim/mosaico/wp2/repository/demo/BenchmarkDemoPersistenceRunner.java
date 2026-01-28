package it.univaq.disim.mosaico.wp2.repository.demo;

import it.univaq.disim.mosaico.wp2.repository.config.LangfuseProperties;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.AgentConsumption;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkResult;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.Deployment;
import it.univaq.disim.mosaico.wp2.repository.data.InputParameter;
import it.univaq.disim.mosaico.wp2.repository.data.InteractionProtocol;
import it.univaq.disim.mosaico.wp2.repository.data.Memory;
import it.univaq.disim.mosaico.wp2.repository.data.MetricSnapshot;
import it.univaq.disim.mosaico.wp2.repository.data.OutputStructure;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.data.Tool;
import it.univaq.disim.mosaico.wp2.repository.data.enums.DeploymentMode;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.ProficiencyLevel;
import it.univaq.disim.mosaico.wp2.repository.dsl.DslParseResult;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaParser;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkResultRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricSnapshotRepository;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;
import it.univaq.disim.mosaico.wp2.repository.service.impl.BenchmarkRunService;
import it.univaq.disim.mosaico.wp2.repository.service.impl.TraceMetricsAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Variante del runner di demo che persiste i dati nelle tabelle reali e
 * evita l'uso delle classi contenitrici in-memory. Utile per validare
 * tutto il flusso end-to-end su un database reale.
 */
@Component
@Profile("demo-benchmark-persistence")
public class BenchmarkDemoPersistenceRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkDemoPersistenceRunner.class);

    private static final String SEPARATOR = "═".repeat(80);
    private static final String MINI_SEP = "─".repeat(60);
    private static final String SECTION_SEP = "━".repeat(80);

    private static final String DATASET_NAME = "ause_train";
    private static final String BASELINE_RUN = "experiment_00 - 2026-01-22T10:44:46.144492Z";
    private static final String VARIANT1_RUN = "experiment_variant1_llama32_1b_fast_train - 2026-01-22T15:12:30.713341Z";
    private static final String VARIANT2_RUN = "experiment_variant2_qwen25_05b_compact_train - 2026-01-22T18:40:01.178020Z";

    private final LangfuseService langfuseService;
    private final LangfuseProperties langfuseProperties;
    private final KPIFormulaParser kpiParser;
    private final TraceMetricsAggregator traceMetricsAggregator;
    private final BenchmarkRunService benchmarkRunService;

    private final AgentRepository agentRepository;
    private final BenchmarkRepository benchmarkRepository;
    private final BenchmarkRunRepository benchmarkRunRepository;
    private final BenchmarkResultRepository benchmarkResultRepository;
    private final MetricSnapshotRepository metricSnapshotRepository;

    public BenchmarkDemoPersistenceRunner(
            LangfuseService langfuseService,
            LangfuseProperties langfuseProperties,
            KPIFormulaParser kpiParser,
            TraceMetricsAggregator traceMetricsAggregator,
            BenchmarkRunService benchmarkRunService,
            AgentRepository agentRepository,
            BenchmarkRepository benchmarkRepository,
            BenchmarkRunRepository benchmarkRunRepository,
            BenchmarkResultRepository benchmarkResultRepository,
            MetricSnapshotRepository metricSnapshotRepository) {
        this.langfuseService = langfuseService;
        this.langfuseProperties = langfuseProperties;
        this.kpiParser = kpiParser;
        this.traceMetricsAggregator = traceMetricsAggregator;
        this.benchmarkRunService = benchmarkRunService;
        this.agentRepository = agentRepository;
        this.benchmarkRepository = benchmarkRepository;
        this.benchmarkRunRepository = benchmarkRunRepository;
        this.benchmarkResultRepository = benchmarkResultRepository;
        this.metricSnapshotRepository = metricSnapshotRepository;
    }

    @Override
    public void run(String... args) {
        runWithPersistence();
    }

    private void runWithPersistence() {
        printHeader("MOSAICO BENCHMARKING SYSTEM - DEMO CON PERSISTENZA");
        print("Data esecuzione: " + Instant.now());
        print("Dataset: " + DATASET_NAME);
        print("");

        kpiParser.registerMetricKeys(Set.of("ROUGE1_F", "ROUGEL_F", "COSINE_PRED_GOLD", "COSINE_PRED_SOURCE", "LEN_RATIO"));

        List<Agent> agents = step1_CreateAndPersistAgents();
        Benchmark benchmark = step2_CreateAndPersistBenchmark(agents);
        Map<String, String> agentRunMapping = Map.of(
            "agent-baseline", BASELINE_RUN,
            "agent-variant1-creative", VARIANT1_RUN,
            "agent-variant2-deterministic", VARIANT2_RUN
        );
        Map<String, List<TraceData>> tracesByAgent = step3_FetchLangfuseData(agents, benchmark);
        Map<String, Map<String, Double>> aggregatedMetrics = step4_ComputeMetrics(agents, tracesByAgent);
        Map<String, Map<String, Double>> kpiValues = step5_CalculateKPIs(agents, aggregatedMetrics);
        step6_ExecuteAndPersistRuns(benchmark, agents, tracesByAgent, aggregatedMetrics, kpiValues, agentRunMapping);
        step7_CompareAgentsAndReport(benchmark, agents, aggregatedMetrics, kpiValues);

        printHeader("DEMO COMPLETATA - DATI PERSISTITI");
    }

    private List<Agent> step1_CreateAndPersistAgents() {
        printStep(1, "CREAZIONE & PERSISTENZA AGENT");
        List<Agent> agents = new ArrayList<>();

        agents.add(createAgent(
            "agent-baseline",
            "SummarizationBot Baseline",
            "Agente baseline per generazione descrizioni GitHub. Usa llama3.2:3b con temperature=0 per output deterministici.",
            "llama3.2:3b",
            Map.of("temperature", 0.0, "top_p", 1.0, "num_predict", 512)
        ));

        agents.add(createAgent(
            "agent-variant1-creative",
            "SummarizationBot Creative",
            "Variante creativa con llama3.1:8b e temperature=0.8 per output piu' variati e creativi.",
            "llama3.1:8b",
            Map.of("temperature", 0.8, "top_p", 0.95, "top_k", 50, "num_predict", 512)
        ));

        agents.add(createAgent(
            "agent-variant2-deterministic",
            "SummarizationBot Deterministic",
            "Variante deterministica con mistral:7b e temperature=0.2 per output precisi e consistenti.",
            "mistral:7b",
            Map.of("temperature", 0.2, "top_p", 0.8, "top_k", 20, "num_predict", 256, "repeat_penalty", 1.1)
        ));

        List<Agent> persisted = agents.stream().map(agentRepository::save).toList();

        print("\nAgent persistiti: " + persisted.size());
        persisted.forEach(agent -> print("  - " + agent.getName() + " [" + agent.getId() + "]"));
        printEndStep();
        return new ArrayList<>(persisted);
    }

    private Benchmark step2_CreateAndPersistBenchmark(List<Agent> agents) {
        printStep(2, "CREAZIONE & PERSISTENZA BENCHMARK");

        Benchmark benchmark = new Benchmark();
        benchmark.setId("bench-summarization-001");
        benchmark.setMetadata("{\"name\": \"GitHub Description Generation Benchmark\", \"version\": \"1.0\", \"task\": \"summarization\"}");
        benchmark.setDatasetRef(DATASET_NAME);
        benchmark.setTaskDef("Genera una descrizione concisa (3-6 frasi) per un repository GitHub partendo dal README file");
        benchmark.setFeatures("accuracy,fluency,conciseness,relevance");
        benchmark.setProtocolVersion("1.0");
        benchmark.setEvaluates(new ArrayList<>(agents));

        Benchmark saved = benchmarkRepository.save(benchmark);

        print("\nBenchmark persistito:");
        print("  ├─ ID: " + saved.getId());
        print("  ├─ Dataset: " + saved.getDatasetRef());
        print("  ├─ Task: " + saved.getTaskDef());
        print("  └─ Agenti valutati: " + saved.getEvaluates().size());
        printEndStep();
        return saved;
    }

    private Map<String, List<TraceData>> step3_FetchLangfuseData(List<Agent> agents, Benchmark benchmark) {
        printStep(3, "RECUPERO DATI REALI DA LANGFUSE");
        print("\nConnessione a Langfuse: " + langfuseProperties.getBaseUrl());
        print("Dataset: " + benchmark.getDatasetRef());

        Map<String, String> agentRunMapping = Map.of(
            "agent-baseline", BASELINE_RUN,
            "agent-variant1-creative", VARIANT1_RUN,
            "agent-variant2-deterministic", VARIANT2_RUN
        );

        Map<String, List<TraceData>> tracesByAgent = new HashMap<>();

        for (Agent agent : agents) {
            String runName = agentRunMapping.get(agent.getId());
            print("\nRecupero traces per: " + agent.getName());
            print("  Run name: " + runName);
            try {
                List<TraceData> traces = langfuseService.fetchTracesFromRun(
                    agent,
                    benchmark.getDatasetRef(),
                    runName
                );
                tracesByAgent.put(agent.getId(), traces);
                print("  Traces recuperate: " + traces.size());
                if (!traces.isEmpty()) {
                    TraceData sample = traces.get(0);
                    print("  Esempio metriche (prima trace):");
                    sample.langfuseScores.forEach((name, value) -> print("    ├─ " + name + ": " + String.format("%.4f", value)));
                }
            } catch (Exception ex) {
                print("  ERRORE nel recupero traces: " + ex.getMessage());
                logger.error("Error fetching traces for agent {}", agent.getId(), ex);
                tracesByAgent.put(agent.getId(), new ArrayList<>());
            }
        }

        printEndStep();
        return tracesByAgent;
    }

    private Map<String, Map<String, Double>> step4_ComputeMetrics(List<Agent> agents, Map<String, List<TraceData>> tracesByAgent) {
        printStep(4, "CALCOLO METRICHE & SNAPSHOT IN MEMORIA");
        Map<String, Map<String, Double>> aggregatedMetrics = new LinkedHashMap<>();

        for (Agent agent : agents) {
            List<TraceData> traces = tracesByAgent.getOrDefault(agent.getId(), List.of());
            print("\nAgent: " + agent.getName());
            print("Traces da processare: " + traces.size());

            Map<String, Double> metrics;
            if (traces.isEmpty()) {
                print("  Nessuna trace disponibile, uso metriche simulate");
                metrics = generateSimulatedMetrics(agent.getId());
            } else {
                metrics = traceMetricsAggregator.aggregate(agent, traces);
                if (metrics.isEmpty()) {
                    print("  Nessuna metrica disponibile da Langfuse o provider, uso metriche simulate");
                    metrics = generateSimulatedMetrics(agent.getId());
                }
            }
            aggregatedMetrics.put(agent.getId(), metrics);

            print("  Metriche aggregate:");
            metrics.forEach((metric, value) -> print("    ├─ " + metric + ": " + String.format("%.4f", value)));
        }

        printEndStep();
        return aggregatedMetrics;
    }

    private Map<String, Map<String, Double>> step5_CalculateKPIs(List<Agent> agents, Map<String, Map<String, Double>> aggregatedMetrics) {
        printStep(5, "CALCOLO KPI TRAMITE DSL (IN MEMORIA)");

        Map<String, String> kpiFormulas = new LinkedHashMap<>();
        kpiFormulas.put("Overall Quality", "WEIGHTED_SUM(ROUGE:0.4, BLEU:0.3, ACCURACY:0.3)");
        kpiFormulas.put("Text Similarity", "AVERAGE(ROUGE, BLEU)");
        kpiFormulas.put("Min Performance", "MIN(ROUGE, BLEU, ACCURACY)");
        kpiFormulas.put("Quality Threshold", "THRESHOLD(ROUGE, 0.3)");

        print("\nFormule KPI da valutare:");
        kpiFormulas.forEach((name, formula) -> print("  ├─ " + name + ": " + formula));

        Map<String, Map<String, Double>> kpiValues = new HashMap<>();

        for (Agent agent : agents) {
            print("\nAgent: " + agent.getName());
            Map<String, Double> metrics = aggregatedMetrics.getOrDefault(agent.getId(), Map.of());
            Map<String, Double> values = new LinkedHashMap<>();

            for (Map.Entry<String, String> kpi : kpiFormulas.entrySet()) {
                DslParseResult parseResult = kpiParser.parse(kpi.getValue());
                if (parseResult.isSuccess()) {
                    double value = parseResult.getFormula().evaluate(metrics);
                    values.put(kpi.getKey(), value);
                    print("  ├─ " + kpi.getKey() + ": " + String.format("%.4f", value));
                } else {
                    print("  ├─ " + kpi.getKey() + ": ERRORE - " + parseResult.getErrorsAsString());
                }
            }
            kpiValues.put(agent.getId(), values);
        }

        printEndStep();
        return kpiValues;
    }

    private void step6_ExecuteAndPersistRuns(
            Benchmark benchmark,
            List<Agent> agents,
            Map<String, List<TraceData>> tracesByAgent,
            Map<String, Map<String, Double>> aggregatedMetrics,
            Map<String, Map<String, Double>> kpiValues,
            Map<String, String> agentRunMapping) {

        printStep(6, "ESECUZIONE BENCHMARK RUN CON PERSISTENZA");

        for (Agent agent : agents) {
            print("\n" + MINI_SEP);
            print("BENCHMARK RUN per: " + agent.getName());
            print(MINI_SEP);

            String langfuseRunName = agentRunMapping.get(agent.getId());
            BenchmarkRun run = benchmarkRunService.createRun(benchmark, agent, langfuseRunName);
            run.setId("run-" + UUID.randomUUID().toString().substring(0, 8));
            run = benchmarkRunRepository.save(run);

            print("\n1. Creazione BenchmarkRun persistita:");
            print("   ├─ Run ID: " + run.getId());
            print("   ├─ Benchmark: " + run.getBenchmarkId());
            print("   ├─ Agent: " + run.getAgentId());
            print("   └─ Status: " + run.getStatus());

            benchmarkRunService.startRun(run);
            run = benchmarkRunRepository.save(run);
            print("\n2. Avvio run (PENDING -> RUNNING) persistito");
            print("   ├─ Status: " + run.getStatus());
            print("   └─ Started at: " + run.getStartedAt());

            List<TraceData> traces = tracesByAgent.getOrDefault(agent.getId(), List.of());
            int tracesProcessed = traces.size();

            List<BenchmarkResult> results = benchmarkRunService.buildBenchmarkResults(
                run,
                traces,
                kpiValues.getOrDefault(agent.getId(), Map.of())
            );
            benchmarkResultRepository.saveAll(results);
            print("\n3. BenchmarkResult persistiti: " + results.size());

            List<MetricSnapshot> snapshots = createMetricSnapshots(run.getId(), aggregatedMetrics.get(agent.getId()));
            metricSnapshotRepository.saveAll(snapshots);
            int metricsComputed = snapshots.size();
            print("4. MetricSnapshot persistiti: " + metricsComputed);

            double rougeValue = benchmarkRunService.getMetricValue(aggregatedMetrics.get(agent.getId()), "ROUGE");
            boolean alertTriggered = benchmarkRunService.isAlertTriggered(rougeValue, 0.3);
            print("\n5. Valutazione Alert:");
            print("   ├─ Condizione: ROUGE < 0.3");
            print("   ├─ Valore ROUGE: " + String.format("%.4f", rougeValue));
            print("   └─ Alert triggered: " + (alertTriggered ? "SI" : "NO"));

            benchmarkRunService.completeRun(run, tracesProcessed, metricsComputed);
            run = benchmarkRunRepository.save(run);
            print("\n6. Completamento run persistito:");
            print("   ├─ Status: " + run.getStatus());
            print("   ├─ Completed at: " + run.getCompletedAt());
            print("   └─ Duration: " + run.getDurationMillis() + "ms");
        }

        printEndStep();
    }

    private void step7_CompareAgentsAndReport(
            Benchmark benchmark,
            List<Agent> agents,
            Map<String, Map<String, Double>> aggregatedMetrics,
            Map<String, Map<String, Double>> kpiValues) {

        printStep(7, "REPORT DA DATI PERSISTITI");
        print("\n" + SECTION_SEP);
        print("                    RISULTATI BENCHMARK - CONFRONTO AGENTI");
        print(SECTION_SEP);

        print("\n" + String.format("%-35s | %-15s | %-15s | %-15s", "Metrica/KPI", "Baseline", "Creative", "Deterministic"));
        print("─".repeat(90));

        Agent baseline = agents.stream().filter(a -> a.getId().equals("agent-baseline")).findFirst().orElse(null);
        Agent creative = agents.stream().filter(a -> a.getId().equals("agent-variant1-creative")).findFirst().orElse(null);
        Agent deterministic = agents.stream().filter(a -> a.getId().equals("agent-variant2-deterministic")).findFirst().orElse(null);

        Map<String, Double> baselineMetrics = aggregatedMetrics.getOrDefault("agent-baseline", Map.of());
        Map<String, Double> creativeMetrics = aggregatedMetrics.getOrDefault("agent-variant1-creative", Map.of());
        Map<String, Double> deterministicMetrics = aggregatedMetrics.getOrDefault("agent-variant2-deterministic", Map.of());

        Set<String> allMetrics = new LinkedHashSet<>();
        allMetrics.addAll(baselineMetrics.keySet());
        allMetrics.addAll(creativeMetrics.keySet());
        allMetrics.addAll(deterministicMetrics.keySet());

        print("\nMETRICHE:");
        for (String metric : allMetrics) {
            if (metric.equals("ROUGE") || metric.equals("BLEU") || metric.equals("ACCURACY") ||
                metric.equals("ROUGE1_F") || metric.equals("COSINE_PRED_GOLD")) {
                print(String.format("%-35s | %-15s | %-15s | %-15s",
                    metric,
                    formatValue(baselineMetrics.get(metric)),
                    formatValue(creativeMetrics.get(metric)),
                    formatValue(deterministicMetrics.get(metric))));
            }
        }

        print("\nKPI:");
        Map<String, Double> baselineKpi = kpiValues.getOrDefault("agent-baseline", Map.of());
        Map<String, Double> creativeKpi = kpiValues.getOrDefault("agent-variant1-creative", Map.of());
        Map<String, Double> deterministicKpi = kpiValues.getOrDefault("agent-variant2-deterministic", Map.of());

        for (String kpi : baselineKpi.keySet()) {
            print(String.format("%-35s | %-15s | %-15s | %-15s",
                kpi,
                formatValue(baselineKpi.get(kpi)),
                formatValue(creativeKpi.get(kpi)),
                formatValue(deterministicKpi.get(kpi))));
        }

        print("─".repeat(90));
        print("\nANALISI COMPARATIVA:");

        double baselineQuality = baselineKpi.getOrDefault("Overall Quality", 0.0);
        double creativeQuality = creativeKpi.getOrDefault("Overall Quality", 0.0);
        double deterministicQuality = deterministicKpi.getOrDefault("Overall Quality", 0.0);

        double maxQuality = Math.max(baselineQuality, Math.max(creativeQuality, deterministicQuality));
        String winner = maxQuality == deterministicQuality ? "Deterministic (mistral:7b)"
                : maxQuality == baselineQuality ? "Baseline (llama3.2:3b)"
                : "Creative (llama3.1:8b)";

        print("  Migliore Overall Quality: " + winner + " con score " + String.format("%.4f", maxQuality));

        print("\nSTATISTICHE RUN (da DB):");
        benchmarkRunRepository.findByBenchmarkId(benchmark.getId()).forEach(run -> {
            print("  Run " + run.getId() + ":");
            print("    ├─ Agent: " + run.getAgentId());
            print("    ├─ Status: " + run.getStatus());
            print("    ├─ Traces: " + run.getTracesProcessed());
            print("    ├─ Metriche: " + run.getMetricsComputed());
            print("    └─ Durata: " + run.getDurationMillis() + "ms");
        });

        printEndStep();
    }

    // Helpers -----------------------------------------------------------------

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
        return agent;
    }

    private Deployment buildDeployment(String model) {
        String sanitizedModel = model.replace(":", "-").replace("/", "-");
        return new Deployment(DeploymentMode.DOCKER, "ghcr.io/mosaico/" + sanitizedModel + ":latest");
    }

    private List<Skill> buildSkills(String agentId, String model) {
        LocalDateTime now = LocalDateTime.now();
        Skill summarization = new Skill(null, "Summarization", "Sintetizza README in descrizioni curate (" + model + ")", ProficiencyLevel.EXPERT, now, List.of("TASK_SUMMARY", agentId + "_README"));
        Skill evaluation = new Skill(null, "Self-Eval", "Valuta la coerenza con le metriche ROUGE/BLEU", ProficiencyLevel.ADVANCED, now, List.of("TASK_EVAL"));
        return List.of(summarization, evaluation);
    }

    private List<Tool> buildTools(String model) {
        Tool langfuseTool = new Tool(null, "Langfuse API", "Recupera traces e metriche per il modello " + model, "API_KEY", "traces.read,metrics.read", "120 rpm", "burst-200");
        Tool githubTool = new Tool(null, "GitHub Ingestor", "Scarica README e metadati repository", "OAUTH_APP", "repo.read", "5 rps", "per-repo");
        return List.of(langfuseTool, githubTool);
    }

    private List<Memory> buildMemories(String agentId) {
        Memory shortTerm = new Memory(null, MemoryType.SHORT_TERM, MemoryScope.AGENT, "redis://cache/" + agentId);
        Memory longTerm = new Memory(null, MemoryType.LONG_TERM, MemoryScope.SHARED, "postgres://langfuse/" + DATASET_NAME);
        return List.of(shortTerm, longTerm);
    }

    private List<InteractionProtocol> buildProtocols() {
        InteractionProtocol rest = new InteractionProtocol(null, "REST_CALLBACK", "1.0", langfuseProperties.getBaseUrl() + "/api/public/trace", "Riceve notifiche di completamento trace");
        InteractionProtocol webhook = new InteractionProtocol(null, "LangfuseDatasetRun", "0.9", langfuseProperties.getBaseUrl() + "/api/public/datasets", "Pubblica risultati benchmark");
        return List.of(rest, webhook);
    }

    private List<AgentConsumption> buildConsumptions(String model, Map<String, Object> options) {
        List<InputParameter> params = options.entrySet().stream()
            .map(entry -> new InputParameter(entry.getKey(), String.valueOf(entry.getValue())))
            .collect(Collectors.toList());
        List<OutputStructure> outputs = List.of(new OutputStructure("summary", "markdown"), new OutputStructure("explanations", "json"));
        return List.of(new AgentConsumption(null, "model=" + model, params, outputs));
    }

    private Map<String, Double> generateSimulatedMetrics(String agentId) {
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

    private List<MetricSnapshot> createMetricSnapshots(String runId, Map<String, Double> metrics) {
        List<MetricSnapshot> snapshots = new ArrayList<>();
        if (metrics == null) {
            return snapshots;
        }
        metrics.forEach((metricName, value) -> {
            MetricSnapshot snapshot = new MetricSnapshot(runId, mapToMetricType(metricName), metricName, value);
            snapshot.setMetricName(metricName);
            snapshot.setUnit("score");
            snapshots.add(snapshot);
        });
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
}
