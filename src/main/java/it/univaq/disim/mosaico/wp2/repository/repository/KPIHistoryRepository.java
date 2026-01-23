package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.KPIHistory;
import it.univaq.disim.mosaico.wp2.repository.data.enums.KPIStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface KPIHistoryRepository extends JpaRepository<KPIHistory, String> {

    List<KPIHistory> findByBenchmarkIdAndAgentId(String benchmarkId, String agentId);

    List<KPIHistory> findByBenchmarkIdAndKpiName(String benchmarkId, String kpiName);

    List<KPIHistory> findByAgentIdAndKpiName(String agentId, String kpiName);

    List<KPIHistory> findByStatus(KPIStatus status);

    @Query("SELECT kh FROM KPIHistory kh WHERE kh.benchmarkId = :benchmarkId AND kh.agentId = :agentId ORDER BY kh.recordedAt DESC")
    List<KPIHistory> findByBenchmarkIdAndAgentIdOrderByRecordedAtDesc(
        @Param("benchmarkId") String benchmarkId, @Param("agentId") String agentId);

    @Query("SELECT kh FROM KPIHistory kh WHERE kh.benchmarkId = :benchmarkId AND kh.agentId = :agentId AND kh.kpiName = :kpiName ORDER BY kh.recordedAt DESC")
    List<KPIHistory> findHistoryForKpi(
        @Param("benchmarkId") String benchmarkId,
        @Param("agentId") String agentId,
        @Param("kpiName") String kpiName);

    @Query("SELECT kh FROM KPIHistory kh WHERE kh.benchmarkId = :benchmarkId AND kh.agentId = :agentId AND kh.kpiName = :kpiName ORDER BY kh.recordedAt DESC LIMIT 1")
    Optional<KPIHistory> findLatestForKpi(
        @Param("benchmarkId") String benchmarkId,
        @Param("agentId") String agentId,
        @Param("kpiName") String kpiName);

    @Query("SELECT kh FROM KPIHistory kh WHERE kh.recordedAt >= :since AND kh.status IN :statuses")
    List<KPIHistory> findByRecordedAtAfterAndStatusIn(@Param("since") Instant since, @Param("statuses") List<KPIStatus> statuses);

    @Query("SELECT AVG(kh.value) FROM KPIHistory kh WHERE kh.benchmarkId = :benchmarkId AND kh.agentId = :agentId AND kh.kpiName = :kpiName")
    Double findAverageValueForKpi(
        @Param("benchmarkId") String benchmarkId,
        @Param("agentId") String agentId,
        @Param("kpiName") String kpiName);
}
