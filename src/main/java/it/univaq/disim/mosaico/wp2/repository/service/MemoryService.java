package it.univaq.disim.mosaico.wp2.repository.service;

import java.util.List;
import java.util.Optional;
import it.univaq.disim.mosaico.wp2.repository.data.Memory;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;

/**
 * Service interface for Memory operations.
 */
public interface MemoryService {
    
    List<Memory> findAll();
    Optional<Memory> findById(String id);
    Memory save(Memory memory);
    void deleteById(String id);
    
    List<Memory> findByType(MemoryType type);
    List<Memory> findByScope(MemoryScope scope);
    List<Memory> findByTypeAndScope(MemoryType type, MemoryScope scope);
}