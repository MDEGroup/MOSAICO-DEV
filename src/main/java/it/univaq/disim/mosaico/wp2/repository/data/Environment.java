package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;
import java.util.Map;

@Document(collection = "environments")
public record Environment(
    @Id String id,
    String name,
    String description,
    Map<String, Object> properties,
    @DocumentReference List<Agent> agents,
    @DocumentReference List<Rule> environmentRules
) {}