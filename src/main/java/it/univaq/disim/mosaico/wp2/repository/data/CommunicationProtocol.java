package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.Map;
import java.util.UUID;

/**
 * Communication protocol configuration for multi-agent systems.
 * Supports formats like JSON, MessagePack, Protobuf over HTTP, WebSocket, gRPC.
 * Configurable for encryption, timeouts, and distributed architectures.
 */
@Entity
@Table(name = "communication_protocols")
public class CommunicationProtocol {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    private String name;
    private String description;

    private String version;
    private String format;
    private String transportLayer;

    @Transient
    private Map<String, Object> messageSchema;

    private boolean encryption;
    private int messageTimeout;
    private boolean distributed;

    public CommunicationProtocol() {}

    public CommunicationProtocol(String id, String name, String description, String version, String format, String transportLayer, Map<String, Object> messageSchema, boolean encryption, int messageTimeout, boolean distributed) {
        this.id = (id == null) ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.format = format;
        this.transportLayer = transportLayer;
        this.messageSchema = messageSchema;
        this.encryption = encryption;
        this.messageTimeout = messageTimeout;
        this.distributed = distributed;
    }

    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String version() { return version; }
    public String format() { return format; }
    public String transportLayer() { return transportLayer; }
    public Map<String, Object> messageSchema() { return messageSchema; }
    public boolean encryption() { return encryption; }
    public int messageTimeout() { return messageTimeout; }
    public boolean distributed() { return distributed; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}