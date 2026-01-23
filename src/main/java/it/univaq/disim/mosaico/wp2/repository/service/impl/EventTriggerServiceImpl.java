package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.service.EventTriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of EventTriggerService.
 */
@Service
@Transactional
public class EventTriggerServiceImpl implements EventTriggerService {

    private static final Logger logger = LoggerFactory.getLogger(EventTriggerServiceImpl.class);

    private final BenchmarkRunRepository runRepository;

    public EventTriggerServiceImpl(BenchmarkRunRepository runRepository) {
        this.runRepository = runRepository;
    }

    @Override
    public String triggerBenchmarkRun(String benchmarkId, String agentId, TriggerType triggerType, String triggeredBy) {
        logger.info("Triggering benchmark run: benchmark={}, agent={}, type={}, by={}",
            benchmarkId, agentId, triggerType, triggeredBy);

        BenchmarkRun run = new BenchmarkRun(benchmarkId, agentId, triggerType);
        run.setTriggeredByUser(triggeredBy);
        run = runRepository.save(run);

        logger.info("Created benchmark run: {}", run.getId());
        return run.getId();
    }

    @Override
    public void onAgentUpdated(String agentId) {
        logger.info("Agent updated event received: {}", agentId);
        // TODO: Query for benchmarks configured to trigger on agent update
        // and trigger runs for each
    }

    @Override
    public void onDatasetUpdated(String datasetRef) {
        logger.info("Dataset updated event received: {}", datasetRef);
        // TODO: Query for benchmarks using this dataset
        // and trigger runs for each
    }

    @Override
    public void onNewTrace(String agentId, String traceId) {
        logger.debug("New trace event received: agent={}, trace={}", agentId, traceId);
        // TODO: Implement real-time evaluation if configured
    }

    @Override
    public String onWebhookTrigger(String benchmarkId, String agentId, String payload) {
        logger.info("Webhook trigger received: benchmark={}, agent={}", benchmarkId, agentId);
        return triggerBenchmarkRun(benchmarkId, agentId, TriggerType.WEBHOOK, "webhook");
    }
}
