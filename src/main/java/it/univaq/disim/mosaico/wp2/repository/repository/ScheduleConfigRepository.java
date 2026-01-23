package it.univaq.disim.mosaico.wp2.repository.repository;

import it.univaq.disim.mosaico.wp2.repository.data.ScheduleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleConfigRepository extends JpaRepository<ScheduleConfig, String> {

    List<ScheduleConfig> findByBenchmarkId(String benchmarkId);

    List<ScheduleConfig> findByAgentId(String agentId);

    List<ScheduleConfig> findByEnabled(Boolean enabled);

    Optional<ScheduleConfig> findByBenchmarkIdAndAgentId(String benchmarkId, String agentId);

    @Query("SELECT sc FROM ScheduleConfig sc WHERE sc.enabled = true AND sc.nextRunAt <= :now")
    List<ScheduleConfig> findDueSchedules(@Param("now") Instant now);

    @Query("SELECT sc FROM ScheduleConfig sc WHERE sc.enabled = true ORDER BY sc.nextRunAt ASC")
    List<ScheduleConfig> findAllEnabledOrderByNextRunAt();

    @Query("SELECT sc FROM ScheduleConfig sc WHERE sc.consecutiveFailures >= sc.maxConsecutiveFailures AND sc.enabled = true")
    List<ScheduleConfig> findSchedulesExceedingFailureThreshold();
}
