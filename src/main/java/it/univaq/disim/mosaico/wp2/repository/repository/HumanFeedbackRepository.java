package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.HumanFeedback;
import it.univaq.disim.mosaico.wp2.repository.data.enums.FeedbackKind;
import java.util.List;

/**
 * Repository interface for HumanFeedback entities.
 */
@Repository
public interface HumanFeedbackRepository extends MongoRepository<HumanFeedback, String> {
    
    List<HumanFeedback> findByKind(FeedbackKind kind);
    List<HumanFeedback> findBySource(String source);
}