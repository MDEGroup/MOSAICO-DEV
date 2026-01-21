package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;

/**
 * Service for dispatching notifications through various channels.
 */
public interface NotificationDispatcher {

    /**
     * Dispatches an alert notification to all configured channels.
     */
    void dispatch(AlertConfig alert, String kpiName, double value);

    /**
     * Sends an email notification.
     */
    void sendEmail(String recipient, String subject, String body);

    /**
     * Sends a Slack message.
     */
    void sendSlackMessage(String channel, String message);

    /**
     * Sends a webhook notification.
     */
    void sendWebhook(String url, String payload);
}
