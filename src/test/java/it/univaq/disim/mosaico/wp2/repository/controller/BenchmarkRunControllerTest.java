package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkOrchestrator;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkRunManager;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for BenchmarkRunController.
 *
 * Test Plan:
 * 1. Trigger benchmark run
 * 2. Get benchmark run by id
 * 3. List benchmark runs with filters
 * 4. Get run history
 * 5. Cancel benchmark run
 * 6. Retry benchmark run
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BenchmarkRunController Tests")
class BenchmarkRunControllerTest {

    @Mock
    private BenchmarkRunManager runManager;
    @Mock
    private BenchmarkOrchestrator orchestrator;

    @InjectMocks
    private BenchmarkRunController controller;

    private BenchmarkRun testRun;

    @BeforeEach
    void setUp() {
        testRun = new BenchmarkRun("benchmark-123", "agent-456", TriggerType.MANUAL);
        testRun.setId("run-789");
    }

    @Nested
    @DisplayName("triggerRun Tests")
    class TriggerRunTests {

        @Test
        @DisplayName("Should trigger benchmark run")
        void shouldTriggerRun() {
            when(runManager.createRun(anyString(), anyString(), any(), anyString(), any()))
                .thenReturn(testRun);

            Map<String, String> request = Map.of(
                "benchmarkId", "benchmark-123",
                "agentId", "agent-456"
            );

            ResponseEntity<BenchmarkRun> response = controller.triggerRun(request);

            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("run-789", response.getBody().getId());
            verify(orchestrator).executeBenchmarkRunAsync("run-789");
        }

        @Test
        @DisplayName("Should return bad request when missing benchmarkId")
        void shouldReturnBadRequestWhenMissingBenchmarkId() {
            Map<String, String> request = Map.of("agentId", "agent-456");

            ResponseEntity<BenchmarkRun> response = controller.triggerRun(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Should return bad request when missing agentId")
        void shouldReturnBadRequestWhenMissingAgentId() {
            Map<String, String> request = Map.of("benchmarkId", "benchmark-123");

            ResponseEntity<BenchmarkRun> response = controller.triggerRun(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getRun Tests")
    class GetRunTests {

        @Test
        @DisplayName("Should return run when found")
        void shouldReturnRunWhenFound() {
            when(runManager.findById("run-789")).thenReturn(Optional.of(testRun));

            ResponseEntity<BenchmarkRun> response = controller.getRun("run-789");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("run-789", response.getBody().getId());
        }

        @Test
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() {
            when(runManager.findById("unknown")).thenReturn(Optional.empty());

            ResponseEntity<BenchmarkRun> response = controller.getRun("unknown");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getRunsByBenchmark Tests")
    class GetRunsByBenchmarkTests {

        @Test
        @DisplayName("Should list runs by benchmark id")
        void shouldListRunsByBenchmarkId() {
            when(runManager.findByBenchmarkId("benchmark-123")).thenReturn(List.of(testRun));

            ResponseEntity<List<BenchmarkRun>> response = controller.getRunsByBenchmark("benchmark-123", null, null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Should list runs by agent id")
        void shouldListRunsByAgentId() {
            when(runManager.findByAgentId("agent-456")).thenReturn(List.of(testRun));

            ResponseEntity<List<BenchmarkRun>> response = controller.getRunsByBenchmark(null, "agent-456", null);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Should list runs by status")
        void shouldListRunsByStatus() {
            when(runManager.findByStatus(RunStatus.PENDING)).thenReturn(List.of(testRun));

            ResponseEntity<List<BenchmarkRun>> response = controller.getRunsByBenchmark(null, null, RunStatus.PENDING);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Should return bad request when no filter provided")
        void shouldReturnBadRequestWhenNoFilter() {
            ResponseEntity<List<BenchmarkRun>> response = controller.getRunsByBenchmark(null, null, null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("getRunHistory Tests")
    class GetRunHistoryTests {

        @Test
        @DisplayName("Should return run history")
        void shouldReturnRunHistory() {
            when(runManager.getRunHistory("benchmark-123", "agent-456", 10)).thenReturn(List.of(testRun));

            ResponseEntity<List<BenchmarkRun>> response = controller.getRunHistory("benchmark-123", "agent-456", 10);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("cancelRun Tests")
    class CancelRunTests {

        @Test
        @DisplayName("Should cancel benchmark run")
        void shouldCancelRun() {
            testRun.setStatus(RunStatus.CANCELLED);
            when(runManager.findById("run-789")).thenReturn(Optional.of(testRun));

            ResponseEntity<BenchmarkRun> response = controller.cancelRun("run-789");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(orchestrator).cancelBenchmarkRun("run-789");
        }

        @Test
        @DisplayName("Should return 404 when run not found after cancel")
        void shouldReturn404WhenNotFoundAfterCancel() {
            when(runManager.findById("unknown")).thenReturn(Optional.empty());

            ResponseEntity<BenchmarkRun> response = controller.cancelRun("unknown");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("retryRun Tests")
    class RetryRunTests {

        @Test
        @DisplayName("Should retry benchmark run")
        void shouldRetryRun() {
            when(orchestrator.retryBenchmarkRun("run-789")).thenReturn("retry-run-id");

            ResponseEntity<Map<String, String>> response = controller.retryRun("run-789");

            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("retry-run-id", response.getBody().get("runId"));
            verify(orchestrator).executeBenchmarkRunAsync("retry-run-id");
        }

        @Test
        @DisplayName("Should return bad request when max retries exceeded")
        void shouldReturnBadRequestWhenMaxRetriesExceeded() {
            when(orchestrator.retryBenchmarkRun("run-789"))
                .thenThrow(new IllegalStateException("Max retries exceeded"));

            ResponseEntity<Map<String, String>> response = controller.retryRun("run-789");

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().containsKey("error"));
        }
    }
}
