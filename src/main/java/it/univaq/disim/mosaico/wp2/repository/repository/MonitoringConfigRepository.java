package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.MonitoringConfig;

@Repository
public interface MonitoringConfigRepository extends JpaRepository<MonitoringConfig, String> {
    // Repository base per MonitoringConfig
}