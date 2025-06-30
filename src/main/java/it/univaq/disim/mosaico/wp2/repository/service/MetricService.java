package it.univaq.disim.mosaico.wp2.repository.service;
import java.util.List;

import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;

@Service
public interface MetricService {
    /*
     * This interface defines the contract for the MetricService.
     */

    /*
     * This method retrieves all metrics from the repository.
     * @return a list of all metrics
     */
    public List<Metric> findAll();
    
    /*
     * This method retrieves a metric by its id from the repository.
     * @param id - the id of the metric to be retrieved
     * @return the metric with the given id
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric findById(String id);
    
    /*
     * This method saves the given metric to the persistence store. If the metric already exists, it is updated.
     * @param metric - the metric to be saved
     * @return the saved metric
     */
    public Metric save(Metric metric);
    
    /* 
     * This method deletes a metric by its id from the repository. If the metric does not exist, it does nothing.
     * @param id - the id of the metric to be deleted
     */
    public void deleteById(String id);
    
    /*
     * This method updates the given metric in the persistence store. If the metric does not exist, it throws a MetricNotFoundException.   
     * @param metric - the metric to be updated
     * @return the updated metric
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric update(Metric metric);
}
