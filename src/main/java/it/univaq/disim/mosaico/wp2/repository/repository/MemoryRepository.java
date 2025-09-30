package it.univaq.disim.mosaico.wp2.repository.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import it.univaq.disim.mosaico.wp2.repository.data.Memory;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;
import java.util.List;

/**
 * Repository interface for Memory entities.
 */
@Repository
public interface MemoryRepository extends MongoRepository<Memory, String> {
    
    List<Memory> findByType(MemoryType type);
    List<Memory> findByScope(MemoryScope scope);
    List<Memory> findByTypeAndScope(MemoryType type, MemoryScope scope);
    List<Memory> findByDb(String db);
}