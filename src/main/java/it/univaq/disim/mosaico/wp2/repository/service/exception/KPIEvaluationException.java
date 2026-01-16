package it.univaq.disim.mosaico.wp2.repository.service.exception;

/**
 * Exception thrown when KPI evaluation fails.
 */
public class KPIEvaluationException extends RuntimeException {
    public KPIEvaluationException(String message) {
        super(message);
    }

    public KPIEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }
}
