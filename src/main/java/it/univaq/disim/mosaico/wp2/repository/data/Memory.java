package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryType;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MemoryScope;
import java.util.UUID;

/**
 * JPA Entity for Memory.
 */
@Entity
@Table(name = "memories")
public class Memory {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private MemoryType type;

    private MemoryScope scope;

    private String db;

    public Memory() {
        // JPA
    }

    public Memory(String id, MemoryType type, MemoryScope scope, String db) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.type = type;
        this.scope = scope;
        this.db = db;
    }

    public String id() { return id; }
    public MemoryType type() { return type; }
    public MemoryScope scope() { return scope; }
    public String db() { return db; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public MemoryType getType() { return type; }
    public void setType(MemoryType type) { this.type = type; }
    public MemoryScope getScope() { return scope; }
    public void setScope(MemoryScope scope) { this.scope = scope; }
    public String getDb() { return db; }
    public void setDb(String db) { this.db = db; }
}