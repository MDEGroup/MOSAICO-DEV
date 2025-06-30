package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.MonitoringConfig;

@Repository
public interface MonitoringConfigRepository extends MongoRepository<MonitoringConfig, String> {
    // Repository base per MonitoringConfig
}