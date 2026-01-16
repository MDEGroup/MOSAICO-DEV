package it.univaq.disim.mosaico.wp2.repository.service.exception;

/**
 * Exception thrown when metric computation fails.
 */
public class MetricComputationException extends RuntimeException {
    public MetricComputationException(String message) {
        super(message);
    }

    public MetricComputationException(String message, Throwable cause) {
        super(message, cause);
    }
}
