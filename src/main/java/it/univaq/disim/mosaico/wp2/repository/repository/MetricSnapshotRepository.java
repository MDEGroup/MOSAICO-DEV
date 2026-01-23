package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.MetricSnapshot;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricSnapshotRepository extends JpaRepository<MetricSnapshot, String> {

    List<MetricSnapshot> findByRunId(String runId);

    List<MetricSnapshot> findByRunIdAndMetricType(String runId, MetricType metricType);

    List<MetricSnapshot> findByTraceId(String traceId);

    @Query("SELECT ms FROM MetricSnapshot ms WHERE ms.runId = :runId ORDER BY ms.timestamp DESC")
    List<MetricSnapshot> findByRunIdOrderByTimestampDesc(@Param("runId") String runId);

    @Query("SELECT AVG(ms.value) FROM MetricSnapshot ms WHERE ms.runId = :runId AND ms.metricType = :metricType")
    Double findAverageValueByRunIdAndMetricType(@Param("runId") String runId, @Param("metricType") MetricType metricType);

    @Query("SELECT ms FROM MetricSnapshot ms WHERE ms.timestamp >= :since AND ms.metricType = :metricType ORDER BY ms.timestamp")
    List<MetricSnapshot> findByTimestampAfterAndMetricType(@Param("since") Instant since, @Param("metricType") MetricType metricType);
}
