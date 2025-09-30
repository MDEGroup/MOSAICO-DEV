package it.univaq.disim.mosaico.wp2.repository.data;

/**
 * InputParameter class for MOSAICO taxonomy.
 * Extends KeyValue for input parameters.
 */
public record InputParameter(
    String name,
    String value
) {
    // Factory method for creating from KeyValue
    public static InputParameter from(KeyValue keyValue) {
        return new InputParameter(keyValue.name(), keyValue.value());
    }
}