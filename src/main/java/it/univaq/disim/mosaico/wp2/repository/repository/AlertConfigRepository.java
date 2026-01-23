package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.AlertConfig;
import it.univaq.disim.mosaico.wp2.repository.data.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfig, String> {

    List<AlertConfig> findByBenchmarkId(String benchmarkId);

    List<AlertConfig> findByAgentId(String agentId);

    List<AlertConfig> findByKpiName(String kpiName);

    List<AlertConfig> findByEnabled(Boolean enabled);

    List<AlertConfig> findByBenchmarkIdAndEnabled(String benchmarkId, Boolean enabled);

    List<AlertConfig> findBySeverity(Severity severity);

    @Query("SELECT ac FROM AlertConfig ac WHERE ac.benchmarkId = :benchmarkId AND ac.kpiName = :kpiName AND ac.enabled = true")
    List<AlertConfig> findActiveAlertsForKpi(@Param("benchmarkId") String benchmarkId, @Param("kpiName") String kpiName);

    @Query("SELECT ac FROM AlertConfig ac WHERE ac.enabled = true AND (ac.benchmarkId = :benchmarkId OR ac.benchmarkId IS NULL)")
    List<AlertConfig> findActiveAlertsForBenchmark(@Param("benchmarkId") String benchmarkId);
}
