package it.univaq.disim.mosaico.wp2.repository.service.impl;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;
import it.univaq.disim.mosaico.wp2.repository.data.*;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaDslService;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkResultRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.KPIHistoryRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricSnapshotRepository;
import it.univaq.disim.mosaico.wp2.repository.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for BenchmarkOrchestratorImpl.
 *
 * Test Plan:
 * 1. Execute benchmark run successfully
 * 2. Handle missing benchmark/agent
 * 3. Handle empty traces
 * 4. Cancel benchmark run
 * 5. Retry failed benchmark run
 * 6. Verify alert evaluation is called
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BenchmarkOrchestratorImpl Tests")
class BenchmarkOrchestratorImplTest {

    @Mock
    private BenchmarkRunManager runManager;
    @Mock
    private BenchmarkService benchmarkService;
    @Mock
    private AgentService agentService;
    @Mock
    private LangfuseService langfuseService;
    @Mock
    private MetricProviderRegistry metricProviderRegistry;
    @Mock
    private KPIFormulaDslService kpiFormulaDslService;
    @Mock
    private AlertEvaluationService alertEvaluationService;
    @Mock
    private BenchmarkResultRepository resultRepository;
    @Mock
    private MetricSnapshotRepository metricSnapshotRepository;
    @Mock
    private KPIHistoryRepository kpiHistoryRepository;

    @InjectMocks
    private BenchmarkOrchestratorImpl orchestrator;

    private BenchmarkRun testRun;
    private Benchmark testBenchmark;
    private Agent testAgent;

    @BeforeEach
    void setUp() {
        testRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
        testRun.setId("run-789");

        testBenchmark = new Benchmark();
        testBenchmark.setId("benchmark-123");
        testBenchmark.setDatasetRef("dataset-ref");
        testBenchmark.setRunName("test-run");

        testAgent = new Agent();
        testAgent.setId("agent-456");
    }

    @Nested
    @DisplayName("executeBenchmarkRun Tests")
    class ExecuteBenchmarkRunTests {

        @Test
        @DisplayName("Should execute benchmark run successfully with no traces")
        void shouldExecuteSuccessfullyWithNoTraces() {
            when(runManager.startRun("run-789")).thenReturn(testRun);
            when(benchmarkService.findById("benchmark-123")).thenReturn(Optional.of(testBenchmark));
            when(agentService.findById("agent-456")).thenReturn(Optional.of(testAgent));
            when(langfuseService.getRunBenchmarkTraces(any(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
            lenient().when(metricProviderRegistry.getAllProviders()).thenReturn(Collections.emptyList());
            when(runManager.completeRun(anyString(), anyInt(), anyInt())).thenReturn(testRun);

            BenchmarkRun result = orchestrator.executeBenchmarkRun("run-789");

            assertNotNull(result);
            verify(runManager).startRun("run-789");
            verify(alertEvaluationService).evaluateAlertsForRun("run-789");
            verify(runManager).completeRun("run-789", 0, 0);
        }

        @Test
        @DisplayName("Should fail run when benchmark not found")
        void shouldFailWhenBenchmarkNotFound() {
            when(runManager.startRun("run-789")).thenReturn(testRun);
            when(benchmarkService.findById("benchmark-123")).thenReturn(Optional.empty());
            when(runManager.failRun(anyString(), anyString())).thenReturn(testRun);

            BenchmarkRun result = orchestrator.executeBenchmarkRun("run-789");

            verify(runManager).failRun(eq("run-789"), contains("Benchmark not found"));
        }

        @Test
        @DisplayName("Should fail run when agent not found")
        void shouldFailWhenAgentNotFound() {
            when(runManager.startRun("run-789")).thenReturn(testRun);
            when(benchmarkService.findById("benchmark-123")).thenReturn(Optional.of(testBenchmark));
            when(agentService.findById("agent-456")).thenReturn(Optional.empty());
            when(runManager.failRun(anyString(), anyString())).thenReturn(testRun);

            BenchmarkRun result = orchestrator.executeBenchmarkRun("run-789");

            verify(runManager).failRun(eq("run-789"), contains("Agent not found"));
        }

        @Test
        @DisplayName("Should handle exception during execution")
        void shouldHandleExceptionDuringExecution() {
            when(runManager.startRun("run-789")).thenReturn(testRun);
            when(benchmarkService.findById("benchmark-123")).thenReturn(Optional.of(testBenchmark));
            when(agentService.findById("agent-456")).thenReturn(Optional.of(testAgent));
            when(langfuseService.getRunBenchmarkTraces(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Connection failed"));
            when(runManager.failRun(anyString(), anyString())).thenReturn(testRun);

            BenchmarkRun result = orchestrator.executeBenchmarkRun("run-789");

            verify(runManager).failRun(eq("run-789"), contains("Connection failed"));
        }
    }

    @Nested
    @DisplayName("cancelBenchmarkRun Tests")
    class CancelBenchmarkRunTests {

        @Test
        @DisplayName("Should cancel benchmark run")
        void shouldCancelRun() {
            orchestrator.cancelBenchmarkRun("run-789");

            verify(runManager).cancelRun("run-789");
        }
    }

    @Nested
    @DisplayName("retryBenchmarkRun Tests")
    class RetryBenchmarkRunTests {

        @Test
        @DisplayName("Should create retry run")
        void shouldCreateRetryRun() {
            testRun.setStatus(RunStatus.FAILED);
            testRun.setRetryCount(0);
            when(runManager.findById("run-789")).thenReturn(Optional.of(testRun));

            BenchmarkRun newRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
            newRun.setId("retry-run-id");
            when(runManager.createRun(anyString(), anyString(), any(), anyString()))
                .thenReturn(newRun);

            String retryRunId = orchestrator.retryBenchmarkRun("run-789");

            assertEquals("retry-run-id", retryRunId);
            verify(runManager).createRun("benchmark-123", "agent-456", TriggerType.MANUAL, "retry");
        }

        @Test
        @DisplayName("Should throw when run not found")
        void shouldThrowWhenRunNotFound() {
            when(runManager.findById("unknown")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                orchestrator.retryBenchmarkRun("unknown");
            });
        }

        @Test
        @DisplayName("Should throw when max retries exceeded")
        void shouldThrowWhenMaxRetriesExceeded() {
            testRun.setRetryCount(3);
            when(runManager.findById("run-789")).thenReturn(Optional.of(testRun));

            assertThrows(IllegalStateException.class, () -> {
                orchestrator.retryBenchmarkRun("run-789");
            });
        }
    }

    @Nested
    @DisplayName("executeBenchmarkRunAsync Tests")
    class AsyncExecutionTests {

        @Test
        @DisplayName("Should execute asynchronously")
        void shouldExecuteAsync() {
            // Use lenient stubbings since async execution may or may not use all mocks
            lenient().when(runManager.startRun("run-789")).thenReturn(testRun);
            lenient().when(benchmarkService.findById("benchmark-123")).thenReturn(Optional.of(testBenchmark));
            lenient().when(agentService.findById("agent-456")).thenReturn(Optional.of(testAgent));
            lenient().when(langfuseService.getRunBenchmarkTraces(any(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
            lenient().when(metricProviderRegistry.getAllProviders()).thenReturn(Collections.emptyList());
            lenient().when(runManager.completeRun(anyString(), anyInt(), anyInt())).thenReturn(testRun);

            // Should not throw - async execution delegates to sync method
            assertDoesNotThrow(() -> orchestrator.executeBenchmarkRunAsync("run-789"));
        }
    }
}
