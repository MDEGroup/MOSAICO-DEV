package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.MonitoringConfig;

@Service
public interface MonitoringConfigService {
    /*
     * This interface defines the contract for the MonitoringConfigService.
     */

    /*
     * This method retrieves all monitoring configurations from the repository.
     * @return a list of all monitoring configurations
     */
    public List<MonitoringConfig> findAll();
    
    /*
     * This method retrieves a monitoring configuration by its id from the repository.
     * @param id - the id of the monitoring configuration to be retrieved
     * @return the monitoring configuration with the given id
     * @throws MonitoringConfigNotFoundException - if the monitoring configuration does not exist
     */
    public MonitoringConfig findById(String id);
    
    /*
     * This method saves the given monitoring configuration to the persistence store. If the monitoring configuration already exists, it is updated.
     * @param monitoringConfig - the monitoring configuration to be saved
     * @return the saved monitoring configuration
     */
    public MonitoringConfig save(MonitoringConfig monitoringConfig);
    
    /* 
     * This method deletes a monitoring configuration by its id from the repository. If the monitoring configuration does not exist, it does nothing.
     * @param id - the id of the monitoring configuration to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given monitoring configuration in the persistence store. If the monitoring configuration does not exist, it throws a MonitoringConfigNotFoundException.   
     * @param monitoringConfig - the monitoring configuration to be updated
     * @return the updated monitoring configuration
     * @throws MonitoringConfigNotFoundException - if the monitoring configuration does not exist
     */
    public MonitoringConfig update(MonitoringConfig monitoringConfig);
}
