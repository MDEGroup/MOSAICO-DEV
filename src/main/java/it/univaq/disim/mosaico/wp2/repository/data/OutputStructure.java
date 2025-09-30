package it.univaq.disim.mosaico.wp2.repository.data;

/**
 * OutputStructure class for MOSAICO taxonomy.
 * Extends KeyValue for output structures.
 */
public record OutputStructure(
    String name,
    String value
) {
    // Factory method for creating from KeyValue
    public static OutputStructure from(KeyValue keyValue) {
        return new OutputStructure(keyValue.name(), keyValue.value());
    }
}