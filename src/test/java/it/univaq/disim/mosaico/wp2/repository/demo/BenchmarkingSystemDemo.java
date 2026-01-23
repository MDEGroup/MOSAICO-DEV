package it.univaq.disim.mosaico.wp2.repository.demo;

import it.univaq.disim.mosaico.wp2.repository.data.*;
import it.univaq.disim.mosaico.wp2.repository.data.enums.*;
import it.univaq.disim.mosaico.wp2.repository.data.formula.*;
import it.univaq.disim.mosaico.wp2.repository.dsl.DefaultKPIFormulaParser;
import it.univaq.disim.mosaico.wp2.repository.dsl.DslParseResult;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaParser;

import java.util.*;

/**
 * Demo interattiva del sistema di benchmarking MOSAICO.
 *
 * Questa classe dimostra passo-passo:
 * 1. Creazione di Benchmark e Agent
 * 2. Gestione del ciclo di vita di un BenchmarkRun
 * 3. Calcolo delle metriche
 * 4. Valutazione delle formule KPI (DSL e programmatiche)
 * 5. Sistema di alerting (tramite ScheduleConfig e condizioni)
 * 6. Scheduling automatico
 *
 * Esegui come test JUnit o come main per vedere l'output.
 */
public class BenchmarkingSystemDemo {

    private static final String SEPARATOR = "═".repeat(70);
    private static final String MINI_SEP = "─".repeat(50);

    public static void main(String[] args) {
        BenchmarkingSystemDemo demo = new BenchmarkingSystemDemo();
        demo.runFullDemo();
    }

