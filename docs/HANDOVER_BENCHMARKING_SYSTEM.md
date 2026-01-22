# MOSAICO Benchmarking System - Documento di Handover

## Indice
1. [Panoramica del Sistema](#1-panoramica-del-sistema)
2. [Architettura](#2-architettura)
3. [Entità e Modelli Dati](#3-entità-e-modelli-dati)
4. [Flusso di Esecuzione](#4-flusso-di-esecuzione)
5. [Sistema di Metriche](#5-sistema-di-metriche)
6. [DSL per Formule KPI](#6-dsl-per-formule-kpi)
7. [Sistema di Alerting](#7-sistema-di-alerting)
8. [Scheduling Automatico](#8-scheduling-automatico)
9. [API REST](#9-api-rest)
10. [Test Suite](#10-test-suite)
11. [Configurazione e Deploy](#11-configurazione-e-deploy)
12. [Estensioni Future](#12-estensioni-future)

---

## 1. Panoramica del Sistema

Il sistema di benchmarking MOSAICO permette di valutare le performance di agenti AI attraverso l'integrazione con **Langfuse** per la raccolta delle trace e il calcolo di metriche e KPI configurabili.

### Funzionalità Principali
- Esecuzione manuale e schedulata di benchmark
- Raccolta automatica delle trace da Langfuse
- Calcolo di metriche multiple (ROUGE, BLEU, ACCURACY, etc.)
- Aggregazione tramite formule KPI configurabili via DSL
- Sistema di alerting con notifiche multi-canale
- Storicizzazione e analisi dei risultati

### Stack Tecnologico
- **Java 17+** con Spring Boot 3.x
- **JPA/Hibernate** per la persistenza
- **Langfuse Client** per l'integrazione con il sistema di tracing
- **Spring Scheduling** per l'esecuzione automatica

---

## 2. Architettura

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           REST Controllers                               │
│   BenchmarkController │ BenchmarkRunController │ ScheduleConfigController│
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────┐
│                        Orchestration Layer                               │
│              BenchmarkOrchestrator │ BenchmarkScheduledTaskRunner        │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────┐
│                          Service Layer                                   │
│  BenchmarkRunManager │ MetricProviderRegistry │ AlertEvaluationService  │
│  LangfuseService     │ KPIFormulaDslService   │ NotificationDispatcher  │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────┐
│                        Formula/DSL Layer                                 │
│   KPIFormula │ KPIFormulaParser │ WeightedSumFormula │ AverageFormula   │
└───────────────────────────────┬─────────────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────────────┐
│                       Data/Repository Layer                              │
│  BenchmarkRepository │ BenchmarkRunRepository │ ScheduleConfigRepository│
│  Agent │ Benchmark │ BenchmarkRun │ BenchmarkResult │ ScheduleConfig    │
└─────────────────────────────────────────────────────────────────────────┘
```

### Componenti Chiave

| Componente | Responsabilità | File |
|------------|---------------|------|
| `BenchmarkOrchestrator` | Coordina l'esecuzione completa di un benchmark | `service/impl/BenchmarkOrchestratorImpl.java` |
| `BenchmarkRunManager` | Gestisce il ciclo di vita dei run | `service/impl/BenchmarkRunManagerImpl.java` |
| `MetricProviderRegistry` | Registro centralizzato dei provider di metriche | `service/impl/MetricProviderRegistryImpl.java` |
| `KPIFormulaParser` | Parsing delle formule DSL | `dsl/DefaultKPIFormulaParser.java` |
| `AlertEvaluationService` | Valutazione delle condizioni di alert | `service/impl/AlertEvaluationServiceImpl.java` |
| `BenchmarkScheduledTaskRunner` | Esecuzione schedulata | `scheduling/BenchmarkScheduledTaskRunner.java` |

---

## 3. Entità e Modelli Dati

### 3.1 Agent
Rappresenta un agente AI da valutare.

```java
@Entity
@Table(name = "agents")
public class Agent {
    private String id;
    private String name;
    private String description;
    private String llangfuseProjectName;  // Nome progetto Langfuse
    private String llangfuseUrl;          // URL Langfuse
    private String llangfuseSecretKey;    // Chiave segreta
    private String llangfusePublicKey;    // Chiave pubblica
    // ... altri campi
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Agent.java`

### 3.2 Benchmark
Definisce i criteri di valutazione.

```java
@Entity
@Table(name = "benchmarks")
public class Benchmark {
    private String id;
    private String metadata;              // JSON con nome, versione, etc.
    private String datasetRef;            // Riferimento al dataset Langfuse
    private String runName;               // Nome del run in Langfuse
    private String taskDef;               // Definizione del task
    private String features;              // Features da valutare
    private List<Agent> evaluates;        // Agenti da valutare
    private List<PerformanceKPI> measures;// KPI da calcolare
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Benchmark.java`

### 3.3 BenchmarkRun
Singola esecuzione di un benchmark.

```java
@Entity
@Table(name = "benchmark_runs")
public class BenchmarkRun {
    private String id;
    private String benchmarkId;
    private String agentId;
    private RunStatus status;             // PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    private TriggerType triggeredBy;      // MANUAL, SCHEDULED, EVENT, WEBHOOK
    private Instant startedAt;
    private Instant completedAt;
    private String errorMessage;
    private Integer tracesProcessed;
    private Integer metricsComputed;
    private Integer retryCount;
    private List<BenchmarkResult> results;

    // Metodi di lifecycle
    public void start();      // PENDING → RUNNING
    public void complete();   // RUNNING → COMPLETED
    public void fail(String errorMessage);  // RUNNING → FAILED
    public void cancel();     // RUNNING → CANCELLED
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/BenchmarkRun.java`

### 3.4 ScheduleConfig
Configurazione per l'esecuzione schedulata.

```java
@Entity
@Table(name = "schedule_configs")
public class ScheduleConfig {
    private String id;
    private String name;
    private String benchmarkId;
    private String agentId;
    private String cronExpression;        // Es: "0 0 9 * * *"
    private String timezone;              // Es: "Europe/Rome"
    private Boolean enabled;
    private Instant lastRunAt;
    private Instant nextRunAt;
    private Integer consecutiveFailures;
    private Integer maxConsecutiveFailures;
    private Boolean autoDisableOnFailure;

    // Helper methods
    public void recordRunSuccess(String runId);
    public void recordRunFailure(String runId);
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/ScheduleConfig.java`

### 3.5 Enumerazioni

```java
// Stati del run
public enum RunStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}

// Tipo di trigger
public enum TriggerType {
    MANUAL, SCHEDULED, EVENT, WEBHOOK
}

// Tipi di metriche
public enum MetricType {
    ROUGE, BLEU, ACCURACY, PRECISION, RECALL, F1_SCORE
}

// Condizioni di alert
public enum AlertCondition {
    LESS_THAN, GREATER_THAN, EQUALS, NOT_EQUALS,
    PERCENTAGE_DROP, PERCENTAGE_RISE, ANOMALY_DETECTED
}

// Severità alert
public enum Severity {
    INFO, WARNING, CRITICAL
}

// Canali di notifica
public enum NotificationChannel {
    EMAIL, SLACK, TEAMS, WEBHOOK, IN_APP
}
```

**Directory**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/enums/`

---

## 4. Flusso di Esecuzione

### 4.1 Flusso Completo di un Benchmark Run

```
1. CREAZIONE RUN
   └─ POST /api/v1/benchmark-runs
      └─ BenchmarkRunManager.createRun(benchmarkId, agentId, triggerType)
         └─ Status: PENDING

2. AVVIO ESECUZIONE
   └─ BenchmarkOrchestrator.executeBenchmarkRun(runId)
      └─ runManager.startRun(runId)
         └─ Status: RUNNING

3. VALIDAZIONE
   └─ Verifica esistenza Benchmark e Agent
      └─ Se non trovati → runManager.failRun()

4. RACCOLTA TRACES
   └─ langfuseService.getRunBenchmarkTraces(agent, datasetRef, runName)
      └─ Recupera tutte le trace dal run Langfuse

5. CALCOLO METRICHE
   └─ Per ogni trace:
      └─ Per ogni MetricProvider in registry:
         └─ provider.computeMetric(trace)
         └─ Salva MetricSnapshot

6. CALCOLO KPI
   └─ Per ogni KPIDefinition del Benchmark:
      └─ kpiFormulaDslService.parseFormula(expression)
      └─ formula.evaluate(metricValues)
      └─ Salva KPIHistory

7. VALUTAZIONE ALERT
   └─ alertEvaluationService.evaluateAlertsForRun(runId)
      └─ Verifica condizioni configurate
      └─ Se triggered → notificationDispatcher.dispatch()

8. COMPLETAMENTO
   └─ runManager.completeRun(runId, totalTraces, processedTraces)
      └─ Status: COMPLETED

GESTIONE ERRORI
   └─ Qualsiasi eccezione → runManager.failRun(runId, errorMessage)
      └─ Status: FAILED
```

### 4.2 Sequence Diagram

```
┌──────────┐    ┌────────────┐    ┌───────────┐    ┌──────────┐    ┌─────────┐
│Controller│    │Orchestrator│    │RunManager │    │Langfuse  │    │Metrics  │
└────┬─────┘    └─────┬──────┘    └─────┬─────┘    └────┬─────┘    └────┬────┘
     │                │                 │               │               │
     │ execute(runId) │                 │               │               │
     │───────────────>│                 │               │               │
     │                │ startRun()      │               │               │
     │                │────────────────>│               │               │
     │                │                 │               │               │
     │                │ getTraces()     │               │               │
     │                │────────────────────────────────>│               │
     │                │                 │    traces     │               │
     │                │<───────────────────────────────-│               │
     │                │                 │               │               │
     │                │ computeMetrics()│               │               │
     │                │────────────────────────────────────────────────>│
     │                │                 │               │    values     │
     │                │<────────────────────────────────────────────────│
     │                │                 │               │               │
     │                │ completeRun()   │               │               │
     │                │────────────────>│               │               │
     │    result      │                 │               │               │
     │<───────────────│                 │               │               │
```

---

## 5. Sistema di Metriche

### 5.1 MetricKey Interface
Interfaccia marker per identificare i tipi di metriche.

```java
public interface MetricKey {
    // Marker interface
}
```

### 5.2 MetricProvider Interface

```java
public interface MetricProvider<K extends MetricKey> {
    /**
     * Restituisce la classe della metrica gestita.
     */
    Class<K> getMetricKey();

    /**
     * Calcola il valore della metrica da una trace.
     */
    double computeMetric(TraceWithFullDetails trace);

    /**
     * Verifica se il provider supporta la trace.
     */
    boolean supports(TraceWithFullDetails trace);
}
```

### 5.3 MetricProviderRegistry

```java
public interface MetricProviderRegistry {
    /**
     * Registra un nuovo provider.
     */
    void registerProvider(MetricProvider<?> provider);

    /**
     * Ottiene tutti i provider registrati.
     */
    List<MetricProvider<?>> getAllProviders();

    /**
     * Ottiene un provider specifico per chiave.
     */
    <K extends MetricKey> Optional<MetricProvider<K>> getProvider(Class<K> metricKey);
}
```

### 5.4 Implementazione di un MetricProvider Custom

```java
@Component
public class CustomAccuracyProvider implements MetricProvider<AccuracyMetricKey> {

    @Override
    public Class<AccuracyMetricKey> getMetricKey() {
        return AccuracyMetricKey.class;
    }

    @Override
    public double computeMetric(TraceWithFullDetails trace) {
        // Logica di calcolo accuracy dalla trace
        // Es: confronto output atteso vs output generato
        return calculateAccuracy(trace);
    }

    @Override
    public boolean supports(TraceWithFullDetails trace) {
        // Verifica se la trace contiene i dati necessari
        return trace.getMetadata() != null
            && trace.getMetadata().containsKey("expected_output");
    }
}
```

---

## 6. DSL per Formule KPI

### 6.1 Sintassi Supportata

```
AVERAGE(metric1, metric2, ...)
WEIGHTED_SUM(metric1:weight1, metric2:weight2, ...)
MIN(metric1, metric2, ...)
MAX(metric1, metric2, ...)
THRESHOLD(metric, value)
```

### 6.2 Esempi

```
# Media semplice
AVERAGE(ACCURACY, PRECISION, RECALL)

# Somma pesata
WEIGHTED_SUM(ACCURACY:0.4, PRECISION:0.3, RECALL:0.3)

# Valore minimo
MIN(ROUGE, BLEU, F1_SCORE)

# Valore massimo
MAX(ACCURACY, PRECISION)

# Verifica soglia (ritorna 1.0 se >= threshold, 0.0 altrimenti)
THRESHOLD(ACCURACY, 0.8)
```

### 6.3 KPIFormula Interface

```java
@FunctionalInterface
public interface KPIFormula {
    /**
     * Valuta la formula con i valori delle metriche forniti.
     *
     * Le chiavi della mappa possono essere:
     * - Class<? extends MetricKey> per riferimenti type-safe
     * - String per nomi di metriche (usato dal DSL parser)
     */
    double evaluate(Map<?, Double> metricValues);
}
```

### 6.4 KPIFormulaParser

```java
public interface KPIFormulaParser {
    /**
     * Parsa un'espressione DSL e restituisce il risultato.
     */
    DslParseResult parse(String dslExpression);

    /**
     * Valida un'espressione senza compilarla completamente.
     */
    DslParseResult validate(String dslExpression);

    /**
     * Restituisce le metriche conosciute.
     */
    Set<String> getKnownMetricKeys();

    /**
     * Registra metriche custom.
     */
    void registerMetricKeys(Set<String> metricKeys);
}
```

### 6.5 DslParseResult

```java
public class DslParseResult {
    private final boolean success;
    private final KPIFormula formula;
    private final List<DslValidationError> errors;
    private final Set<String> referencedMetrics;

    public static DslParseResult success(KPIFormula formula, Set<String> metrics, String dsl);
    public static DslParseResult failure(List<DslValidationError> errors, String dsl);

    public boolean isSuccess();
    public KPIFormula getFormula();
    public List<DslValidationError> getErrors();
    public Set<String> getReferencedMetrics();
}
```

### 6.6 Uso Programmatico

```java
@Autowired
private KPIFormulaParser parser;

public double calculateKPI(Map<String, Double> metrics) {
    String expression = "WEIGHTED_SUM(ACCURACY:0.4, PRECISION:0.3, RECALL:0.3)";

    DslParseResult result = parser.parse(expression);

    if (result.isSuccess()) {
        return result.getFormula().evaluate(metrics);
    } else {
        throw new DslParseException(result.getErrorsAsString());
    }
}
```

### 6.7 Formule Programmatiche

Per casi complessi, è possibile usare le formule programmatiche:

```java
// AverageFormula
AverageFormula avg = new AverageFormula();
double result = avg.evaluate(metrics);

// WeightedSumFormula (richiede Class keys)
Map<Class<? extends MetricKey>, Double> weights = Map.of(
    AccuracyMetricKey.class, 0.4,
    PrecisionMetricKey.class, 0.3,
    RecallMetricKey.class, 0.3
);
WeightedSumFormula weighted = new WeightedSumFormula(weights);

// ThresholdFormula
ThresholdFormula threshold = new ThresholdFormula(
    AccuracyMetricKey.class,
    0.8,  // threshold value
    true  // greaterThan
);
```

---

## 7. Sistema di Alerting

### 7.1 Configurazione Alert

Un alert è definito da:
- **Metrica monitorata**: es. ACCURACY
- **Condizione**: es. LESS_THAN
- **Soglia**: es. 0.85
- **Severità**: INFO, WARNING, CRITICAL
- **Canali**: EMAIL, SLACK, TEAMS, WEBHOOK, IN_APP
- **Destinatari**: lista di email/webhook

### 7.2 AlertEvaluationService

```java
public interface AlertEvaluationService {
    /**
     * Valuta tutti gli alert configurati per un run.
     */
    void evaluateAlertsForRun(String runId);

    /**
     * Valuta una singola condizione.
     */
    boolean evaluateCondition(AlertCondition condition, double value, double threshold);
}
```

### 7.3 NotificationDispatcher

```java
public interface NotificationDispatcher {
    /**
     * Invia notifiche attraverso i canali configurati.
     */
    void dispatch(Alert alert, String metricName, double value);
}
```

### 7.4 Condizioni Supportate

| Condizione | Descrizione | Esempio |
|------------|-------------|---------|
| `LESS_THAN` | value < threshold | ACCURACY < 0.85 |
| `GREATER_THAN` | value > threshold | LATENCY > 1000 |
| `EQUALS` | value == threshold | STATUS == 1 |
| `NOT_EQUALS` | value != threshold | ERROR_COUNT != 0 |
| `PERCENTAGE_DROP` | Calo % rispetto a baseline | -10% da media |
| `PERCENTAGE_RISE` | Aumento % rispetto a baseline | +20% da media |
| `ANOMALY_DETECTED` | Rilevamento anomalie | Outlier statistico |

---

## 8. Scheduling Automatico

### 8.1 Configurazione

La schedulazione è gestita tramite `ScheduleConfig`:

```java
ScheduleConfig config = new ScheduleConfig(
    "Daily Quality Check",     // nome
    "bench-001",               // benchmarkId
    "agent-001",               // agentId
    "0 0 9 * * *"             // cron: ogni giorno alle 9:00
);
config.setTimezone("Europe/Rome");
config.setMaxConsecutiveFailures(3);
config.setAutoDisableOnFailure(true);
```

### 8.2 Cron Expressions

```
┌───────────── secondo (0-59)
│ ┌───────────── minuto (0-59)
│ │ ┌───────────── ora (0-23)
│ │ │ ┌───────────── giorno del mese (1-31)
│ │ │ │ ┌───────────── mese (1-12 o JAN-DEC)
│ │ │ │ │ ┌───────────── giorno della settimana (0-7 o SUN-SAT)
│ │ │ │ │ │
* * * * * *
```

**Esempi comuni:**
- `0 0 * * * *` - Ogni ora
- `0 0 0 * * *` - Ogni giorno a mezzanotte
- `0 0 9 * * MON-FRI` - Ogni giorno feriale alle 9:00
- `0 */30 * * * *` - Ogni 30 minuti
- `0 0 9,12,18 * * *` - Alle 9:00, 12:00 e 18:00

### 8.3 BenchmarkScheduledTaskRunner

```java
@Component
public class BenchmarkScheduledTaskRunner {

    @Scheduled(fixedRate = 60000) // Ogni minuto
    public void runScheduledBenchmarks() {
        // 1. Trova ScheduleConfig abilitati con nextRunAt <= now
        // 2. Per ogni config:
        //    - Crea BenchmarkRun con TriggerType.SCHEDULED
        //    - Esegue orchestrator.executeBenchmarkRunAsync()
        //    - Aggiorna config con recordRunSuccess/Failure
        //    - Calcola nuovo nextRunAt
    }
}
```

### 8.4 Gestione Fallimenti

```java
// Dopo un fallimento
config.recordRunFailure("run-id");
// - Incrementa consecutiveFailures
// - Se consecutiveFailures >= maxConsecutiveFailures e autoDisableOnFailure
//   → config.enabled = false

// Dopo un successo
config.recordRunSuccess("run-id");
// - Resetta consecutiveFailures a 0
```

---

## 9. API REST

### 9.1 BenchmarkRun Endpoints

```http
# Crea un nuovo run
POST /api/v1/benchmark-runs
{
    "benchmarkId": "bench-001",
    "agentId": "agent-001",
    "triggerType": "MANUAL"
}

# Ottiene lo stato di un run
GET /api/v1/benchmark-runs/{runId}

# Lista tutti i run di un benchmark
GET /api/v1/benchmark-runs?benchmarkId=bench-001

# Cancella un run in esecuzione
POST /api/v1/benchmark-runs/{runId}/cancel

# Riprova un run fallito
POST /api/v1/benchmark-runs/{runId}/retry
```

### 9.2 ScheduleConfig Endpoints

```http
# Crea una nuova schedulazione
POST /api/v1/schedule-configs
{
    "name": "Daily Check",
    "benchmarkId": "bench-001",
    "agentId": "agent-001",
    "cronExpression": "0 0 9 * * *",
    "timezone": "Europe/Rome"
}

# Lista schedulazioni
GET /api/v1/schedule-configs

# Abilita/disabilita
PATCH /api/v1/schedule-configs/{id}
{
    "enabled": true
}

# Elimina
DELETE /api/v1/schedule-configs/{id}
```

### 9.3 Benchmark Endpoints

```http
# Lista benchmarks
GET /api/v1/benchmarks

# Dettaglio benchmark
GET /api/v1/benchmarks/{id}

# Crea benchmark
POST /api/v1/benchmarks
{
    "metadata": "{\"name\": \"Quality Check\"}",
    "datasetRef": "dataset-123",
    "runName": "quality-run"
}
```

---

## 10. Test Suite

### 10.1 Struttura Test

```
src/test/java/it/univaq/disim/mosaico/wp2/repository/
├── controller/
│   ├── BenchmarkControllerTest.java
│   ├── BenchmarkRunControllerTest.java
│   └── ScheduleConfigControllerTest.java
├── service/impl/
│   ├── BenchmarkOrchestratorImplTest.java
│   ├── BenchmarkRunManagerImplTest.java
│   ├── AlertEvaluationServiceImplTest.java
│   └── NotificationDispatcherImplTest.java
├── dsl/
│   └── DefaultKPIFormulaParserTest.java
├── data/
│   ├── BenchmarkRunTest.java
│   └── ScheduleConfigTest.java
└── demo/
    └── BenchmarkingSystemDemo.java
```

### 10.2 Esecuzione Test

```bash
# Tutti i test
mvn test

# Test specifici
mvn test -Dtest=BenchmarkOrchestratorImplTest

# Test con coverage
mvn test jacoco:report
```

### 10.3 Demo Interattiva

```bash
# Esegui la demo completa
mvn compile exec:java \
    -Dexec.mainClass="it.univaq.disim.mosaico.wp2.repository.demo.BenchmarkingSystemDemo" \
    -Dexec.classpathScope=test
```

---

## 11. Configurazione e Deploy

### 11.1 application.properties

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/mosaico
spring.datasource.username=mosaico
spring.datasource.password=secret

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Scheduling
spring.task.scheduling.pool.size=5

# Langfuse (defaults, can be overridden per-agent)
langfuse.default.url=https://cloud.langfuse.com
```

### 11.2 Docker Compose

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mosaico
    depends_on:
      - db

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=mosaico
      - POSTGRES_USER=mosaico
      - POSTGRES_PASSWORD=secret
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

---

## 12. Estensioni Future

### 12.1 DSL Avanzato (xText)
Il parser attuale (`DefaultKPIFormulaParser`) è un placeholder. La versione completa con grammatica xText supporterà:

```
# Espressioni condizionali
IF ACCURACY > 0.9 THEN 1.0 ELSE 0.0

# Operazioni matematiche
(ROUGE + BLEU + F1_SCORE) / 3

# Funzioni composte
MIN(ACCURACY, MAX(PRECISION, RECALL)) * 100
```

### 12.2 Metriche Custom via Plugin
Sistema di plugin per aggiungere metriche custom senza modificare il codice core.

### 12.3 Dashboard e Visualizzazione
Integrazione con strumenti di visualizzazione per trend e analytics.

### 12.4 Machine Learning per Anomaly Detection
Implementazione di `ANOMALY_DETECTED` con modelli ML per rilevamento automatico.

---

## Contatti e Supporto

- **Repository**: https://github.com/your-org/mosaico-repository
- **Documentazione API**: http://localhost:8080/swagger-ui.html
- **Team**: WP2 - Agent Benchmarking

---

*Documento generato il 2026-01-22*
*Versione: 1.0*
