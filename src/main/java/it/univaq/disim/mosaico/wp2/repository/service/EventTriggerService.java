package it.univaq.disim.mosaico.wp2.repository.service;

import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;

/**
 * Service for handling event-based benchmark triggers.
 * Listens to various events and triggers benchmark runs accordingly.
 */
public interface EventTriggerService {

    /**
     * Triggers a benchmark run for a specific benchmark and agent.
     *
     * @param benchmarkId the benchmark to run
     * @param agentId the agent to evaluate
     * @param triggerType the type of trigger
     * @param triggeredBy optional user/system identifier
     * @return the created run ID
     */
    String triggerBenchmarkRun(String benchmarkId, String agentId, TriggerType triggerType, String triggeredBy);

    /**
     * Handles an agent update event.
     * May trigger benchmarks configured to run on agent changes.
     */
    void onAgentUpdated(String agentId);

    /**
     * Handles a dataset update event.
     * May trigger benchmarks using this dataset.
     */
    void onDatasetUpdated(String datasetRef);

    /**
     * Handles a new trace event from Langfuse.
     * May trigger benchmarks configured for real-time evaluation.
     */
    void onNewTrace(String agentId, String traceId);

    /**
     * Handles a webhook trigger from external systems.
     */
    String onWebhookTrigger(String benchmarkId, String agentId, String payload);
}
