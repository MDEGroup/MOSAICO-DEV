package it.univaq.disim.mosaico.wp2.repository.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Generic JSON AttributeConverter using Jackson.
 * Stores the Java value as a JSON string (we use jsonb column in Postgres via columnDefinition).
 */
@Converter(autoApply = false)
public class JsonAttributeConverter implements AttributeConverter<Object, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert attribute to JSON", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            // deserialize generically to Object (Map / List / primitive wrappers)
            return MAPPER.readValue(dbData, Object.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to read JSON from database", e);
        }
    }
}
