package it.univaq.disim.mosaico.wp2.repository.service.impl;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import it.univaq.disim.mosaico.wp2.repository.data.enums.TriggerType;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkRunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BenchmarkRunManagerImpl implements BenchmarkRunManager {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkRunManagerImpl.class);

    private final BenchmarkRunRepository repository;

    public BenchmarkRunManagerImpl(BenchmarkRunRepository repository) {
        this.repository = repository;
    }

    @Override
    public BenchmarkRun createRun(String benchmarkId, String agentId, TriggerType triggerType, String triggeredBy) {
        BenchmarkRun run = new BenchmarkRun(benchmarkId, agentId, triggerType);
        run.setTriggeredByUser(triggeredBy);
        run = repository.save(run);
        logger.info("Created benchmark run: {}", run.getId());
        return run;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BenchmarkRun> findById(String runId) {
        return repository.findById(runId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkRun> findByBenchmarkId(String benchmarkId) {
        return repository.findByBenchmarkIdOrderByStartedAtDesc(benchmarkId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkRun> findByAgentId(String agentId) {
        return repository.findByAgentIdOrderByStartedAtDesc(agentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkRun> findByStatus(RunStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenchmarkRun> getRunHistory(String benchmarkId, String agentId, int limit) {
        return repository.findByBenchmarkIdAndAgentId(benchmarkId, agentId)
            .stream()
            .limit(limit)
            .toList();
    }

    @Override
    public BenchmarkRun startRun(String runId) {
        BenchmarkRun run = repository.findById(runId)
            .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        run.start();
        run = repository.save(run);
        logger.info("Started benchmark run: {}", runId);
        return run;
    }

    @Override
    public BenchmarkRun completeRun(String runId, int tracesProcessed, int metricsComputed) {
        BenchmarkRun run = repository.findById(runId)
            .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        run.setTracesProcessed(tracesProcessed);
        run.setMetricsComputed(metricsComputed);
        run.complete();
        run = repository.save(run);
        logger.info("Completed benchmark run: {} (traces={}, metrics={})", runId, tracesProcessed, metricsComputed);
        return run;
    }

    @Override
    public BenchmarkRun failRun(String runId, String errorMessage) {
        BenchmarkRun run = repository.findById(runId)
            .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        run.fail(errorMessage);
        run = repository.save(run);
        logger.error("Failed benchmark run: {} - {}", runId, errorMessage);
        return run;
    }

    @Override
    public BenchmarkRun cancelRun(String runId) {
        BenchmarkRun run = repository.findById(runId)
            .orElseThrow(() -> new IllegalArgumentException("Run not found: " + runId));
        run.cancel();
        run = repository.save(run);
        logger.info("Cancelled benchmark run: {}", runId);
        return run;
    }

    @Override
    public void updateProgress(String runId, int tracesProcessed, int metricsComputed) {
        repository.findById(runId).ifPresent(run -> {
            run.setTracesProcessed(tracesProcessed);
            run.setMetricsComputed(metricsComputed);
            repository.save(run);
        });
    }
}
