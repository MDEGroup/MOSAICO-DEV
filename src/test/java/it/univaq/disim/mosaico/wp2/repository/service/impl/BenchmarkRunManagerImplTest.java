package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for BenchmarkRunManagerImpl.
 *
 * Test Plan:
 * 1. Create run with various trigger types
 * 2. Start run and verify status change
 * 3. Complete run with metrics
 * 4. Fail run with error message
 * 5. Cancel run
 * 6. Update progress
 * 7. Find operations (by id, benchmark, agent, status)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BenchmarkRunManagerImpl Tests")
class BenchmarkRunManagerImplTest {

    @Mock
    private BenchmarkRunRepository repository;

    @InjectMocks
    private BenchmarkRunManagerImpl manager;

    private BenchmarkRun testRun;

    @BeforeEach
    void setUp() {
        testRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
        testRun.setId("run-789");
    }

    @Nested
    @DisplayName("createRun Tests")
    class CreateRunTests {

        @Test
        @DisplayName("Should create run with PENDING status")
        void shouldCreateRunWithPendingStatus() {
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> {
                BenchmarkRun run = i.getArgument(0);
                run.setId("new-run-id");
                return run;
            });

            BenchmarkRun result = manager.createRun(
                "benchmark-123", "agent-456", TriggerType.MANUAL, "user@example.com");

            assertNotNull(result);
            assertEquals("new-run-id", result.getId());
            assertEquals(RunStatus.PENDING, result.getStatus());
            assertEquals("benchmark-123", result.getBenchmarkId());
            assertEquals("agent-456", result.getAgentId());
            assertEquals(TriggerType.MANUAL, result.getTriggeredBy());
            assertEquals("user@example.com", result.getTriggeredByUser());
        }

        @Test
        @DisplayName("Should create run with scheduled trigger")
        void shouldCreateRunWithScheduledTrigger() {
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> {
                BenchmarkRun run = i.getArgument(0);
                run.setId("scheduled-run-id");
                return run;
            });

            BenchmarkRun result = manager.createRun(
                "benchmark-123", "agent-456", TriggerType.SCHEDULED, "scheduler");

            assertEquals(TriggerType.SCHEDULED, result.getTriggeredBy());
            assertEquals("scheduler", result.getTriggeredByUser());
        }
    }

    @Nested
    @DisplayName("startRun Tests")
    class StartRunTests {

        @Test
        @DisplayName("Should start run and set RUNNING status")
        void shouldStartRunWithRunningStatus() {
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> i.getArgument(0));

            BenchmarkRun result = manager.startRun("run-789");

            assertEquals(RunStatus.RUNNING, result.getStatus());
            assertNotNull(result.getStartedAt());
        }

        @Test
        @DisplayName("Should throw when run not found")
        void shouldThrowWhenRunNotFound() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                manager.startRun("unknown");
            });
        }
    }

    @Nested
    @DisplayName("completeRun Tests")
    class CompleteRunTests {

        @Test
        @DisplayName("Should complete run with metrics")
        void shouldCompleteRunWithMetrics() {
            testRun.start();
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> i.getArgument(0));

            BenchmarkRun result = manager.completeRun("run-789", 100, 500);

            assertEquals(RunStatus.COMPLETED, result.getStatus());
            assertEquals(100, result.getTracesProcessed());
            assertEquals(500, result.getMetricsComputed());
            assertNotNull(result.getCompletedAt());
        }

        @Test
        @DisplayName("Should throw when completing unknown run")
        void shouldThrowWhenCompletingUnknownRun() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                manager.completeRun("unknown", 0, 0);
            });
        }
    }

    @Nested
    @DisplayName("failRun Tests")
    class FailRunTests {

        @Test
        @DisplayName("Should fail run with error message")
        void shouldFailRunWithErrorMessage() {
            testRun.start();
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> i.getArgument(0));

            BenchmarkRun result = manager.failRun("run-789", "Connection timeout");

            assertEquals(RunStatus.FAILED, result.getStatus());
            assertEquals("Connection timeout", result.getErrorMessage());
            assertNotNull(result.getCompletedAt());
        }
    }

    @Nested
    @DisplayName("cancelRun Tests")
    class CancelRunTests {

        @Test
        @DisplayName("Should cancel run")
        void shouldCancelRun() {
            testRun.start();
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> i.getArgument(0));

            BenchmarkRun result = manager.cancelRun("run-789");

            assertEquals(RunStatus.CANCELLED, result.getStatus());
            assertNotNull(result.getCompletedAt());
        }
    }

    @Nested
    @DisplayName("updateProgress Tests")
    class UpdateProgressTests {

        @Test
        @DisplayName("Should update progress counts")
        void shouldUpdateProgressCounts() {
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(repository.save(any(BenchmarkRun.class))).thenAnswer(i -> i.getArgument(0));

            manager.updateProgress("run-789", 50, 250);

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(repository).save(captor.capture());
            assertEquals(50, captor.getValue().getTracesProcessed());
            assertEquals(250, captor.getValue().getMetricsComputed());
        }

        @Test
        @DisplayName("Should do nothing when run not found")
        void shouldDoNothingWhenNotFound() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            manager.updateProgress("unknown", 50, 250);

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return run when found")
        void shouldReturnRunWhenFound() {
            when(repository.findById("run-789")).thenReturn(Optional.of(testRun));

            Optional<BenchmarkRun> result = manager.findById("run-789");

            assertTrue(result.isPresent());
            assertEquals("run-789", result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            Optional<BenchmarkRun> result = manager.findById("unknown");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByBenchmarkId Tests")
    class FindByBenchmarkIdTests {

        @Test
        @DisplayName("Should return runs for benchmark")
        void shouldReturnRunsForBenchmark() {
            when(repository.findByBenchmarkIdOrderByStartedAtDesc("benchmark-123"))
                .thenReturn(List.of(testRun));

            List<BenchmarkRun> result = manager.findByBenchmarkId("benchmark-123");

            assertEquals(1, result.size());
            assertEquals("benchmark-123", result.get(0).getBenchmarkId());
        }
    }

    @Nested
    @DisplayName("findByAgentId Tests")
    class FindByAgentIdTests {

        @Test
        @DisplayName("Should return runs for agent")
        void shouldReturnRunsForAgent() {
            when(repository.findByAgentIdOrderByStartedAtDesc("agent-456"))
                .thenReturn(List.of(testRun));

            List<BenchmarkRun> result = manager.findByAgentId("agent-456");

            assertEquals(1, result.size());
            assertEquals("agent-456", result.get(0).getAgentId());
        }
    }

    @Nested
    @DisplayName("findByStatus Tests")
    class FindByStatusTests {

        @Test
        @DisplayName("Should return runs with specific status")
        void shouldReturnRunsWithStatus() {
            when(repository.findByStatus(RunStatus.PENDING))
                .thenReturn(List.of(testRun));

            List<BenchmarkRun> result = manager.findByStatus(RunStatus.PENDING);

            assertEquals(1, result.size());
            assertEquals(RunStatus.PENDING, result.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("getRunHistory Tests")
    class GetRunHistoryTests {

        @Test
        @DisplayName("Should return limited run history")
        void shouldReturnLimitedRunHistory() {
            BenchmarkRun run1 = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
            BenchmarkRun run2 = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.SCHEDULED);
            BenchmarkRun run3 = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
            when(repository.findByBenchmarkIdAndAgentId("benchmark-123", "agent-456"))
                .thenReturn(List.of(run1, run2, run3));

            List<BenchmarkRun> result = manager.getRunHistory("benchmark-123", "agent-456", 2);

            assertEquals(2, result.size());
        }
    }
}
