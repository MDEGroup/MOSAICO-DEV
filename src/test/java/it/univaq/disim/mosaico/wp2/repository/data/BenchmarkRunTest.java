package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BenchmarkRun entity.
 *
 * Test Plan:
 * 1. Entity construction
 * 2. Lifecycle transitions (start, complete, fail, cancel)
 * 3. Duration calculation
 * 4. Retry count management
 */
@DisplayName("BenchmarkRun Entity Tests")
class BenchmarkRunTest {

    private BenchmarkRun run;

    @BeforeEach
    void setUp() {
        run = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create run with PENDING status")
        void shouldCreateWithPendingStatus() {
            assertEquals(RunStatus.PENDING, run.getStatus());
        }

        @Test
        @DisplayName("Should set benchmark and agent IDs")
        void shouldSetBenchmarkAndAgentIds() {
            assertEquals("benchmark-123", run.getBenchmarkId());
            assertEquals("agent-456", run.getAgentId());
        }

        @Test
        @DisplayName("Should set trigger type")
        void shouldSetTriggerType() {
            assertEquals(TriggerType.MANUAL, run.getTriggeredBy());
        }

        @Test
        @DisplayName("Should initialize retry count to zero")
        void shouldInitializeRetryCountToZero() {
            assertEquals(0, run.getRetryCount());
        }

        @Test
        @DisplayName("Should not have start time before starting")
        void shouldNotHaveStartTime() {
            assertNull(run.getStartedAt());
        }
    }

    @Nested
    @DisplayName("Lifecycle Tests")
    class LifecycleTests {

        @Test
        @DisplayName("Should transition to RUNNING when started")
        void shouldTransitionToRunning() {
            run.start();

            assertEquals(RunStatus.RUNNING, run.getStatus());
            assertNotNull(run.getStartedAt());
        }

        @Test
        @DisplayName("Should transition to COMPLETED when completed")
        void shouldTransitionToCompleted() {
            run.start();
            run.complete();

            assertEquals(RunStatus.COMPLETED, run.getStatus());
            assertNotNull(run.getCompletedAt());
        }

        @Test
        @DisplayName("Should transition to FAILED with error message")
        void shouldTransitionToFailed() {
            run.start();
            run.fail("Connection timeout");

            assertEquals(RunStatus.FAILED, run.getStatus());
            assertEquals("Connection timeout", run.getErrorMessage());
            assertNotNull(run.getCompletedAt());
        }

        @Test
        @DisplayName("Should transition to CANCELLED")
        void shouldTransitionToCancelled() {
            run.start();
            run.cancel();

            assertEquals(RunStatus.CANCELLED, run.getStatus());
            assertNotNull(run.getCompletedAt());
        }
    }

    @Nested
    @DisplayName("Duration Tests")
    class DurationTests {

        @Test
        @DisplayName("Should return zero duration when not started")
        void shouldReturnZeroDurationWhenNotStarted() {
            assertEquals(0, run.getDurationMillis());
        }

        @Test
        @DisplayName("Should return zero duration when not completed")
        void shouldReturnZeroDurationWhenNotCompleted() {
            run.start();
            assertEquals(0, run.getDurationMillis());
        }

        @Test
        @DisplayName("Should calculate duration when completed")
        void shouldCalculateDuration() throws InterruptedException {
            run.start();
            Thread.sleep(10);
            run.complete();

            assertTrue(run.getDurationMillis() >= 10);
        }
    }

    @Nested
    @DisplayName("Retry Tests")
    class RetryTests {

        @Test
        @DisplayName("Should increment retry count")
        void shouldIncrementRetryCount() {
            run.incrementRetry();
            assertEquals(1, run.getRetryCount());

            run.incrementRetry();
            assertEquals(2, run.getRetryCount());
        }

        @Test
        @DisplayName("Should handle null retry count")
        void shouldHandleNullRetryCount() {
            run.setRetryCount(null);
            run.incrementRetry();
            assertEquals(1, run.getRetryCount());
        }
    }

    @Nested
    @DisplayName("Accessor Tests")
    class AccessorTests {

        @Test
        @DisplayName("Should support record-style accessors")
        void shouldSupportRecordStyleAccessors() {
            run.start();

            assertEquals("benchmark-123", run.benchmarkId());
            assertEquals("agent-456", run.agentId());
            assertEquals(RunStatus.RUNNING, run.status());
            assertEquals(TriggerType.MANUAL, run.triggeredBy());
            assertNotNull(run.startedAt());
        }

        @Test
        @DisplayName("Should set and get all properties")
        void shouldSetAndGetAllProperties() {
            run.setTracesProcessed(100);
            run.setMetricsComputed(500);
            run.setScheduleConfigId("schedule-123");
            run.setTriggeredByUser("user@example.com");

            assertEquals(100, run.getTracesProcessed());
            assertEquals(500, run.getMetricsComputed());
            assertEquals("schedule-123", run.getScheduleConfigId());
            assertEquals("user@example.com", run.getTriggeredByUser());
        }
    }
}
