// filepath: /Users/juridirocco/development/MOSAICO/repository/src/main/java/it/univaq/disim/mosaico/wp2/repository/data/Task.java
package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;
import java.util.Map;

@Document(collection = "tasks")
public record Task(
    @Id String id,
    String name,
    String description,
    @DocumentReference List<Tool> requiredTools,
    Map<String, Object> parameters,
    String precondition,
    String postcondition,
    int estimatedDuration
) {}