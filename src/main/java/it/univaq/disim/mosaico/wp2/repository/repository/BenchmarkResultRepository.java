package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.BenchmarkResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BenchmarkResultRepository extends JpaRepository<BenchmarkResult, String> {

    @Query("SELECT br FROM BenchmarkResult br WHERE br.benchmarkRun.id = :runId")
    List<BenchmarkResult> findByRunId(@Param("runId") String runId);

    List<BenchmarkResult> findByTraceId(String traceId);

    @Query("SELECT br FROM BenchmarkResult br WHERE br.benchmarkRun.id = :runId ORDER BY br.createdAt DESC")
    List<BenchmarkResult> findByRunIdOrderByCreatedAtDesc(@Param("runId") String runId);

    @Query("SELECT COUNT(br) FROM BenchmarkResult br WHERE br.benchmarkRun.id = :runId")
    long countByRunId(@Param("runId") String runId);
}
