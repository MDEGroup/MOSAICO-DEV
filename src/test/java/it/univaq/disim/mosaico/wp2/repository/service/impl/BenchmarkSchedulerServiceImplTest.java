package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import it.univaq.disim.mosaico.wp2.repository.repository.ScheduleConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for BenchmarkSchedulerServiceImpl.
 *
 * Test Plan:
 * 1. Schedule creation with cron validation
 * 2. Schedule update operations
 * 3. Enable/disable schedule operations
 * 4. Find due schedules
 * 5. Record run success/failure
 * 6. Invalid cron expression handling
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BenchmarkSchedulerServiceImpl Tests")
class BenchmarkSchedulerServiceImplTest {

    @Mock
    private ScheduleConfigRepository repository;

    @InjectMocks
    private BenchmarkSchedulerServiceImpl service;

    private ScheduleConfig testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new ScheduleConfig(
            "Test Schedule",
            "benchmark-123",
            "agent-456",
            "0 0 * * * *" // Every hour
        );
        testSchedule.setId("schedule-789");
    }

    @Nested
    @DisplayName("createSchedule Tests")
    class CreateScheduleTests {

        @Test
        @DisplayName("Should create schedule with valid cron expression")
        void shouldCreateScheduleWithValidCron() {
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            ScheduleConfig result = service.createSchedule(testSchedule);

            assertNotNull(result);
            assertNotNull(result.getNextRunAt());
            verify(repository).save(testSchedule);
        }

        @Test
        @DisplayName("Should throw exception for invalid cron expression")
        void shouldThrowForInvalidCron() {
            testSchedule.setCronExpression("invalid-cron");

            assertThrows(IllegalArgumentException.class, () -> {
                service.createSchedule(testSchedule);
            });

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should calculate next run time on create")
        void shouldCalculateNextRunTimeOnCreate() {
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            ScheduleConfig result = service.createSchedule(testSchedule);

            assertNotNull(result.getNextRunAt());
            assertTrue(result.getNextRunAt().isAfter(Instant.now()));
        }
    }

    @Nested
    @DisplayName("updateSchedule Tests")
    class UpdateScheduleTests {

        @Test
        @DisplayName("Should update schedule and recalculate next run time")
        void shouldUpdateScheduleAndRecalculate() {
            testSchedule.setCronExpression("0 30 * * * *"); // Every hour at :30
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            ScheduleConfig result = service.updateSchedule(testSchedule);

            assertNotNull(result.getNextRunAt());
            verify(repository).save(testSchedule);
        }

        @Test
        @DisplayName("Should throw for invalid cron on update")
        void shouldThrowForInvalidCronOnUpdate() {
            testSchedule.setCronExpression("bad-expression");

            assertThrows(IllegalArgumentException.class, () -> {
                service.updateSchedule(testSchedule);
            });
        }
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return schedule when found")
        void shouldReturnScheduleWhenFound() {
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));

            Optional<ScheduleConfig> result = service.findById("schedule-789");

            assertTrue(result.isPresent());
            assertEquals("schedule-789", result.get().getId());
        }

        @Test
        @DisplayName("Should return empty when not found")
        void shouldReturnEmptyWhenNotFound() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            Optional<ScheduleConfig> result = service.findById("unknown");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findDueSchedules Tests")
    class FindDueSchedulesTests {

        @Test
        @DisplayName("Should return due schedules")
        void shouldReturnDueSchedules() {
            testSchedule.setNextRunAt(Instant.now().minusSeconds(60)); // Past due
            when(repository.findDueSchedules(any(Instant.class))).thenReturn(List.of(testSchedule));

            List<ScheduleConfig> result = service.findDueSchedules();

            assertEquals(1, result.size());
            assertEquals("schedule-789", result.get(0).getId());
        }

        @Test
        @DisplayName("Should return empty list when no schedules are due")
        void shouldReturnEmptyWhenNoDue() {
            when(repository.findDueSchedules(any(Instant.class))).thenReturn(List.of());

            List<ScheduleConfig> result = service.findDueSchedules();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("enableSchedule Tests")
    class EnableScheduleTests {

        @Test
        @DisplayName("Should enable schedule and recalculate next run")
        void shouldEnableSchedule() {
            testSchedule.setEnabled(false);
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.enableSchedule("schedule-789");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertTrue(captor.getValue().getEnabled());
            assertNotNull(captor.getValue().getNextRunAt());
        }

        @Test
        @DisplayName("Should do nothing when schedule not found")
        void shouldDoNothingWhenNotFound() {
            when(repository.findById("unknown")).thenReturn(Optional.empty());

            service.enableSchedule("unknown");

            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("disableSchedule Tests")
    class DisableScheduleTests {

        @Test
        @DisplayName("Should disable schedule")
        void shouldDisableSchedule() {
            testSchedule.setEnabled(true);
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.disableSchedule("schedule-789");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertFalse(captor.getValue().getEnabled());
        }
    }

    @Nested
    @DisplayName("deleteSchedule Tests")
    class DeleteScheduleTests {

        @Test
        @DisplayName("Should delete schedule by id")
        void shouldDeleteSchedule() {
            service.deleteSchedule("schedule-789");

            verify(repository).deleteById("schedule-789");
        }
    }

    @Nested
    @DisplayName("recordRunSuccess Tests")
    class RecordRunSuccessTests {

        @Test
        @DisplayName("Should record successful run")
        void shouldRecordSuccess() {
            testSchedule.setRunCount(5);
            testSchedule.setConsecutiveFailures(2);
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.recordRunSuccess("schedule-789", "run-123");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertEquals(6, captor.getValue().getRunCount());
            assertEquals(0, captor.getValue().getConsecutiveFailures());
            assertEquals("run-123", captor.getValue().getLastRunId());
            assertEquals("COMPLETED", captor.getValue().getLastRunStatus());
        }
    }

    @Nested
    @DisplayName("recordRunFailure Tests")
    class RecordRunFailureTests {

        @Test
        @DisplayName("Should record failed run")
        void shouldRecordFailure() {
            testSchedule.setRunCount(5);
            testSchedule.setFailureCount(1);
            testSchedule.setConsecutiveFailures(0);
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.recordRunFailure("schedule-789", "run-456");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertEquals(6, captor.getValue().getRunCount());
            assertEquals(2, captor.getValue().getFailureCount());
            assertEquals(1, captor.getValue().getConsecutiveFailures());
            assertEquals("run-456", captor.getValue().getLastRunId());
            assertEquals("FAILED", captor.getValue().getLastRunStatus());
        }

        @Test
        @DisplayName("Should auto-disable after max consecutive failures")
        void shouldAutoDisableAfterMaxFailures() {
            testSchedule.setConsecutiveFailures(2);
            testSchedule.setMaxConsecutiveFailures(3);
            testSchedule.setAutoDisableOnFailure(true);
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.recordRunFailure("schedule-789", "run-789");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertFalse(captor.getValue().getEnabled());
            assertEquals(3, captor.getValue().getConsecutiveFailures());
        }
    }

    @Nested
    @DisplayName("findByBenchmarkId Tests")
    class FindByBenchmarkIdTests {

        @Test
        @DisplayName("Should return schedules for benchmark")
        void shouldReturnSchedulesForBenchmark() {
            when(repository.findByBenchmarkId("benchmark-123")).thenReturn(List.of(testSchedule));

            List<ScheduleConfig> result = service.findByBenchmarkId("benchmark-123");

            assertEquals(1, result.size());
            assertEquals("benchmark-123", result.get(0).getBenchmarkId());
        }
    }

    @Nested
    @DisplayName("findEnabledSchedules Tests")
    class FindEnabledSchedulesTests {

        @Test
        @DisplayName("Should return only enabled schedules")
        void shouldReturnEnabledSchedules() {
            when(repository.findByEnabled(true)).thenReturn(List.of(testSchedule));

            List<ScheduleConfig> result = service.findEnabledSchedules();

            assertEquals(1, result.size());
            assertTrue(result.get(0).getEnabled());
        }
    }

    @Nested
    @DisplayName("updateNextRunTime Tests")
    class UpdateNextRunTimeTests {

        @Test
        @DisplayName("Should update next run time")
        void shouldUpdateNextRunTime() {
            Instant oldNextRun = testSchedule.getNextRunAt();
            when(repository.findById("schedule-789")).thenReturn(Optional.of(testSchedule));
            when(repository.save(any(ScheduleConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.updateNextRunTime("schedule-789");

            ArgumentCaptor<ScheduleConfig> captor = ArgumentCaptor.forClass(ScheduleConfig.class);
            verify(repository).save(captor.capture());
            assertNotEquals(oldNextRun, captor.getValue().getNextRunAt());
        }
    }
}
