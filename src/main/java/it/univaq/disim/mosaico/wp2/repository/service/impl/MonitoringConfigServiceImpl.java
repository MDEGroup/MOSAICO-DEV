package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.MonitoringConfig;
import it.univaq.disim.mosaico.wp2.repository.repository.MonitoringConfigNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.MonitoringConfigRepository;
import it.univaq.disim.mosaico.wp2.repository.service.MonitoringConfigService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the MonitoringConfig entity in the repository.
 * It provides methods to perform CRUD operations on the MonitoringConfig entity.
 * It uses the MonitoringConfigRepository to interact with the persistence store.
 */
@Service
public class MonitoringConfigServiceImpl implements MonitoringConfigService {

    /*
     * The monitoringConfigRepository is used to perform CRUD operations on the MonitoringConfig entity. It is autowired by Spring.
    * The MonitoringConfigRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final MonitoringConfigRepository monitoringConfigRepository;

    /*
     * This constructor is used to inject the MonitoringConfigRepository dependency into the MonitoringConfigServiceImpl class.
     * The @Autowired annotation is used to indicate that the MonitoringConfigRepository bean should be injected.
     */
    public MonitoringConfigServiceImpl(@Autowired MonitoringConfigRepository monitoringConfigRepository) {
        this.monitoringConfigRepository = monitoringConfigRepository;
    }

    @Override
    /*
     * Retrieves all monitoring configurations from the repository.
     * @return a list of all monitoring configurations
     */
    public List<MonitoringConfig> findAll() {
        return monitoringConfigRepository.findAll();
    }

    @Override
    /*
     * Retrieves a monitoring configuration by its id from the repository.
     * @param id - the id of the monitoring configuration to be retrieved
     * @return the monitoring configuration with the given id
     * @throws MonitoringConfigNotFoundException - if the monitoring configuration does not exist
     */
    public MonitoringConfig findById(String id) {
        return monitoringConfigRepository.findById(id)
                .orElseThrow(() -> new MonitoringConfigNotFoundException(id));
    }

    @Override
    /*
     * Saves the given monitoring configuration to the persistence store. If the monitoring configuration already exists, it is updated.
     * @param monitoringConfig - the monitoring configuration to be saved
     * @return the saved monitoring configuration
     */
    public MonitoringConfig save(MonitoringConfig monitoringConfig) {
        return monitoringConfigRepository.save(monitoringConfig);
    }

    @Override
    /*
     * Deletes the monitoring configuration with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the monitoring configuration to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        monitoringConfigRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given monitoring configuration in the persistence store. If the monitoring configuration does not exist, it throws a MonitoringConfigNotFoundException.
     * @param monitoringConfig - the monitoring configuration to be updated
     * @return the updated monitoring configuration
     * @throws MonitoringConfigNotFoundException - if the monitoring configuration does not exist
     */
    public MonitoringConfig update(MonitoringConfig monitoringConfig) {
        if (!monitoringConfigRepository.existsById(monitoringConfig.id())) {
            throw new MonitoringConfigNotFoundException(monitoringConfig.id());
        }
        // Update the monitoring configuration in the repository
        return monitoringConfigRepository.save(monitoringConfig); 
    }
}
