package it.univaq.disim.mosaico.wp2.repository.data;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Model(String name, String description, String version, String author, String license) {
    // This is a record class in Java that represents a model with five fields: name, description, version, author, and license.
    // The record automatically generates the constructor, getters, equals(), hashCode(), and toString() methods for these fields.
    // The fields are defined as final, meaning they cannot be changed after the object is created.
    // This is a simple and concise way to create data classes in Java.

}
