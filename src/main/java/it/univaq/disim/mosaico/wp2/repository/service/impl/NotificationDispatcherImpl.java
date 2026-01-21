package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.enums.NotificationChannel;
import it.univaq.disim.mosaico.wp2.repository.service.NotificationDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of NotificationDispatcher.
 * Placeholder implementation - actual integrations to be added.
 */
@Service
public class NotificationDispatcherImpl implements NotificationDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDispatcherImpl.class);

    @Override
    public void dispatch(AlertConfig alert, String kpiName, double value) {
        String message = formatAlertMessage(alert, kpiName, value);

        for (NotificationChannel channel : alert.getChannels()) {
            try {
                switch (channel) {
                    case EMAIL -> {
                        for (String recipient : alert.getRecipients()) {
                            sendEmail(recipient, "Alert: " + alert.getName(), message);
                        }
                    }
                    case SLACK -> sendSlackMessage(null, message);
                    case TEAMS -> logger.info("Teams notification (not implemented): {}", message);
                    case WEBHOOK -> {
                        if (alert.getWebhookUrl() != null) {
                            sendWebhook(alert.getWebhookUrl(), message);
                        }
                    }
                    case IN_APP -> logger.info("In-app notification: {}", message);
                }
            } catch (Exception e) {
                logger.error("Failed to send {} notification for alert {}: {}",
                    channel, alert.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void sendEmail(String recipient, String subject, String body) {
        // Placeholder - integrate with email service
        logger.info("Email notification to {}: {} - {}", recipient, subject, body);
    }

    @Override
    public void sendSlackMessage(String channel, String message) {
        // Placeholder - integrate with Slack API
        logger.info("Slack notification: {}", message);
    }

    @Override
    public void sendWebhook(String url, String payload) {
        // Placeholder - integrate with HTTP client
        logger.info("Webhook notification to {}: {}", url, payload);
    }

    private String formatAlertMessage(AlertConfig alert, String kpiName, double value) {
        return String.format(
            "[%s] Alert: %s%n" +
            "KPI: %s%n" +
            "Value: %.4f%n" +
            "Threshold: %.4f%n" +
            "Condition: %s",
            alert.getSeverity(),
            alert.getName(),
            kpiName,
            value,
            alert.getThreshold(),
            alert.getCondition()
        );
    }
}
