package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkSchedulerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for ScheduleConfigController.
 *
 * Test Plan:
 * 1. Create schedule configuration
 * 2. Get schedule by id
 * 3. Update schedule
 * 4. Delete schedule
 * 5. Enable/disable schedule
 * 6. List schedules
 * 7. Get due schedules
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ScheduleConfigController Tests")
class ScheduleConfigControllerTest {

    @Mock
    private BenchmarkSchedulerService schedulerService;

    @InjectMocks
    private ScheduleConfigController controller;

    private ScheduleConfig testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new ScheduleConfig("Daily Run", "benchmark-123", "agent-456", "0 0 0 * * *");
        testSchedule.setId("schedule-789");
    }

    @Nested
    @DisplayName("createSchedule Tests")
    class CreateScheduleTests {

        @Test
        @DisplayName("Should create schedule")
        void shouldCreateSchedule() {
            when(schedulerService.createSchedule(any(ScheduleConfig.class))).thenReturn(testSchedule);

            ResponseEntity<ScheduleConfig> response = controller.createSchedule(testSchedule);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("schedule-789", response.getBody().getId());
        }

        @Test
        @DisplayName("Should return bad request for invalid cron")
        void shouldReturnBadRequestForInvalidCron() {
            when(schedulerService.createSchedule(any(ScheduleConfig.class)))
                .thenThrow(new IllegalArgumentException("Invalid cron"));

            ResponseEntity<ScheduleConfig> response = controller.createSchedule(testSchedule);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getSchedule Tests")
    class GetScheduleTests {

        @Test
        @DisplayName("Should return schedule when found")
        void shouldReturnScheduleWhenFound() {
            when(schedulerService.findById("schedule-789")).thenReturn(Optional.of(testSchedule));

            ResponseEntity<ScheduleConfig> response = controller.getSchedule("schedule-789");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("schedule-789", response.getBody().getId());
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() {
            when(schedulerService.findById("unknown")).thenReturn(Optional.empty());

            ResponseEntity<ScheduleConfig> response = controller.getSchedule("unknown");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getSchedules Tests")
    class GetSchedulesTests {

        @Test
        @DisplayName("Should list schedules by benchmark id")
        void shouldListSchedulesByBenchmarkId() {
            when(schedulerService.findByBenchmarkId("benchmark-123")).thenReturn(List.of(testSchedule));

            ResponseEntity<List<ScheduleConfig>> response = controller.getSchedules("benchmark-123", null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Should list enabled schedules")
        void shouldListEnabledSchedules() {
            when(schedulerService.findEnabledSchedules()).thenReturn(List.of(testSchedule));

            ResponseEntity<List<ScheduleConfig>> response = controller.getSchedules(null, true);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Should return enabled schedules by default")
        void shouldReturnEnabledByDefault() {
            when(schedulerService.findEnabledSchedules()).thenReturn(List.of(testSchedule));

            ResponseEntity<List<ScheduleConfig>> response = controller.getSchedules(null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(schedulerService).findEnabledSchedules();
        }
    }

    @Nested
    @DisplayName("updateSchedule Tests")
    class UpdateScheduleTests {

        @Test
        @DisplayName("Should update schedule")
        void shouldUpdateSchedule() {
            testSchedule.setCronExpression("0 0 12 * * *");
            when(schedulerService.updateSchedule(any(ScheduleConfig.class))).thenReturn(testSchedule);

            ResponseEntity<ScheduleConfig> response = controller.updateSchedule("schedule-789", testSchedule);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("schedule-789", response.getBody().getId());
        }

        @Test
        @DisplayName("Should return bad request for invalid cron on update")
        void shouldReturnBadRequestForInvalidCronOnUpdate() {
            when(schedulerService.updateSchedule(any(ScheduleConfig.class)))
                .thenThrow(new IllegalArgumentException("Invalid cron"));

            ResponseEntity<ScheduleConfig> response = controller.updateSchedule("schedule-789", testSchedule);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("deleteSchedule Tests")
    class DeleteScheduleTests {

        @Test
        @DisplayName("Should delete schedule")
        void shouldDeleteSchedule() {
            ResponseEntity<Void> response = controller.deleteSchedule("schedule-789");

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            verify(schedulerService).deleteSchedule("schedule-789");
        }
    }

    @Nested
    @DisplayName("enableSchedule Tests")
    class EnableScheduleTests {

        @Test
        @DisplayName("Should enable schedule")
        void shouldEnableSchedule() {
            ResponseEntity<Void> response = controller.enableSchedule("schedule-789");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(schedulerService).enableSchedule("schedule-789");
        }
    }

    @Nested
    @DisplayName("disableSchedule Tests")
    class DisableScheduleTests {

        @Test
        @DisplayName("Should disable schedule")
        void shouldDisableSchedule() {
            ResponseEntity<Void> response = controller.disableSchedule("schedule-789");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(schedulerService).disableSchedule("schedule-789");
        }
    }

    @Nested
    @DisplayName("getDueSchedules Tests")
    class GetDueSchedulesTests {

        @Test
        @DisplayName("Should return due schedules")
        void shouldReturnDueSchedules() {
            when(schedulerService.findDueSchedules()).thenReturn(List.of(testSchedule));

            ResponseEntity<List<ScheduleConfig>> response = controller.getDueSchedules();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }
    }
}
