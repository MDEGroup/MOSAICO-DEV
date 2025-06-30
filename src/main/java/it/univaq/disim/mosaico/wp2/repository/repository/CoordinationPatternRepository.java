package it.univaq.disim.mosaico.wp2.repository.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.univaq.disim.mosaico.wp2.repository.data.CoordinationPattern;

@Repository
public interface CoordinationPatternRepository extends MongoRepository<CoordinationPattern, String> {
    
    List<CoordinationPattern> findByName(String name);
}