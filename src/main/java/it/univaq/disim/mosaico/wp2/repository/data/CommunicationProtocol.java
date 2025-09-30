package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

/**
 * Communication protocol configuration for multi-agent systems.
 * Supports formats like JSON, MessagePack, Protobuf over HTTP, WebSocket, gRPC.
 * Configurable for encryption, timeouts, and distributed architectures.
 */
@Document(collection = "communicationProtocols")
public record CommunicationProtocol(@Id String id, String name, String description, 
    String version, String format, String transportLayer, Map<String, Object> messageSchema, 
    boolean encryption, int messageTimeout, boolean distributed) {}