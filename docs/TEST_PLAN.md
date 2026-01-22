# Test Plan - Benchmarking Architecture

This document describes the test plan for the new benchmarking architecture implemented in the MOSAICO repository.

## Overview

The test suite covers the following components:
- DSL (Domain Specific Language) for KPI formulas
- Scheduling services for automated benchmark execution
- Orchestration services for benchmark run management
- Alerting services for KPI threshold monitoring
- Entity classes and their lifecycle
- REST controllers for API endpoints

## Test Structure

All tests use JUnit 5 with the following conventions:
- `@DisplayName` annotations for readable test names
- `@Nested` classes for grouping related tests
- Mockito for mocking dependencies
- `@ExtendWith(MockitoExtension.class)` for mock injection

## Test Classes

### 1. DSL Tests

#### DefaultKPIFormulaParserTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/dsl/DefaultKPIFormulaParserTest.java`

| Test Category | Description |
|--------------|-------------|
| AVERAGE Formula Tests | Parse and evaluate AVERAGE formulas with multiple metrics |
| WEIGHTED_SUM Formula Tests | Parse and evaluate weighted sum formulas |
| MIN/MAX Formula Tests | Parse and evaluate MIN/MAX aggregation formulas |
| THRESHOLD Formula Tests | Parse and evaluate threshold-based formulas |
| Error Handling Tests | Handle null, empty, and invalid expressions |
| Metric Key Registration Tests | Register custom metric keys |
| Validation Tests | Validate formulas without full parsing |

#### DefaultKPIFormulaDslServiceTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/dsl/DefaultKPIFormulaDslServiceTest.java`

| Test Category | Description |
|--------------|-------------|
| parseFormula Tests | Parse DSL expressions into KPIFormula objects |
| buildFromSpecification Tests | Build formulas from KPISpecification entities |
| validateFormula Tests | Validate formula syntax |
| validateFormulaAgainstMetrics Tests | Validate formulas against available metrics |
| createSpecification Tests | Create KPISpecification from DSL text |
| Metric Key Management Tests | Manage known metric keys |
| DSL Syntax Help Tests | Generate syntax help documentation |

### 2. Scheduling Tests

#### BenchmarkSchedulerServiceImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/BenchmarkSchedulerServiceImplTest.java`

| Test Category | Description |
|--------------|-------------|
| createSchedule Tests | Create schedules with cron validation |
| updateSchedule Tests | Update existing schedules |
| findById Tests | Find schedules by ID |
| findDueSchedules Tests | Find schedules due for execution |
| enableSchedule Tests | Enable disabled schedules |
| disableSchedule Tests | Disable active schedules |
| deleteSchedule Tests | Delete schedules |
| recordRunSuccess Tests | Record successful benchmark runs |
| recordRunFailure Tests | Record failed runs with auto-disable |
| findByBenchmarkId Tests | Find schedules by benchmark |
| findEnabledSchedules Tests | Find all enabled schedules |
| updateNextRunTime Tests | Update next execution time |

#### EventTriggerServiceImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/EventTriggerServiceImplTest.java`

| Test Category | Description |
|--------------|-------------|
| triggerBenchmarkRun Tests | Trigger runs with various trigger types |
| onWebhookTrigger Tests | Handle webhook-triggered runs |
| Event Handler Tests | Handle agent/dataset update events |
| Run Initialization Tests | Verify initial run state |

### 3. Orchestration Tests

#### BenchmarkRunManagerImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/BenchmarkRunManagerImplTest.java`

| Test Category | Description |
|--------------|-------------|
| createRun Tests | Create runs with different trigger types |
| startRun Tests | Start pending runs |
| completeRun Tests | Complete runs with metrics |
| failRun Tests | Mark runs as failed |
| cancelRun Tests | Cancel running benchmarks |
| updateProgress Tests | Update run progress |
| findById Tests | Find runs by ID |
| findByBenchmarkId Tests | Find runs by benchmark |
| findByAgentId Tests | Find runs by agent |
| findByStatus Tests | Find runs by status |
| getRunHistory Tests | Get run history with limit |

#### BenchmarkOrchestratorImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/BenchmarkOrchestratorImplTest.java`

