package it.univaq.disim.mosaico.wp2.repository.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ScheduleConfig entity.
 *
 * Test Plan:
 * 1. Entity construction and defaults
 * 2. Record run success
 * 3. Record run failure with auto-disable
 * 4. Timezone handling
 */
@DisplayName("ScheduleConfig Entity Tests")
class ScheduleConfigTest {

    private ScheduleConfig config;

    @BeforeEach
    void setUp() {
        config = new ScheduleConfig("Daily Benchmark", "benchmark-123", "agent-456", "0 0 0 * * *");
    }

    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {

        @Test
        @DisplayName("Should create config with required fields")
        void shouldCreateWithRequiredFields() {
            assertEquals("Daily Benchmark", config.getName());
            assertEquals("benchmark-123", config.getBenchmarkId());
            assertEquals("agent-456", config.getAgentId());
            assertEquals("0 0 0 * * *", config.getCronExpression());
        }

        @Test
        @DisplayName("Should initialize with default values")
        void shouldInitializeWithDefaults() {
            assertEquals("UTC", config.getTimezone());
            assertTrue(config.getEnabled());
            assertEquals(0, config.getRunCount());
            assertEquals(0, config.getFailureCount());
            assertEquals(0, config.getConsecutiveFailures());
            assertEquals(3, config.getMaxConsecutiveFailures());
            assertTrue(config.getAutoDisableOnFailure());
            assertNotNull(config.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Record Run Success Tests")
    class RecordRunSuccessTests {

        @Test
        @DisplayName("Should record successful run")
        void shouldRecordSuccess() {
            config.setRunCount(5);
            config.setConsecutiveFailures(2);

            config.recordRunSuccess("run-123");

            assertEquals(6, config.getRunCount());
            assertEquals(0, config.getConsecutiveFailures());
            assertEquals("run-123", config.getLastRunId());
            assertEquals("COMPLETED", config.getLastRunStatus());
            assertNotNull(config.getLastRunAt());
        }

        @Test
        @DisplayName("Should handle null run count on success")
        void shouldHandleNullRunCountOnSuccess() {
            config.setRunCount(null);

            config.recordRunSuccess("run-123");

            assertEquals(1, config.getRunCount());
        }
    }

    @Nested
    @DisplayName("Record Run Failure Tests")
    class RecordRunFailureTests {

        @Test
        @DisplayName("Should record failed run")
        void shouldRecordFailure() {
            config.setRunCount(5);
            config.setFailureCount(1);
            config.setConsecutiveFailures(0);

            config.recordRunFailure("run-456");

            assertEquals(6, config.getRunCount());
            assertEquals(2, config.getFailureCount());
            assertEquals(1, config.getConsecutiveFailures());
            assertEquals("run-456", config.getLastRunId());
            assertEquals("FAILED", config.getLastRunStatus());
            assertNotNull(config.getLastRunAt());
        }

        @Test
        @DisplayName("Should auto-disable after max consecutive failures")
        void shouldAutoDisableAfterMaxFailures() {
            config.setConsecutiveFailures(2);
            config.setMaxConsecutiveFailures(3);
            config.setAutoDisableOnFailure(true);

            config.recordRunFailure("run-789");

            assertFalse(config.getEnabled());
            assertEquals(3, config.getConsecutiveFailures());
        }

        @Test
        @DisplayName("Should not auto-disable when feature is off")
        void shouldNotAutoDisableWhenFeatureOff() {
            config.setConsecutiveFailures(2);
            config.setMaxConsecutiveFailures(3);
            config.setAutoDisableOnFailure(false);

            config.recordRunFailure("run-789");

            assertTrue(config.getEnabled());
        }

        @Test
        @DisplayName("Should handle null counts on failure")
        void shouldHandleNullCountsOnFailure() {
            config.setRunCount(null);
            config.setFailureCount(null);
            config.setConsecutiveFailures(null);

            config.recordRunFailure("run-123");

            assertEquals(1, config.getRunCount());
            assertEquals(1, config.getFailureCount());
            assertEquals(1, config.getConsecutiveFailures());
        }
    }

    @Nested
    @DisplayName("Timezone Tests")
    class TimezoneTests {

        @Test
        @DisplayName("Should return default UTC timezone")
        void shouldReturnDefaultUtcTimezone() {
            assertEquals(ZoneId.of("UTC"), config.getTimezoneId());
        }

        @Test
        @DisplayName("Should return configured timezone")
        void shouldReturnConfiguredTimezone() {
            config.setTimezone("America/New_York");
            assertEquals(ZoneId.of("America/New_York"), config.getTimezoneId());
        }

        @Test
        @DisplayName("Should handle null timezone")
        void shouldHandleNullTimezone() {
            config.setTimezone(null);
            assertEquals(ZoneId.of("UTC"), config.getTimezoneId());
        }

        @Test
        @DisplayName("Should return zoned next run time")
        void shouldReturnZonedNextRunTime() {
            config.setNextRunAt(Instant.now());
            config.setTimezone("Europe/Rome");

            assertNotNull(config.getNextRunAtZoned());
            assertEquals(ZoneId.of("Europe/Rome"), config.getNextRunAtZoned().getZone());
        }

        @Test
        @DisplayName("Should return null for zoned time when next run is null")
        void shouldReturnNullZonedTimeWhenNull() {
            config.setNextRunAt(null);
            assertNull(config.getNextRunAtZoned());
        }
    }

    @Nested
    @DisplayName("Accessor Tests")
    class AccessorTests {

        @Test
        @DisplayName("Should support record-style accessors")
        void shouldSupportRecordStyleAccessors() {
            config.setId("config-123");
            config.setNextRunAt(Instant.now());

            assertEquals("config-123", config.id());
            assertEquals("Daily Benchmark", config.name());
            assertEquals("benchmark-123", config.benchmarkId());
            assertEquals("agent-456", config.agentId());
            assertEquals("0 0 0 * * *", config.cronExpression());
            assertTrue(config.enabled());
            assertNotNull(config.nextRunAt());
        }
    }
}
