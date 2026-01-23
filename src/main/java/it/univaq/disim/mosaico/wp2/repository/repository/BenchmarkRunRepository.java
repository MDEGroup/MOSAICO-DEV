package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkRun;
import it.univaq.disim.mosaico.wp2.repository.data.enums.RunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BenchmarkRunRepository extends JpaRepository<BenchmarkRun, String> {

    List<BenchmarkRun> findByBenchmarkId(String benchmarkId);

    List<BenchmarkRun> findByAgentId(String agentId);

    List<BenchmarkRun> findByBenchmarkIdAndAgentId(String benchmarkId, String agentId);

    List<BenchmarkRun> findByStatus(RunStatus status);

    List<BenchmarkRun> findByBenchmarkIdAndStatus(String benchmarkId, RunStatus status);

    @Query("SELECT br FROM BenchmarkRun br WHERE br.benchmarkId = :benchmarkId ORDER BY br.startedAt DESC")
    List<BenchmarkRun> findByBenchmarkIdOrderByStartedAtDesc(@Param("benchmarkId") String benchmarkId);

    @Query("SELECT br FROM BenchmarkRun br WHERE br.agentId = :agentId ORDER BY br.startedAt DESC")
    List<BenchmarkRun> findByAgentIdOrderByStartedAtDesc(@Param("agentId") String agentId);

    @Query("SELECT br FROM BenchmarkRun br WHERE br.startedAt >= :since AND br.status = :status")
    List<BenchmarkRun> findByStartedAtAfterAndStatus(@Param("since") Instant since, @Param("status") RunStatus status);

    @Query("SELECT br FROM BenchmarkRun br WHERE br.scheduleConfigId = :scheduleConfigId ORDER BY br.startedAt DESC")
    List<BenchmarkRun> findByScheduleConfigIdOrderByStartedAtDesc(@Param("scheduleConfigId") String scheduleConfigId);

    @Query("SELECT COUNT(br) FROM BenchmarkRun br WHERE br.benchmarkId = :benchmarkId AND br.status = :status")
    long countByBenchmarkIdAndStatus(@Param("benchmarkId") String benchmarkId, @Param("status") RunStatus status);
}
