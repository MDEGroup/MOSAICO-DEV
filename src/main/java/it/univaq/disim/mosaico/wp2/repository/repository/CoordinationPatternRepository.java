package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;

@Repository
public interface CoordinationPatternRepository extends JpaRepository<CoordinationPattern, String> {
    
    List<CoordinationPattern> findByName(String name);
}