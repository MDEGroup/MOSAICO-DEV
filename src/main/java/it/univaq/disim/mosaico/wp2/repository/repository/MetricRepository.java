package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;

@Repository
public interface MetricRepository extends MongoRepository<Metric, String> {
    
    List<Metric> findByName(String name);
}