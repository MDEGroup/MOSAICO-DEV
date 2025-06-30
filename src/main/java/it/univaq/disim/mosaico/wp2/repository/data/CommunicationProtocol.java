package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "communicationProtocols")
public record CommunicationProtocol(
    @Id String id,
    String name,
    String description,
    String version,
    String format,                   // es. "JSON", "MessagePack", "Protobuf"
    String transportLayer,           // es. "HTTP", "WebSocket", "gRPC"
    Map<String, Object> messageSchema, // schema dei messaggi
    boolean encryption,              // se la comunicazione è crittografata
    int messageTimeout,               // timeout dei messaggi in millisecondi
    boolean distributed // se il protocollo è distribuito (es. per microservizi o architetture a eventi)
) {}