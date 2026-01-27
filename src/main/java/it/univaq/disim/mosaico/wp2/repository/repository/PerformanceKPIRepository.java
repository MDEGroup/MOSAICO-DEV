package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;

/**
 * Repository interface for PerformanceKPI entities.
 */
@Repository
public interface PerformanceKPIRepository extends JpaRepository<PerformanceKPI, String> {

    PerformanceKPI findByDescription(String description);

    List<PerformanceKPI> findByBenchmark(Benchmark benchmark);

    List<PerformanceKPI> findByBenchmark_Id(String benchmarkId);
}