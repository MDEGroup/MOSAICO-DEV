package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.KPIHistory;
import it.univaq.disim.mosaico.wp2.repository.data.enums.AlertCondition;
import it.univaq.disim.mosaico.wp2.repository.data.enums.NotificationChannel;
import it.univaq.disim.mosaico.wp2.repository.data.enums.Severity;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.repository.AlertConfigRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.KPIHistoryRepository;
import it.univaq.disim.mosaico.wp2.repository.service.NotificationDispatcher;
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
 * Test class for AlertEvaluationServiceImpl.
 *
 * Test Plan:
 * 1. Evaluate alerts for a run
 * 2. Evaluate KPI value against thresholds
 * 3. Check various alert conditions (LESS_THAN, GREATER_THAN, EQUALS, NOT_EQUALS)
 * 4. Handle cooldown period
 * 5. CRUD operations for alerts
 * 6. Enable/disable alerts
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlertEvaluationServiceImpl Tests")
class AlertEvaluationServiceImplTest {

    @Mock
    private AlertConfigRepository alertConfigRepository;
    @Mock
    private KPIHistoryRepository kpiHistoryRepository;
    @Mock
    private BenchmarkRunRepository benchmarkRunRepository;
    @Mock
    private NotificationDispatcher notificationDispatcher;

    @InjectMocks
    private AlertEvaluationServiceImpl service;

    private AlertConfig testAlert;
    private BenchmarkRun testRun;
    private KPIHistory testKpiHistory;

    @BeforeEach
    void setUp() {
        testAlert = new AlertConfig();
        testAlert.setId("alert-123");
        testAlert.setName("Low Accuracy Alert");
        testAlert.setBenchmarkId("benchmark-123");
        testAlert.setKpiName("accuracy");
        testAlert.setCondition(AlertCondition.LESS_THAN);
        testAlert.setThreshold(0.7);
        testAlert.setSeverity(Severity.WARNING);
        testAlert.setEnabled(true);
        testAlert.setChannels(List.of(NotificationChannel.EMAIL));

        testRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
        testRun.setId("run-789");

        testKpiHistory = new KPIHistory("benchmark-123", "agent-456", "accuracy", 0.65);
        testKpiHistory.setRunId("run-789");
    }

    @Nested
    @DisplayName("evaluateAlertsForRun Tests")
    class EvaluateAlertsForRunTests {

