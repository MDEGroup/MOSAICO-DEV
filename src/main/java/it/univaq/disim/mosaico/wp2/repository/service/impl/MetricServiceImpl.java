package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricNotFoundException;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricRepository;
import it.univaq.disim.mosaico.wp2.repository.service.MetricService;

/*
 * Copyright 2025 Mosaico
 * This class is used to manage the Metric entity in the repository.
 * It provides methods to perform CRUD operations on the Metric entity.
 * It uses the MetricRepository to interact with the persistence store.
 */
@Service
public class MetricServiceImpl implements MetricService {

    /*
     * The metricRepository is used to perform CRUD operations on the Metric entity. It is autowired by Spring.
    * The MetricRepository interface extends the JpaRepository interface, which provides methods for CRUD operations.
     */
    private final MetricRepository metricRepository;

    /*
     * This constructor is used to inject the MetricRepository dependency into the MetricServiceImpl class.
     * The @Autowired annotation is used to indicate that the MetricRepository bean should be injected.
     */
    public MetricServiceImpl(@Autowired MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @Override
    /*
     * Retrieves all metrics from the repository.
     * @return a list of all metrics
     */
    public List<Metric> findAll() {
        return metricRepository.findAll();
    }

    @Override
    /*
     * Retrieves a metric by its id from the repository.
     * @param id - the id of the metric to be retrieved
     * @return the metric with the given id
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric findById(String id) {
        return metricRepository.findById(id)
                .orElseThrow(() -> new MetricNotFoundException(id));
    }

    @Override
    /*
     * Saves the given metric to the persistence store. If the metric already exists, it is updated.
     * @param metric - the metric to be saved
     * @return the saved metric
     */
    public Metric save(Metric metric) {
        return metricRepository.save(metric);
    }

    @Override
    /*
     * Deletes the metric with the given id from the repository. If the entity is not found in the persistence store it is silently ignored.
     * @param id - the id of the metric to be deleted
     * @throws IllegalArgumentException - in case the given id is null
     */
    public void deleteById(String id) {
        metricRepository.deleteById(id);
    }

    @Override
    /*
     * Updates the given metric in the persistence store. If the metric does not exist, it throws a MetricNotFoundException.
     * @param metric - the metric to be updated
     * @return the updated metric
     * @throws MetricNotFoundException - if the metric does not exist
     */
    public Metric update(Metric metric) {
        if (!metricRepository.existsById(metric.id())) {
            throw new MetricNotFoundException(metric.id());
        }
        // Update the metric in the repository
        return metricRepository.save(metric); 
    }
}