| Test Category | Description |
|--------------|-------------|
| executeBenchmarkRun Tests | Execute benchmark runs |
| cancelBenchmarkRun Tests | Cancel running benchmarks |
| retryBenchmarkRun Tests | Retry failed runs |
| executeBenchmarkRunAsync Tests | Async execution |

### 4. Alerting Tests

#### AlertEvaluationServiceImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/AlertEvaluationServiceImplTest.java`

| Test Category | Description |
|--------------|-------------|
| evaluateAlertsForRun Tests | Evaluate alerts after benchmark run |
| evaluateKpiValue Tests | Evaluate KPI values against thresholds |
| CRUD Operations Tests | Create, read, update, delete alerts |
| Enable/Disable Tests | Enable and disable alerts |

#### NotificationDispatcherImplTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/service/impl/NotificationDispatcherImplTest.java`

| Test Category | Description |
|--------------|-------------|
| dispatch Tests | Dispatch to various channels |
| Individual Channel Tests | Test individual notification methods |
| Message Formatting Tests | Verify alert message format |
| Error Handling Tests | Handle edge cases |

### 5. Entity Tests

#### BenchmarkRunTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/data/BenchmarkRunTest.java`

| Test Category | Description |
|--------------|-------------|
| Construction Tests | Entity initialization |
| Lifecycle Tests | State transitions (start, complete, fail, cancel) |
| Duration Tests | Duration calculation |
| Retry Tests | Retry count management |
| Accessor Tests | Getter/setter verification |

#### ScheduleConfigTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/data/ScheduleConfigTest.java`

| Test Category | Description |
|--------------|-------------|
| Construction Tests | Entity initialization with defaults |
| Record Run Success Tests | Record successful runs |
| Record Run Failure Tests | Record failures with auto-disable |
| Timezone Tests | Timezone handling |
| Accessor Tests | Getter/setter verification |

### 6. Controller Tests

#### BenchmarkRunControllerTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/controller/BenchmarkRunControllerTest.java`

| Test Category | Description |
|--------------|-------------|
| triggerRun Tests | POST /api/benchmark-runs |
| getRun Tests | GET /api/benchmark-runs/{id} |
| getRunsByBenchmark Tests | GET /api/benchmark-runs with filters |
| getRunHistory Tests | GET /api/benchmark-runs/{benchmarkId}/{agentId}/history |
| cancelRun Tests | POST /api/benchmark-runs/{id}/cancel |
| retryRun Tests | POST /api/benchmark-runs/{id}/retry |

#### ScheduleConfigControllerTest
Location: `src/test/java/it/univaq/disim/mosaico/wp2/repository/controller/ScheduleConfigControllerTest.java`

| Test Category | Description |
|--------------|-------------|
| createSchedule Tests | POST /api/schedules |
| getSchedule Tests | GET /api/schedules/{id} |
| getSchedules Tests | GET /api/schedules with filters |
| updateSchedule Tests | PUT /api/schedules/{id} |
| deleteSchedule Tests | DELETE /api/schedules/{id} |
| enableSchedule Tests | POST /api/schedules/{id}/enable |
| disableSchedule Tests | POST /api/schedules/{id}/disable |
| getDueSchedules Tests | GET /api/schedules/due |

## Running Tests

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=DefaultKPIFormulaParserTest
```

### Run Tests with Coverage
```bash
./mvnw test jacoco:report
```

## Test Coverage Goals

| Component | Target Coverage |
|-----------|----------------|
| DSL Classes | > 90% |
| Service Implementations | > 85% |
| Entity Classes | > 80% |
| Controllers | > 80% |
| Overall | > 80% |

## Future Test Enhancements

1. **Integration Tests**: Add Spring Boot integration tests with `@SpringBootTest`
2. **Repository Tests**: Add `@DataJpaTest` tests for repository layer
3. **End-to-End Tests**: Add full workflow tests
4. **Performance Tests**: Add benchmark tests for critical paths
5. **Contract Tests**: Add API contract tests for REST endpoints

## Dependencies

The test suite uses the following test dependencies:
- JUnit 5 (Jupiter)
- Mockito
- Spring Boot Test
- AssertJ (optional, for fluent assertions)

## Notes

- All tests are designed to run in isolation without external dependencies
- Mocks are used for database and external service interactions
- Test data is created fresh in `@BeforeEach` methods
- Tests follow the Arrange-Act-Assert pattern
