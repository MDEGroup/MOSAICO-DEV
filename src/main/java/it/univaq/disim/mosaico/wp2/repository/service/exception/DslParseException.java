package it.univaq.disim.mosaico.wp2.repository.service.exception;

import it.univaq.disim.mosaico.wp2.repository.dsl.DslValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when DSL formula parsing fails.
 */
public class DslParseException extends RuntimeException {

    private final List<DslValidationError> errors;

    public DslParseException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public DslParseException(String message, List<DslValidationError> errors) {
        super(message);
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }

    public DslParseException(String message, Throwable cause) {
        super(message, cause);
        this.errors = new ArrayList<>();
    }

    public List<DslValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
