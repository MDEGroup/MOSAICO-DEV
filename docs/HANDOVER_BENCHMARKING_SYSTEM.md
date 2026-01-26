# MOSAICO Benchmarking System - Handover Document

## Table of Contents
1. [System Overview](#1-system-overview)
2. [Architecture](#2-architecture)
3. [Entities and Data Models](#3-entities-and-data-models)
4. [Execution Flow](#4-execution-flow)
5. [Metrics System](#5-metrics-system)
6. [DSL for KPI Formulas](#6-dsl-for-kpi-formulas)
7. [Alerting System](#7-alerting-system)
8. [Automatic Scheduling](#8-automatic-scheduling)
9. [REST API](#9-rest-api)
10. [Test Suite](#10-test-suite)
11. [Configuration and Deployment](#11-configuration-and-deployment)
12. [Future Extensions](#12-future-extensions)

---

## 1. System Overview

The MOSAICO benchmarking system allows evaluating AI agent performance through integration with **Langfuse** for trace collection and calculation of configurable metrics and KPIs.

### Main Features
- Manual and scheduled benchmark execution
- Automatic trace collection from Langfuse
- Multiple metrics calculation (ROUGE, BLEU, ACCURACY, etc.)
- Aggregation through configurable KPI formulas via DSL
- Alerting system with multi-channel notifications
- Results historization and analysis

### Technology Stack
- **Java 17+** with Spring Boot 3.x
- **JPA/Hibernate** for persistence
- **Langfuse Client** for tracing system integration
- **Spring Scheduling** for automatic execution

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

### Key Components

| Component | Responsibility | File |
|-----------|---------------|------|
| `BenchmarkOrchestrator` | Coordinates complete benchmark execution | `service/impl/BenchmarkOrchestratorImpl.java` |
| `BenchmarkRunManager` | Manages run lifecycle | `service/impl/BenchmarkRunManagerImpl.java` |
| `MetricProviderRegistry` | Centralized registry of metric providers | `service/impl/MetricProviderRegistryImpl.java` |
| `KPIFormulaParser` | DSL formula parsing | `dsl/DefaultKPIFormulaParser.java` |
| `AlertEvaluationService` | Alert condition evaluation | `service/impl/AlertEvaluationServiceImpl.java` |
| `BenchmarkScheduledTaskRunner` | Scheduled execution | `scheduling/BenchmarkScheduledTaskRunner.java` |

---

## 3. Entities and Data Models

### 3.1 Agent
Represents an AI agent to be evaluated.

```java
@Entity
@Table(name = "agents")
public class Agent {
    private String id;
    private String name;
    private String description;
    private String llangfuseProjectName;  // Langfuse project name
    private String llangfuseUrl;          // Langfuse URL
    private String llangfuseSecretKey;    // Secret key
    private String llangfusePublicKey;    // Public key
    // ... other fields
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Agent.java`

### 3.2 Benchmark
Defines evaluation criteria.

```java
@Entity
@Table(name = "benchmarks")
public class Benchmark {
    private String id;
    private String metadata;              // JSON with name, version, etc.
    private String datasetRef;            // Langfuse dataset reference
    private String runName;               // Run name in Langfuse
    private String taskDef;               // Task definition
    private String features;              // Features to evaluate
    private List<Agent> evaluates;        // Agents to evaluate
    private List<PerformanceKPI> measures;// KPIs to calculate
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Benchmark.java`

### 3.3 BenchmarkRun
Single benchmark execution.

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

    // Lifecycle methods
    public void start();      // PENDING → RUNNING
    public void complete();   // RUNNING → COMPLETED
    public void fail(String errorMessage);  // RUNNING → FAILED
    public void cancel();     // RUNNING → CANCELLED
}
```

**File**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/BenchmarkRun.java`

### 3.4 ScheduleConfig
Configuration for scheduled execution.

```java
@Entity
@Table(name = "schedule_configs")
public class ScheduleConfig {
    private String id;
    private String name;
    private String benchmarkId;
    private String agentId;
    private String cronExpression;        // E.g.: "0 0 9 * * *"
    private String timezone;              // E.g.: "Europe/Rome"
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

### 3.5 Enumerations

```java
// Run states
public enum RunStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}

// Trigger type
public enum TriggerType {
    MANUAL, SCHEDULED, EVENT, WEBHOOK
}

// Metric types
public enum MetricType {
    ROUGE, BLEU, ACCURACY, PRECISION, RECALL, F1_SCORE
}

// Alert conditions
public enum AlertCondition {
    LESS_THAN, GREATER_THAN, EQUALS, NOT_EQUALS,
    PERCENTAGE_DROP, PERCENTAGE_RISE, ANOMALY_DETECTED
}

// Alert severity
public enum Severity {
    INFO, WARNING, CRITICAL
}

// Notification channels
public enum NotificationChannel {
    EMAIL, SLACK, TEAMS, WEBHOOK, IN_APP
}
```

**Directory**: `src/main/java/it/univaq/disim/mosaico/wp2/repository/data/enums/`

---

## 4. Execution Flow

### 4.1 Complete Benchmark Run Flow

```
1. RUN CREATION
   └─ POST /api/v1/benchmark-runs
      └─ BenchmarkRunManager.createRun(benchmarkId, agentId, triggerType)
         └─ Status: PENDING

2. EXECUTION START
   └─ BenchmarkOrchestrator.executeBenchmarkRun(runId)
      └─ runManager.startRun(runId)
         └─ Status: RUNNING

3. VALIDATION
   └─ Verify Benchmark and Agent existence
      └─ If not found → runManager.failRun()

4. TRACE COLLECTION
   └─ langfuseService.getRunBenchmarkTraces(agent, datasetRef, runName)
      └─ Retrieves all traces from Langfuse run

5. METRICS CALCULATION
   └─ For each trace:
      └─ For each MetricProvider in registry:
         └─ provider.computeMetric(trace)
         └─ Save MetricSnapshot

6. KPI CALCULATION
   └─ For each KPIDefinition of the Benchmark:
      └─ kpiFormulaDslService.parseFormula(expression)
      └─ formula.evaluate(metricValues)
      └─ Save KPIHistory

7. ALERT EVALUATION
   └─ alertEvaluationService.evaluateAlertsForRun(runId)
      └─ Verify configured conditions
      └─ If triggered → notificationDispatcher.dispatch()

8. COMPLETION
   └─ runManager.completeRun(runId, totalTraces, processedTraces)
      └─ Status: COMPLETED

ERROR HANDLING
   └─ Any exception → runManager.failRun(runId, errorMessage)
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

## 5. Metrics System

### 5.1 MetricKey Interface
Marker interface to identify metric types.

```java
public interface MetricKey {
    // Marker interface
}
```

### 5.2 MetricProvider Interface

```java
public interface MetricProvider<K extends MetricKey> {
    /**
     * Returns the class of the managed metric.
     */
    Class<K> getMetricKey();

    /**
     * Calculates the metric value from a trace.
     */
    double computeMetric(TraceWithFullDetails trace);

    /**
     * Verifies if the provider supports the trace.
     */
    boolean supports(TraceWithFullDetails trace);
}
```

### 5.3 MetricProviderRegistry

```java
public interface MetricProviderRegistry {
    /**
     * Registers a new provider.
     */
    void registerProvider(MetricProvider<?> provider);

    /**
     * Gets all registered providers.
     */
    List<MetricProvider<?>> getAllProviders();

    /**
     * Gets a specific provider by key.
     */
    <K extends MetricKey> Optional<MetricProvider<K>> getProvider(Class<K> metricKey);
}
```

### 5.4 Custom MetricProvider Implementation

```java
@Component
public class CustomAccuracyProvider implements MetricProvider<AccuracyMetricKey> {

    @Override
    public Class<AccuracyMetricKey> getMetricKey() {
        return AccuracyMetricKey.class;
    }

    @Override
    public double computeMetric(TraceWithFullDetails trace) {
        // Accuracy calculation logic from trace
        // E.g.: compare expected output vs generated output
        return calculateAccuracy(trace);
    }

    @Override
    public boolean supports(TraceWithFullDetails trace) {
        // Verify if the trace contains necessary data
        return trace.getMetadata() != null
            && trace.getMetadata().containsKey("expected_output");
    }
}
```

---

## 6. DSL for KPI Formulas

### 6.1 Supported Syntax

```
AVERAGE(metric1, metric2, ...)
WEIGHTED_SUM(metric1:weight1, metric2:weight2, ...)
MIN(metric1, metric2, ...)
MAX(metric1, metric2, ...)
THRESHOLD(metric, value)
```

### 6.2 Examples

```
# Simple average
AVERAGE(ACCURACY, PRECISION, RECALL)

# Weighted sum
WEIGHTED_SUM(ACCURACY:0.4, PRECISION:0.3, RECALL:0.3)

# Minimum value
MIN(ROUGE, BLEU, F1_SCORE)

# Maximum value
MAX(ACCURACY, PRECISION)

# Threshold check (returns 1.0 if >= threshold, 0.0 otherwise)
THRESHOLD(ACCURACY, 0.8)
```

### 6.3 KPIFormula Interface

```java
@FunctionalInterface
public interface KPIFormula {
    /**
     * Evaluates the formula with the provided metric values.
     *
     * Map keys can be:
     * - Class<? extends MetricKey> for type-safe references
     * - String for metric names (used by DSL parser)
     */
    double evaluate(Map<?, Double> metricValues);
}
```

### 6.4 KPIFormulaParser

```java
public interface KPIFormulaParser {
    /**
     * Parses a DSL expression and returns the result.
     */
    DslParseResult parse(String dslExpression);

    /**
     * Validates an expression without fully compiling it.
     */
    DslParseResult validate(String dslExpression);

    /**
     * Returns known metrics.
     */
    Set<String> getKnownMetricKeys();

    /**
     * Registers custom metrics.
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

### 6.6 Programmatic Usage

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

### 6.7 Programmatic Formulas

For complex cases, you can use programmatic formulas:

```java
// AverageFormula
AverageFormula avg = new AverageFormula();
double result = avg.evaluate(metrics);

// WeightedSumFormula (requires Class keys)
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

## 7. Alerting System

### 7.1 Alert Configuration

An alert is defined by:
- **Monitored metric**: e.g. ACCURACY
- **Condition**: e.g. LESS_THAN
- **Threshold**: e.g. 0.85
- **Severity**: INFO, WARNING, CRITICAL
- **Channels**: EMAIL, SLACK, TEAMS, WEBHOOK, IN_APP
- **Recipients**: list of emails/webhooks

### 7.2 AlertEvaluationService

```java
public interface AlertEvaluationService {
    /**
     * Evaluates all alerts configured for a run.
     */
    void evaluateAlertsForRun(String runId);

    /**
     * Evaluates a single condition.
     */
    boolean evaluateCondition(AlertCondition condition, double value, double threshold);
}
```

### 7.3 NotificationDispatcher

```java
public interface NotificationDispatcher {
    /**
     * Sends notifications through configured channels.
     */
    void dispatch(Alert alert, String metricName, double value);
}
```

### 7.4 Supported Conditions

| Condition | Description | Example |
|-----------|-------------|---------|
| `LESS_THAN` | value < threshold | ACCURACY < 0.85 |
| `GREATER_THAN` | value > threshold | LATENCY > 1000 |
| `EQUALS` | value == threshold | STATUS == 1 |
| `NOT_EQUALS` | value != threshold | ERROR_COUNT != 0 |
| `PERCENTAGE_DROP` | % drop from baseline | -10% from average |
| `PERCENTAGE_RISE` | % increase from baseline | +20% from average |
| `ANOMALY_DETECTED` | Anomaly detection | Statistical outlier |

---

## 8. Automatic Scheduling

### 8.1 Configuration

Scheduling is managed through `ScheduleConfig`:

```java
ScheduleConfig config = new ScheduleConfig(
    "Daily Quality Check",     // name
    "bench-001",               // benchmarkId
    "agent-001",               // agentId
    "0 0 9 * * *"             // cron: every day at 9:00
);
config.setTimezone("Europe/Rome");
config.setMaxConsecutiveFailures(3);
config.setAutoDisableOnFailure(true);
```

### 8.2 Cron Expressions

```
┌───────────── second (0-59)
│ ┌───────────── minute (0-59)
│ │ ┌───────────── hour (0-23)
│ │ │ ┌───────────── day of month (1-31)
│ │ │ │ ┌───────────── month (1-12 or JAN-DEC)
│ │ │ │ │ ┌───────────── day of week (0-7 or SUN-SAT)
│ │ │ │ │ │
* * * * * *
```

**Common examples:**
- `0 0 * * * *` - Every hour
- `0 0 0 * * *` - Every day at midnight
- `0 0 9 * * MON-FRI` - Every weekday at 9:00
- `0 */30 * * * *` - Every 30 minutes
- `0 0 9,12,18 * * *` - At 9:00, 12:00, and 18:00

### 8.3 BenchmarkScheduledTaskRunner

```java
@Component
public class BenchmarkScheduledTaskRunner {

    @Scheduled(fixedRate = 60000) // Every minute
    public void runScheduledBenchmarks() {
        // 1. Find enabled ScheduleConfigs with nextRunAt <= now
        // 2. For each config:
        //    - Create BenchmarkRun with TriggerType.SCHEDULED
        //    - Execute orchestrator.executeBenchmarkRunAsync()
        //    - Update config with recordRunSuccess/Failure
        //    - Calculate new nextRunAt
    }
}
```

### 8.4 Failure Handling

```java
// After a failure
config.recordRunFailure("run-id");
// - Increments consecutiveFailures
// - If consecutiveFailures >= maxConsecutiveFailures and autoDisableOnFailure
//   → config.enabled = false

// After a success
config.recordRunSuccess("run-id");
// - Resets consecutiveFailures to 0
```

---

## 9. REST API

### 9.1 BenchmarkRun Endpoints

```http
# Create a new run
POST /api/v1/benchmark-runs
{
    "benchmarkId": "bench-001",
    "agentId": "agent-001",
    "triggerType": "MANUAL"
}

# Get run status
GET /api/v1/benchmark-runs/{runId}

# List all runs of a benchmark
GET /api/v1/benchmark-runs?benchmarkId=bench-001

# Cancel a running run
POST /api/v1/benchmark-runs/{runId}/cancel

# Retry a failed run
POST /api/v1/benchmark-runs/{runId}/retry
```

### 9.2 ScheduleConfig Endpoints

```http
# Create a new schedule
POST /api/v1/schedule-configs
{
    "name": "Daily Check",
    "benchmarkId": "bench-001",
    "agentId": "agent-001",
    "cronExpression": "0 0 9 * * *",
    "timezone": "Europe/Rome"
}

# List schedules
GET /api/v1/schedule-configs

# Enable/disable
PATCH /api/v1/schedule-configs/{id}
{
    "enabled": true
}

# Delete
DELETE /api/v1/schedule-configs/{id}
```

### 9.3 Benchmark Endpoints

```http
# List benchmarks
GET /api/v1/benchmarks

# Benchmark details
GET /api/v1/benchmarks/{id}

# Create benchmark
POST /api/v1/benchmarks
{
    "metadata": "{\"name\": \"Quality Check\"}",
    "datasetRef": "dataset-123",
    "runName": "quality-run"
}
```

---

## 10. Test Suite

### 10.1 Test Structure

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

### 10.2 Running Tests

```bash
# All tests
mvn test

# Specific tests
mvn test -Dtest=BenchmarkOrchestratorImplTest

# Tests with coverage
mvn test jacoco:report
```

### 10.3 Interactive Demo

```bash
# Run the complete demo
mvn compile exec:java \
    -Dexec.mainClass="it.univaq.disim.mosaico.wp2.repository.demo.BenchmarkingSystemDemo" \
    -Dexec.classpathScope=test
```

---

## 11. Configuration and Deployment

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

## 12. Future Extensions

### 12.1 Advanced DSL (xText)
The current parser (`DefaultKPIFormulaParser`) is a placeholder. The complete version with xText grammar will support:

```
# Conditional expressions
IF ACCURACY > 0.9 THEN 1.0 ELSE 0.0

# Mathematical operations
(ROUGE + BLEU + F1_SCORE) / 3

# Composite functions
MIN(ACCURACY, MAX(PRECISION, RECALL)) * 100
```

### 12.2 Custom Metrics via Plugin
Plugin system to add custom metrics without modifying core code.

### 12.3 Dashboard and Visualization
Integration with visualization tools for trends and analytics.

### 12.4 Machine Learning for Anomaly Detection
Implementation of `ANOMALY_DETECTED` with ML models for automatic detection.

---

## Contacts and Support

- **Repository**: https://github.com/your-org/mosaico-repository
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Team**: WP2 - Agent Benchmarking

---

*Document generated on 2026-01-22*
*Version: 1.0*
