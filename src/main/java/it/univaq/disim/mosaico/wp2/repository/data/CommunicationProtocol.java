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
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    private String description;
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    private String format;
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    private String transportLayer;
    public String getTransportLayer() {
        return transportLayer;
    }
    public void setTransportLayer(String transportLayer) {
        this.transportLayer = transportLayer;
    }

    @Transient
    private Map<String, Object> messageSchema;

    private boolean encryption;
    public boolean isEncryption() {
        return encryption;
    }
    public void setEncryption(boolean encryption) {
        this.encryption = encryption;
    }
    private int messageTimeout;
    public int getMessageTimeout() {
        return messageTimeout;
    }
    public void setMessageTimeout(int messageTimeout) {
        this.messageTimeout = messageTimeout;
    }
    private boolean distributed;
    public boolean isDistributed() {
        return distributed;
    }
    public void setDistributed(boolean distributed) {
        this.distributed = distributed;
    }

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



    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}