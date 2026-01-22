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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for EventTriggerServiceImpl.
 *
 * Test Plan:
 * 1. Trigger benchmark run manually
 * 2. Trigger benchmark run via webhook
 * 3. Handle agent updated events
 * 4. Handle dataset updated events
 * 5. Handle new trace events
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventTriggerServiceImpl Tests")
class EventTriggerServiceImplTest {

    @Mock
    private BenchmarkRunRepository runRepository;

    @InjectMocks
    private EventTriggerServiceImpl service;

    private BenchmarkRun testRun;

    @BeforeEach
    void setUp() {
        testRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
        testRun.setId("run-789");
    }

    @Nested
    @DisplayName("triggerBenchmarkRun Tests")
    class TriggerBenchmarkRunTests {

        @Test
        @DisplayName("Should create benchmark run with manual trigger")
        void shouldCreateRunWithManualTrigger() {
            when(runRepository.save(any(BenchmarkRun.class))).thenAnswer(invocation -> {
                BenchmarkRun run = invocation.getArgument(0);
                run.setId("generated-id");
                return run;
            });

            String runId = service.triggerBenchmarkRun(
                "benchmark-123", "agent-456", TriggerType.MANUAL, "user@example.com");

            assertNotNull(runId);
            assertEquals("generated-id", runId);

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(runRepository).save(captor.capture());
            assertEquals("benchmark-123", captor.getValue().getBenchmarkId());
            assertEquals("agent-456", captor.getValue().getAgentId());
            assertEquals(TriggerType.MANUAL, captor.getValue().getTriggeredBy());
            assertEquals("user@example.com", captor.getValue().getTriggeredByUser());
            assertEquals(RunStatus.PENDING, captor.getValue().getStatus());
        }

        @Test
        @DisplayName("Should create benchmark run with scheduled trigger")
        void shouldCreateRunWithScheduledTrigger() {
            when(runRepository.save(any(BenchmarkRun.class))).thenAnswer(invocation -> {
                BenchmarkRun run = invocation.getArgument(0);
                run.setId("scheduled-run-id");
                return run;
            });

            String runId = service.triggerBenchmarkRun(
                "benchmark-123", "agent-456", TriggerType.SCHEDULED, "scheduler");

            assertNotNull(runId);

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(runRepository).save(captor.capture());
            assertEquals(TriggerType.SCHEDULED, captor.getValue().getTriggeredBy());
            assertEquals("scheduler", captor.getValue().getTriggeredByUser());
        }

        @Test
        @DisplayName("Should create benchmark run with event trigger")
        void shouldCreateRunWithEventTrigger() {
            when(runRepository.save(any(BenchmarkRun.class))).thenAnswer(invocation -> {
                BenchmarkRun run = invocation.getArgument(0);
                run.setId("event-run-id");
                return run;
            });

            String runId = service.triggerBenchmarkRun(
                "benchmark-123", "agent-456", TriggerType.EVENT, "agent-update-listener");

            assertNotNull(runId);

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(runRepository).save(captor.capture());
            assertEquals(TriggerType.EVENT, captor.getValue().getTriggeredBy());
        }
    }

    @Nested
    @DisplayName("onWebhookTrigger Tests")
    class WebhookTriggerTests {

        @Test
        @DisplayName("Should trigger benchmark run via webhook")
        void shouldTriggerViaWebhook() {
            when(runRepository.save(any(BenchmarkRun.class))).thenAnswer(invocation -> {
                BenchmarkRun run = invocation.getArgument(0);
                run.setId("webhook-run-id");
                return run;
            });

            String runId = service.onWebhookTrigger(
                "benchmark-123", "agent-456", "{\"event\": \"deploy\"}");

            assertNotNull(runId);
            assertEquals("webhook-run-id", runId);

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(runRepository).save(captor.capture());
            assertEquals(TriggerType.WEBHOOK, captor.getValue().getTriggeredBy());
            assertEquals("webhook", captor.getValue().getTriggeredByUser());
        }
    }

    @Nested
    @DisplayName("Event Handler Tests")
    class EventHandlerTests {

        @Test
        @DisplayName("Should handle agent updated event")
        void shouldHandleAgentUpdatedEvent() {
            // Currently a placeholder - test that method doesn't throw
            assertDoesNotThrow(() -> service.onAgentUpdated("agent-456"));
        }

        @Test
        @DisplayName("Should handle dataset updated event")
        void shouldHandleDatasetUpdatedEvent() {
            // Currently a placeholder - test that method doesn't throw
            assertDoesNotThrow(() -> service.onDatasetUpdated("dataset-ref-123"));
        }

        @Test
        @DisplayName("Should handle new trace event")
        void shouldHandleNewTraceEvent() {
            // Currently a placeholder - test that method doesn't throw
            assertDoesNotThrow(() -> service.onNewTrace("agent-456", "trace-123"));
        }
    }

    @Nested
    @DisplayName("Run Initialization Tests")
    class RunInitializationTests {

        @Test
        @DisplayName("Should initialize run with PENDING status")
        void shouldInitializeRunWithPendingStatus() {
            when(runRepository.save(any(BenchmarkRun.class))).thenAnswer(invocation -> {
                BenchmarkRun run = invocation.getArgument(0);
                run.setId("new-run-id");
                return run;
            });

            service.triggerBenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL, "user");

            ArgumentCaptor<BenchmarkRun> captor = ArgumentCaptor.forClass(BenchmarkRun.class);
            verify(runRepository).save(captor.capture());
            assertEquals(RunStatus.PENDING, captor.getValue().getStatus());
            assertEquals(0, captor.getValue().getRetryCount());
        }
    }
}
