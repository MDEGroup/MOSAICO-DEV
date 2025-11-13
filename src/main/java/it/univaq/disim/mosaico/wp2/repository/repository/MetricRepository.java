package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;

@Repository
public interface MetricRepository extends JpaRepository<Metric, String> {
    
    List<Metric> findByName(String name);
}