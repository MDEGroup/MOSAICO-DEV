package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import java.util.List;

/**
 * Repository interface for Benchmark entities.
 */
@Repository
public interface BenchmarkRepository extends JpaRepository<Benchmark, String> {

    Benchmark findByDatasetRef(String datasetRef);
    Benchmark findByProtocolVersion(String protocolVersion);
    List<Benchmark> findByEvaluates_Id(String agentId);

    List<Benchmark> findByAssess_Id(String skillId);

    @Query("SELECT b FROM Benchmark b JOIN b.assess s WHERE s.name = :skillName")
    List<Benchmark> findBySkillName(@Param("skillName") String skillName);

}