        @Test
        @DisplayName("Should evaluate alerts for run and trigger notification")
        void shouldEvaluateAndTriggerNotification() {
            when(benchmarkRunRepository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(kpiHistoryRepository.findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc("benchmark-123", "agent-456"))
                .thenReturn(List.of(testKpiHistory));
            when(alertConfigRepository.findByBenchmarkIdAndEnabled("benchmark-123", true))
                .thenReturn(List.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.evaluateAlertsForRun("run-789");

            verify(notificationDispatcher).dispatch(eq(testAlert), eq("accuracy"), eq(0.65));
            verify(alertConfigRepository).save(any(AlertConfig.class));
        }

        @Test
        @DisplayName("Should not trigger when value above threshold")
        void shouldNotTriggerWhenAboveThreshold() {
            testKpiHistory = new KPIHistory("benchmark-123", "agent-456", "accuracy", 0.85);
            testKpiHistory.setRunId("run-789");

            when(benchmarkRunRepository.findById("run-789")).thenReturn(Optional.of(testRun));
            when(kpiHistoryRepository.findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc("benchmark-123", "agent-456"))
                .thenReturn(List.of(testKpiHistory));
            when(alertConfigRepository.findByBenchmarkIdAndEnabled("benchmark-123", true))
                .thenReturn(List.of(testAlert));

            service.evaluateAlertsForRun("run-789");

            verify(notificationDispatcher, never()).dispatch(any(), any(), anyDouble());
        }

        @Test
        @DisplayName("Should handle missing run gracefully")
        void shouldHandleMissingRun() {
            when(benchmarkRunRepository.findById("unknown")).thenReturn(Optional.empty());

            // Should not throw
            assertDoesNotThrow(() -> service.evaluateAlertsForRun("unknown"));
            verify(kpiHistoryRepository, never()).findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc(any(), any());
        }
    }

    @Nested
    @DisplayName("evaluateKpiValue Tests")
    class EvaluateKpiValueTests {

        @Test
        @DisplayName("Should trigger alert when LESS_THAN condition met")
        void shouldTriggerWhenLessThanConditionMet() {
            when(alertConfigRepository.findActiveAlertsForKpi("benchmark-123", "accuracy"))
                .thenReturn(List.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            List<AlertConfig> triggered = service.evaluateKpiValue("benchmark-123", "accuracy", 0.5);

            assertEquals(1, triggered.size());
            assertEquals("alert-123", triggered.get(0).getId());
            verify(notificationDispatcher).dispatch(eq(testAlert), eq("accuracy"), eq(0.5));
        }

        @Test
        @DisplayName("Should trigger alert when GREATER_THAN condition met")
        void shouldTriggerWhenGreaterThanConditionMet() {
            testAlert.setCondition(AlertCondition.GREATER_THAN);
            testAlert.setThreshold(0.9);
            when(alertConfigRepository.findActiveAlertsForKpi("benchmark-123", "accuracy"))
                .thenReturn(List.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            List<AlertConfig> triggered = service.evaluateKpiValue("benchmark-123", "accuracy", 0.95);

            assertEquals(1, triggered.size());
            verify(notificationDispatcher).dispatch(any(), any(), eq(0.95));
        }

        @Test
        @DisplayName("Should trigger alert when EQUALS condition met")
        void shouldTriggerWhenEqualsConditionMet() {
            testAlert.setCondition(AlertCondition.EQUALS);
            testAlert.setThreshold(1.0);
            when(alertConfigRepository.findActiveAlertsForKpi("benchmark-123", "accuracy"))
                .thenReturn(List.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            List<AlertConfig> triggered = service.evaluateKpiValue("benchmark-123", "accuracy", 1.0);

            assertEquals(1, triggered.size());
        }

        @Test
        @DisplayName("Should trigger alert when NOT_EQUALS condition met")
        void shouldTriggerWhenNotEqualsConditionMet() {
            testAlert.setCondition(AlertCondition.NOT_EQUALS);
            testAlert.setThreshold(1.0);
            when(alertConfigRepository.findActiveAlertsForKpi("benchmark-123", "accuracy"))
                .thenReturn(List.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            List<AlertConfig> triggered = service.evaluateKpiValue("benchmark-123", "accuracy", 0.8);

            assertEquals(1, triggered.size());
        }

        @Test
        @DisplayName("Should return empty list when no alerts triggered")
        void shouldReturnEmptyWhenNoAlertTriggered() {
            testAlert.setCondition(AlertCondition.LESS_THAN);
            testAlert.setThreshold(0.5);
            when(alertConfigRepository.findActiveAlertsForKpi("benchmark-123", "accuracy"))
                .thenReturn(List.of(testAlert));

            List<AlertConfig> triggered = service.evaluateKpiValue("benchmark-123", "accuracy", 0.9);

            assertTrue(triggered.isEmpty());
            verify(notificationDispatcher, never()).dispatch(any(), any(), anyDouble());
        }
    }

    @Nested
    @DisplayName("CRUD Operations Tests")
    class CrudOperationsTests {

        @Test
        @DisplayName("Should create alert")
        void shouldCreateAlert() {
            when(alertConfigRepository.save(testAlert)).thenReturn(testAlert);

            AlertConfig result = service.createAlert(testAlert);

            assertEquals("alert-123", result.getId());
            verify(alertConfigRepository).save(testAlert);
        }

        @Test
        @DisplayName("Should update alert")
        void shouldUpdateAlert() {
            testAlert.setThreshold(0.8);
            when(alertConfigRepository.save(testAlert)).thenReturn(testAlert);

            AlertConfig result = service.updateAlert(testAlert);

            assertEquals(0.8, result.getThreshold());
            verify(alertConfigRepository).save(testAlert);
        }

        @Test
        @DisplayName("Should delete alert")
        void shouldDeleteAlert() {
            service.deleteAlert("alert-123");

            verify(alertConfigRepository).deleteById("alert-123");
        }

        @Test
        @DisplayName("Should find alert by id")
        void shouldFindAlertById() {
            when(alertConfigRepository.findById("alert-123")).thenReturn(Optional.of(testAlert));

            Optional<AlertConfig> result = service.findById("alert-123");

            assertTrue(result.isPresent());
            assertEquals("alert-123", result.get().getId());
        }

        @Test
        @DisplayName("Should find alerts by benchmark id")
        void shouldFindAlertsByBenchmarkId() {
            when(alertConfigRepository.findByBenchmarkId("benchmark-123")).thenReturn(List.of(testAlert));

            List<AlertConfig> result = service.findAlertsByBenchmarkId("benchmark-123");

            assertEquals(1, result.size());
            assertEquals("benchmark-123", result.get(0).getBenchmarkId());
        }
    }

    @Nested
    @DisplayName("Enable/Disable Tests")
    class EnableDisableTests {

        @Test
        @DisplayName("Should enable alert")
        void shouldEnableAlert() {
            testAlert.setEnabled(false);
            when(alertConfigRepository.findById("alert-123")).thenReturn(Optional.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.enableAlert("alert-123");

            ArgumentCaptor<AlertConfig> captor = ArgumentCaptor.forClass(AlertConfig.class);
            verify(alertConfigRepository).save(captor.capture());
            assertTrue(captor.getValue().getEnabled());
        }

        @Test
        @DisplayName("Should disable alert")
        void shouldDisableAlert() {
            testAlert.setEnabled(true);
            when(alertConfigRepository.findById("alert-123")).thenReturn(Optional.of(testAlert));
            when(alertConfigRepository.save(any(AlertConfig.class))).thenAnswer(i -> i.getArgument(0));

            service.disableAlert("alert-123");

            ArgumentCaptor<AlertConfig> captor = ArgumentCaptor.forClass(AlertConfig.class);
            verify(alertConfigRepository).save(captor.capture());
            assertFalse(captor.getValue().getEnabled());
        }

        @Test
        @DisplayName("Should do nothing when alert not found for enable")
        void shouldDoNothingWhenNotFoundForEnable() {
            when(alertConfigRepository.findById("unknown")).thenReturn(Optional.empty());

            service.enableAlert("unknown");

            verify(alertConfigRepository, never()).save(any());
        }
    }
}