    public void runFullDemo() {
        printHeader("MOSAICO BENCHMARKING SYSTEM - DEMO COMPLETA");

        // Step 1: Entità base
        step1_CreateEntities();

        // Step 2: Ciclo di vita BenchmarkRun
        step2_BenchmarkRunLifecycle();

        // Step 3: Sistema di metriche
        step3_MetricsSystem();

        // Step 4: Formule KPI programmatiche
        step4_ProgrammaticKPIFormulas();

        // Step 5: DSL per KPI
        step5_KPIFormulaDSL();

        // Step 6: Sistema di alerting
        step6_AlertingSystem();

        // Step 7: Scheduling
        step7_SchedulingSystem();

        // Step 8: Flusso completo end-to-end
        step8_EndToEndFlow();

        printHeader("DEMO COMPLETATA");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 1: CREAZIONE ENTITÀ BASE
    // ═══════════════════════════════════════════════════════════════════════

    private void step1_CreateEntities() {
        printStep(1, "CREAZIONE ENTITÀ BASE");

        // 1.1 Creazione Agent
        print("\n📦 1.1 Creazione di un Agent (rappresenta un agente AI da valutare):");
        Agent agent = new Agent();
        agent.setId("agent-001");
        agent.setName("CustomerServiceBot");
        agent.setDescription("Agente AI per supporto clienti");
        agent.setLlangfuseProjectName("proj-langfuse-123");
        agent.setLlangfuseUrl("https://langfuse.example.com");
        agent.setLlangfuseSecretKey("sk-xxx");
        agent.setLlangfusePublicKey("pk-xxx");

        print("   Agent creato:");
        print("   ├─ ID: " + agent.getId());
        print("   ├─ Nome: " + agent.getName());
        print("   ├─ Descrizione: " + agent.getDescription());
        print("   ├─ Langfuse Project: " + agent.getLlangfuseProjectName());
        print("   └─ Langfuse URL: " + agent.getLlangfuseUrl());

        // 1.2 Creazione Benchmark
        print("\n📊 1.2 Creazione di un Benchmark (definisce i criteri di valutazione):");
        Benchmark benchmark = new Benchmark();
        benchmark.setId("bench-001");
        benchmark.setMetadata("{\"name\": \"Customer Support Quality\", \"version\": \"1.0\"}");
        benchmark.setDatasetRef("dataset-customer-queries");
        benchmark.setRunName("cs-quality-run");
        benchmark.setTaskDef("Valuta qualità risposte supporto clienti");
        benchmark.setFeatures("accuracy,completeness,response_time");
        benchmark.setProtocolVersion("1.0");
        benchmark.setEvaluates(List.of(agent));

        print("   Benchmark creato:");
        print("   ├─ ID: " + benchmark.getId());
        print("   ├─ Dataset: " + benchmark.getDatasetRef());
        print("   ├─ Run Name: " + benchmark.getRunName());
        print("   ├─ Task Def: " + benchmark.getTaskDef());
        print("   ├─ Features: " + benchmark.getFeatures());
        print("   └─ Agents collegati: " + benchmark.getEvaluates().size());

        // 1.3 KPI Formulas
        print("\n📈 1.3 Definizione KPI per il Benchmark:");
        print("   Le formule KPI sono definite tramite DSL:");
        print("   ├─ WEIGHTED_SUM(ACCURACY:0.4, PRECISION:0.3, RECALL:0.3)");
        print("   ├─ AVERAGE(ROUGE, BLEU)");
        print("   └─ THRESHOLD(ACCURACY, 0.8)");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 2: CICLO DI VITA BENCHMARK RUN
    // ═══════════════════════════════════════════════════════════════════════

    private void step2_BenchmarkRunLifecycle() {
        printStep(2, "CICLO DI VITA BENCHMARK RUN");

        print("\n🔄 Stati possibili di un BenchmarkRun (RunStatus enum):");
        for (RunStatus status : RunStatus.values()) {
            print("   ├─ " + status.name());
        }
        print("");
        print("   Transizioni tipiche:");
        print("   PENDING → RUNNING → COMPLETED");
        print("                    → FAILED");
        print("                    → CANCELLED");

        // Creazione run
        print("\n📝 2.1 Creazione di un nuovo BenchmarkRun:");
        BenchmarkRun run = new BenchmarkRun("bench-001", "agent-001", TriggerType.MANUAL);
        run.setId("run-001");

        print("   Run creato:");
        print("   ├─ ID: " + run.getId());
        print("   ├─ Benchmark: " + run.getBenchmarkId());
        print("   ├─ Agent: " + run.getAgentId());
        print("   ├─ Trigger: " + run.getTriggeredBy());
        print("   └─ Status: " + run.getStatus());

        // Transizione a RUNNING usando il metodo start()
        print("\n▶️  2.2 Avvio del run (PENDING → RUNNING):");
        run.start();
        print("   ├─ Status: " + run.getStatus());
        print("   └─ Started at: " + run.getStartedAt());

        // Completamento usando il metodo complete()
        print("\n✅ 2.3 Completamento del run (RUNNING → COMPLETED):");
        run.setTracesProcessed(150);
        run.setMetricsComputed(450); // 150 traces * 3 metrics
        run.complete();
        print("   ├─ Status: " + run.getStatus());
        print("   ├─ Completed at: " + run.getCompletedAt());
        print("   ├─ Traces processed: " + run.getTracesProcessed());
        print("   ├─ Metrics computed: " + run.getMetricsComputed());
        print("   └─ Duration: " + run.getDurationMillis() + "ms");

        // Caso di errore
        print("\n❌ 2.4 Esempio di run fallito:");
        BenchmarkRun failedRun = new BenchmarkRun("bench-001", "agent-001", TriggerType.SCHEDULED);
        failedRun.setId("run-002");
        failedRun.start();
        failedRun.fail("Connection to Langfuse failed: timeout");
        failedRun.incrementRetry();
        print("   ├─ Status: " + failedRun.getStatus());
        print("   ├─ Error: " + failedRun.getErrorMessage());
        print("   └─ Retry count: " + failedRun.getRetryCount());

        // TriggerType
        print("\n🎯 2.5 TriggerType - Come viene attivato un run:");
        for (TriggerType tt : TriggerType.values()) {
            print("   ├─ " + tt.name());
        }

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 3: SISTEMA DI METRICHE
    // ═══════════════════════════════════════════════════════════════════════

    private void step3_MetricsSystem() {
        printStep(3, "SISTEMA DI METRICHE");

        print("\n📐 3.1 MetricType - Tipi di metriche supportate:");
        for (MetricType mt : MetricType.values()) {
            print("   ├─ " + mt.name());
        }

        print("\n📊 3.2 MetricProvider - Calcola le metriche dalle trace:");
        print("   Interface: MetricProvider<K extends MetricKey>");
        print("   Metodi:");
        print("   ├─ getMetricKey()     → Restituisce il tipo di metrica");
        print("   ├─ computeMetric()    → Calcola il valore dalla trace");
        print("   └─ supports()         → Verifica se supporta una trace");

        print("\n🗃️  3.3 MetricProviderRegistry - Gestisce tutti i provider:");
        print("   Funzionalità:");
        print("   ├─ registerProvider()    → Registra un nuovo provider");
        print("   ├─ getAllProviders()     → Ottiene tutti i provider");
        print("   └─ getProvider(key)      → Ottiene provider per chiave");

        // Simulazione calcolo metriche
        print("\n🔢 3.4 Esempio di calcolo metriche per una trace:");
        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("ACCURACY", 0.92);
        metrics.put("PRECISION", 0.88);
        metrics.put("RECALL", 0.90);
        metrics.put("F1_SCORE", 0.89);
        metrics.put("ROUGE", 0.85);
        metrics.put("BLEU", 0.78);

        print("   Metriche calcolate dalla trace:");
        metrics.forEach((key, value) ->
            print("   ├─ " + key + ": " + value));

        // BenchmarkResult
        print("\n💾 3.5 BenchmarkResult - Persistenza dei risultati:");
        print("   Ogni risultato contiene:");
        print("   ├─ benchmarkRun     → Riferimento al run");
        print("   ├─ traceId          → ID della trace Langfuse");
        print("   ├─ metricType       → Tipo di metrica");
        print("   ├─ metricValue      → Valore calcolato");
        print("   └─ computedAt       → Timestamp del calcolo");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 4: FORMULE KPI PROGRAMMATICHE
    // ═══════════════════════════════════════════════════════════════════════

    private void step4_ProgrammaticKPIFormulas() {
        printStep(4, "FORMULE KPI PROGRAMMATICHE");

        print("\n🧮 4.1 KPIFormula Interface:");
        print("   @FunctionalInterface");
        print("   public interface KPIFormula {");
        print("       double evaluate(Map<?, Double> metricValues);");
        print("   }");
        print("   ");
        print("   Supporta chiavi di tipo:");
        print("   ├─ Class<? extends MetricKey>  → Type-safe references");
        print("   └─ String                       → Nome metrica (per DSL)");

        // Dati di test
        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("ACCURACY", 0.92);
        metrics.put("PRECISION", 0.88);
        metrics.put("RECALL", 0.90);

        print("\n📊 Metriche di input per gli esempi:");
        metrics.forEach((k, v) -> print("   ├─ " + k + ": " + v));

        // AverageFormula
        print("\n📈 4.2 AverageFormula - Media semplice:");
        AverageFormula avgFormula = new AverageFormula();
        double avgResult = avgFormula.evaluate(metrics);
        print("   Formula: (ACCURACY + PRECISION + RECALL) / 3");
        print("   Calcolo: (" + metrics.get("ACCURACY") + " + " +
              metrics.get("PRECISION") + " + " + metrics.get("RECALL") + ") / 3");
        print("   Risultato: " + String.format("%.4f", avgResult));

        // WeightedSumFormula (simulata con calcolo diretto per String keys)
        print("\n⚖️  4.3 WeightedSumFormula - Somma pesata:");
        print("   Formula: 0.4*ACCURACY + 0.3*PRECISION + 0.3*RECALL");
        double weightedResult = 0.4 * metrics.get("ACCURACY") +
                               0.3 * metrics.get("PRECISION") +
                               0.3 * metrics.get("RECALL");
        print("   Calcolo: 0.4*" + metrics.get("ACCURACY") + " + 0.3*" +
              metrics.get("PRECISION") + " + 0.3*" + metrics.get("RECALL"));
        print("   Risultato: " + String.format("%.4f", weightedResult));

        // ThresholdFormula
        print("\n🎯 4.4 ThresholdFormula - Verifica soglia:");
        print("   Formula: ACCURACY > 0.9 ? 1.0 : 0.0");
        double thresholdResult = metrics.get("ACCURACY") > 0.9 ? 1.0 : 0.0;
        print("   Calcolo: " + metrics.get("ACCURACY") + " > 0.9 → " +
              (metrics.get("ACCURACY") > 0.9 ? "TRUE" : "FALSE"));
        print("   Risultato: " + thresholdResult);

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 5: DSL PER KPI
    // ═══════════════════════════════════════════════════════════════════════

    private void step5_KPIFormulaDSL() {
        printStep(5, "DSL (Domain Specific Language) PER KPI");

        print("\n📝 5.1 Sintassi DSL supportata (DefaultKPIFormulaParser):");
        print("   ┌────────────────────────────────────────────────────────────┐");
        print("   │ AVERAGE(metric1, metric2, ...)                             │");
        print("   │ WEIGHTED_SUM(metric1:weight1, metric2:weight2, ...)        │");
        print("   │ MIN(metric1, metric2, ...)                                 │");
        print("   │ MAX(metric1, metric2, ...)                                 │");
        print("   │ THRESHOLD(metric, value)                                   │");
        print("   └────────────────────────────────────────────────────────────┘");

        // Parser DSL
        KPIFormulaParser parser = new DefaultKPIFormulaParser();
        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("ACCURACY", 0.92);
        metrics.put("PRECISION", 0.88);
        metrics.put("RECALL", 0.90);
        metrics.put("F1_SCORE", 0.89);
        metrics.put("ROUGE", 0.85);
        metrics.put("BLEU", 0.78);

        print("\n📊 Metriche disponibili (MetricType enum):");
        metrics.forEach((k, v) -> print("   ├─ " + k + ": " + v));

        // Esempio 1: AVERAGE
        print("\n🔹 5.2 Esempio AVERAGE:");
        String expr1 = "AVERAGE(ACCURACY, PRECISION)";
        print("   Espressione: " + expr1);
        DslParseResult result1 = parser.parse(expr1);
        if (result1.isSuccess()) {
            KPIFormula formula1 = result1.getFormula();
            double value1 = formula1.evaluate(metrics);
            print("   Metriche referenziate: " + result1.getReferencedMetrics());
            print("   Risultato: " + String.format("%.4f", value1));
        } else {
            print("   Errori: " + result1.getErrorsAsString());
        }

        // Esempio 2: WEIGHTED_SUM
        print("\n🔹 5.3 Esempio WEIGHTED_SUM:");
        String expr2 = "WEIGHTED_SUM(ACCURACY:0.5, PRECISION:0.3, RECALL:0.2)";
        print("   Espressione: " + expr2);
        DslParseResult result2 = parser.parse(expr2);
        if (result2.isSuccess()) {
            KPIFormula formula2 = result2.getFormula();
            double value2 = formula2.evaluate(metrics);
            print("   Metriche referenziate: " + result2.getReferencedMetrics());
            print("   Risultato: " + String.format("%.4f", value2));
        }

        // Esempio 3: MIN/MAX
        print("\n🔹 5.4 Esempio MIN e MAX:");
        String expr3min = "MIN(ACCURACY, PRECISION, RECALL)";
        String expr3max = "MAX(ACCURACY, PRECISION, RECALL)";

        DslParseResult result3min = parser.parse(expr3min);
        if (result3min.isSuccess()) {
            print("   " + expr3min);
            print("   Risultato MIN: " + String.format("%.4f", result3min.getFormula().evaluate(metrics)));
        }

        DslParseResult result3max = parser.parse(expr3max);
        if (result3max.isSuccess()) {
            print("   " + expr3max);
            print("   Risultato MAX: " + String.format("%.4f", result3max.getFormula().evaluate(metrics)));
        }

        // Esempio 4: THRESHOLD
        print("\n🔹 5.5 Esempio THRESHOLD:");
        String expr4 = "THRESHOLD(ACCURACY, 0.9)";
        print("   " + expr4 + " (ACCURACY >= 0.9?)");
        DslParseResult result4 = parser.parse(expr4);
        if (result4.isSuccess()) {
            double threshResult = result4.getFormula().evaluate(metrics);
            print("   Risultato: " + threshResult + " (1.0 = TRUE, 0.0 = FALSE)");
        }

        // Gestione errori
        print("\n⚠️  5.6 Gestione errori DSL:");
        String invalidExpr = "INVALID_FUNCTION(ACCURACY)";
        print("   Espressione non valida: " + invalidExpr);
        DslParseResult invalidResult = parser.parse(invalidExpr);
        if (!invalidResult.isSuccess()) {
            print("   Errori: " + invalidResult.getErrors().get(0).getMessage());
        }

        // Metriche conosciute
        print("\n📋 5.7 Metriche conosciute dal parser:");
        Set<String> knownMetrics = parser.getKnownMetricKeys();
        print("   " + knownMetrics);

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 6: SISTEMA DI ALERTING
    // ═══════════════════════════════════════════════════════════════════════

    private void step6_AlertingSystem() {
        printStep(6, "SISTEMA DI ALERTING");

        print("\n🚨 6.1 AlertCondition - Condizioni disponibili:");
        for (AlertCondition cond : AlertCondition.values()) {
            print("   ├─ " + cond.name());
        }

        print("\n📊 6.2 Severity - Livelli di gravità:");
        for (Severity sev : Severity.values()) {
            print("   ├─ " + sev.name());
        }

        print("\n📬 6.3 NotificationChannel - Canali di notifica:");
        for (NotificationChannel ch : NotificationChannel.values()) {
            print("   ├─ " + ch.name());
        }

        // Simulazione di una configurazione alert
        print("\n🔔 6.4 Esempio configurazione Alert:");
        print("   ┌────────────────────────────────────────────────────────────┐");
        print("   │ Alert: 'Low Accuracy Alert'                                │");
        print("   │ Condizione: ACCURACY < 0.85                                │");
        print("   │ Severità: WARNING                                          │");
        print("   │ Canali: [EMAIL, SLACK]                                     │");
        print("   │ Destinatari: [team@example.com]                            │");
        print("   └────────────────────────────────────────────────────────────┘");

        // Simulazione valutazione
        print("\n🔍 6.5 Esempio di valutazione alert:");
        double currentAccuracy = 0.82;
        double threshold = 0.85;
        print("   Valore attuale ACCURACY: " + currentAccuracy);
        print("   Soglia: " + threshold);
        print("   Condizione: LESS_THAN");
        boolean triggered = currentAccuracy < threshold;
        print("   Alert triggered: " + (triggered ? "✅ SÌ" : "❌ NO"));

        if (triggered) {
            print("\n   📧 Notifiche inviate a:");
            print("      ├─ EMAIL → team@example.com");
            print("      └─ SLACK → #benchmark-alerts");
        }

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 7: SISTEMA DI SCHEDULING
    // ═══════════════════════════════════════════════════════════════════════

    private void step7_SchedulingSystem() {
        printStep(7, "SISTEMA DI SCHEDULING");

        print("\n⏰ 7.1 ScheduleConfig Entity:");
        print("   Entità JPA per configurare l'esecuzione schedulata dei benchmark.");
        print("   ");
        print("   Campi principali:");
        print("   ├─ name                    → Nome della schedulazione");
        print("   ├─ benchmarkId             → ID del benchmark da eseguire");
        print("   ├─ agentId                 → ID dell'agent da valutare");
        print("   ├─ cronExpression          → Espressione cron");
        print("   ├─ timezone                → Fuso orario (default: UTC)");
        print("   ├─ enabled                 → Abilitato/disabilitato");
        print("   ├─ lastRunAt               → Ultima esecuzione");
        print("   ├─ nextRunAt               → Prossima esecuzione");
        print("   ├─ consecutiveFailures     → Fallimenti consecutivi");
        print("   └─ autoDisableOnFailure    → Disabilita dopo N fallimenti");

        print("\n📅 7.2 Cron Expressions supportate:");
        print("   ┌───────────────────────────────────────────────────────────┐");
        print("   │ Campo     │ Valori permessi                               │");
        print("   ├───────────┼───────────────────────────────────────────────┤");
        print("   │ Secondi   │ 0-59                                          │");
        print("   │ Minuti    │ 0-59                                          │");
        print("   │ Ore       │ 0-23                                          │");
        print("   │ Giorno    │ 1-31                                          │");
        print("   │ Mese      │ 1-12 o JAN-DEC                                │");
        print("   │ Weekday   │ 0-7 o SUN-SAT                                 │");
        print("   └───────────────────────────────────────────────────────────┘");

        print("\n📋 7.3 Esempi di scheduling:");
        print("   ├─ '0 0 * * * *'      → Ogni ora");
        print("   ├─ '0 0 0 * * *'      → Ogni giorno a mezzanotte");
        print("   ├─ '0 0 9 * * MON'    → Ogni lunedì alle 9:00");
        print("   ├─ '0 */30 * * * *'   → Ogni 30 minuti");
        print("   └─ '0 0 9-17 * * *'   → Ogni ora dalle 9 alle 17");

        // Creazione ScheduleConfig
        print("\n⚙️  7.4 Esempio creazione ScheduleConfig:");
        ScheduleConfig config = new ScheduleConfig(
            "Daily Quality Check",
            "bench-001",
            "agent-001",
            "0 0 9 * * *"
        );
        config.setDescription("Esegue controllo qualità ogni giorno alle 9:00");
        config.setTimezone("Europe/Rome");
        config.setMaxConsecutiveFailures(5);

        print("   ScheduleConfig creato:");
        print("   ├─ Nome: " + config.getName());
        print("   ├─ Benchmark ID: " + config.getBenchmarkId());
        print("   ├─ Agent ID: " + config.getAgentId());
        print("   ├─ Cron: " + config.getCronExpression());
        print("   ├─ Timezone: " + config.getTimezone());
        print("   ├─ Enabled: " + config.getEnabled());
        print("   ├─ Max consecutive failures: " + config.getMaxConsecutiveFailures());
        print("   └─ Auto-disable on failure: " + config.getAutoDisableOnFailure());

        // Simulazione run success/failure
        print("\n📈 7.5 Gestione risultati run:");
        print("   Dopo un run completato con successo:");
        config.recordRunSuccess("run-123");
        print("   ├─ Last Run ID: " + config.getLastRunId());
        print("   ├─ Last Run Status: " + config.getLastRunStatus());
        print("   ├─ Run Count: " + config.getRunCount());
        print("   └─ Consecutive Failures: " + config.getConsecutiveFailures());

        print("\n   Dopo un run fallito:");
        config.recordRunFailure("run-124");
        print("   ├─ Last Run Status: " + config.getLastRunStatus());
        print("   ├─ Run Count: " + config.getRunCount());
        print("   ├─ Failure Count: " + config.getFailureCount());
        print("   └─ Consecutive Failures: " + config.getConsecutiveFailures());

        print("\n🔄 7.6 Flusso di esecuzione schedulata:");
        print("   1. BenchmarkScheduledTaskRunner.runScheduledBenchmarks() invocato");
        print("   2. Cerca ScheduleConfig con enabled=true e nextRunAt <= now");
        print("   3. Per ogni config, crea BenchmarkRun con TriggerType.SCHEDULED");
        print("   4. Chiama orchestrator.executeBenchmarkRunAsync()");
        print("   5. Aggiorna ScheduleConfig con recordRunSuccess/Failure");
        print("   6. Calcola nextRunAt basandosi sulla cron expression");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STEP 8: FLUSSO END-TO-END
    // ═══════════════════════════════════════════════════════════════════════

    private void step8_EndToEndFlow() {
        printStep(8, "FLUSSO COMPLETO END-TO-END");

        print("\n🚀 Simulazione di un benchmark run completo:");
        print("");

        // Setup
        print("┌─ SETUP ─────────────────────────────────────────────────────┐");
        print("│ Agent: CustomerServiceBot (agent-001)                       │");
        print("│ Benchmark: Customer Support Quality (bench-001)             │");
        print("│ KPI: Overall Quality = WEIGHTED_SUM(ACC:0.4, PREC:0.3, REC:0.3)│");
        print("│ Alert: ACCURACY < 0.85 → WARNING                            │");
        print("└──────────────────────────────────────────────────────────────┘");

        // Step 1: Creazione Run
        print("\n📌 FASE 1: Creazione BenchmarkRun");
        print("   → POST /api/v1/benchmark-runs");
        print("   → Body: { benchmarkId: 'bench-001', triggerType: 'MANUAL' }");
        BenchmarkRun run = new BenchmarkRun("bench-001", "agent-001", TriggerType.MANUAL);
        run.setId("run-" + System.currentTimeMillis());
        print("   ← Response: { id: '" + run.getId() + "', status: '" + run.getStatus() + "' }");

        // Step 2: Esecuzione
        print("\n📌 FASE 2: Esecuzione Benchmark");
        print("   → runManager.startRun('" + run.getId() + "')");
        run.start();
        print("   ← Status: " + run.getStatus());

        // Step 3: Raccolta traces
        print("\n📌 FASE 3: Raccolta Traces da Langfuse");
        print("   → langfuseService.getRunBenchmarkTraces(agent, datasetRef, runName)");
        int tracesCount = 150;
        print("   ← Trovate " + tracesCount + " traces");

        // Step 4: Calcolo metriche
        print("\n📌 FASE 4: Calcolo Metriche per ogni Trace");
        print("   → metricProviderRegistry.getAllProviders()");
        print("   → Per ogni trace e provider: provider.computeMetric(trace)");

        // Simulazione metriche aggregate
        Map<String, Double> aggregatedMetrics = new LinkedHashMap<>();
        aggregatedMetrics.put("ACCURACY", 0.89);
        aggregatedMetrics.put("PRECISION", 0.91);
        aggregatedMetrics.put("RECALL", 0.87);

        print("   ← Metriche aggregate (media di " + tracesCount + " traces):");
        aggregatedMetrics.forEach((k, v) ->
            print("      ├─ " + k + ": " + String.format("%.2f", v)));

        // Step 5: Calcolo KPI
        print("\n📌 FASE 5: Calcolo KPI");
        String kpiFormula = "WEIGHTED_SUM(ACCURACY:0.4, PRECISION:0.3, RECALL:0.3)";
        print("   → kpiFormulaParser.parse('" + kpiFormula + "')");
        KPIFormulaParser parser = new DefaultKPIFormulaParser();
        DslParseResult parseResult = parser.parse(kpiFormula);
        double kpiValue = 0.0;
        if (parseResult.isSuccess()) {
            kpiValue = parseResult.getFormula().evaluate(aggregatedMetrics);
            print("   ← KPI 'Overall Quality': " + String.format("%.4f", kpiValue));
        }

        // Step 6: Valutazione Alert
        print("\n📌 FASE 6: Valutazione Alert");
        print("   → alertEvaluationService.evaluateAlertsForRun('" + run.getId() + "')");
        double accuracy = aggregatedMetrics.get("ACCURACY");
        boolean alertTriggered = accuracy < 0.85;
        print("   ← ACCURACY (" + accuracy + ") < 0.85? " +
              (alertTriggered ? "SÌ → Alert inviato!" : "NO → Nessun alert"));

        // Step 7: Completamento
        print("\n📌 FASE 7: Completamento Run");
        print("   → runManager.completeRun('" + run.getId() + "', " + tracesCount + ", " + (tracesCount * 3) + ")");
        run.setTracesProcessed(tracesCount);
        run.setMetricsComputed(tracesCount * 3);
        run.complete();
        print("   ← Status: " + run.getStatus());

        // Riepilogo finale
        print("\n┌─ RIEPILOGO FINALE ──────────────────────────────────────────┐");
        print("│ Run ID: " + padRight(run.getId(), 50) + "│");
        print("│ Status: " + padRight(run.getStatus().toString(), 50) + "│");
        print("│ Duration: " + padRight(run.getDurationMillis() + "ms", 48) + "│");
        print("│ Traces: " + padRight(run.getTracesProcessed() + " processed", 50) + "│");
        print("│ Metrics: " + padRight(run.getMetricsComputed() + " computed", 49) + "│");
        print("│ KPI Score: " + padRight(String.format("%.4f", kpiValue), 47) + "│");
        print("│ Alerts: " + padRight(alertTriggered ? "1 triggered" : "0 triggered", 50) + "│");
        print("└──────────────────────────────────────────────────────────────┘");

        printEndStep();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═══════════════════════════════════════════════════════════════════════

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

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
