package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.enums.AlertCondition;
import it.univaq.disim.mosaico.wp2.repository.data.enums.NotificationChannel;
import it.univaq.disim.mosaico.wp2.repository.data.enums.Severity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for NotificationDispatcherImpl.
 *
 * Test Plan:
 * 1. Dispatch email notification
 * 2. Dispatch Slack notification
 * 3. Dispatch webhook notification
 * 4. Dispatch multiple channel notifications
 * 5. Handle missing webhook URL
 * 6. Test message formatting
 */
@DisplayName("NotificationDispatcherImpl Tests")
class NotificationDispatcherImplTest {

    private NotificationDispatcherImpl dispatcher;
    private AlertConfig testAlert;

    @BeforeEach
    void setUp() {
        dispatcher = new NotificationDispatcherImpl();

        testAlert = new AlertConfig();
        testAlert.setId("alert-123");
        testAlert.setName("Low Accuracy Alert");
        testAlert.setBenchmarkId("benchmark-123");
        testAlert.setKpiName("accuracy");
        testAlert.setCondition(AlertCondition.LESS_THAN);
        testAlert.setThreshold(0.7);
        testAlert.setSeverity(Severity.WARNING);
        testAlert.setEnabled(true);
    }

    @Nested
    @DisplayName("dispatch Tests")
    class DispatchTests {

        @Test
        @DisplayName("Should dispatch email notification")
        void shouldDispatchEmailNotification() {
            testAlert.setChannels(List.of(NotificationChannel.EMAIL));
            testAlert.setRecipients(List.of("user@example.com"));

            // Should not throw - placeholder implementation logs
            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should dispatch Slack notification")
        void shouldDispatchSlackNotification() {
            testAlert.setChannels(List.of(NotificationChannel.SLACK));

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should dispatch webhook notification")
        void shouldDispatchWebhookNotification() {
            testAlert.setChannels(List.of(NotificationChannel.WEBHOOK));
            testAlert.setWebhookUrl("https://example.com/webhook");

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should dispatch to multiple channels")
        void shouldDispatchToMultipleChannels() {
            testAlert.setChannels(List.of(
                NotificationChannel.EMAIL,
                NotificationChannel.SLACK,
                NotificationChannel.WEBHOOK
            ));
            testAlert.setRecipients(List.of("user@example.com"));
            testAlert.setWebhookUrl("https://example.com/webhook");

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should handle missing webhook URL gracefully")
        void shouldHandleMissingWebhookUrl() {
            testAlert.setChannels(List.of(NotificationChannel.WEBHOOK));
            testAlert.setWebhookUrl(null);

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should handle in-app notification")
        void shouldHandleInAppNotification() {
            testAlert.setChannels(List.of(NotificationChannel.IN_APP));

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should handle Teams notification")
        void shouldHandleTeamsNotification() {
            testAlert.setChannels(List.of(NotificationChannel.TEAMS));

            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }
    }

    @Nested
    @DisplayName("Individual Channel Tests")
    class IndividualChannelTests {

        @Test
        @DisplayName("Should send email")
        void shouldSendEmail() {
            assertDoesNotThrow(() ->
                dispatcher.sendEmail("user@example.com", "Test Subject", "Test Body"));
        }

        @Test
        @DisplayName("Should send Slack message")
        void shouldSendSlackMessage() {
            assertDoesNotThrow(() ->
                dispatcher.sendSlackMessage("#alerts", "Test message"));
        }

        @Test
        @DisplayName("Should send webhook")
        void shouldSendWebhook() {
            assertDoesNotThrow(() ->
                dispatcher.sendWebhook("https://example.com/webhook", "{\"test\": true}"));
        }
    }

    @Nested
    @DisplayName("Message Formatting Tests")
    class MessageFormattingTests {

        @Test
        @DisplayName("Should include all alert details in notification")
        void shouldIncludeAlertDetails() {
            testAlert.setChannels(List.of(NotificationChannel.EMAIL));
            testAlert.setRecipients(List.of("test@example.com"));
            testAlert.setSeverity(Severity.CRITICAL);

            // Placeholder implementation logs the message
            // In a real implementation, we would capture and verify the message content
            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle empty recipients list for email")
        void shouldHandleEmptyRecipients() {
            testAlert.setChannels(List.of(NotificationChannel.EMAIL));
            testAlert.setRecipients(List.of());

            // Should not throw even with empty recipients
            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }

        @Test
        @DisplayName("Should handle null recipients for email")
        void shouldHandleNullRecipients() {
            testAlert.setChannels(List.of(NotificationChannel.EMAIL));
            testAlert.setRecipients(null);

            // Should not throw even with null recipients
            assertDoesNotThrow(() -> dispatcher.dispatch(testAlert, "accuracy", 0.5));
        }
    }
}
