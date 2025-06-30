// filepath: /Users/juridirocco/development/MOSAICO/repository/src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Tool.java
package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "tools")
public record Tool(
    @Id String id,
    String name,
    String description,
    String apiEndpoint,
    Map<String, Object> parameters
) {}