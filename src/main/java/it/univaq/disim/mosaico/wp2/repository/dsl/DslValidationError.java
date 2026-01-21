package it.univaq.disim.mosaico.wp2.repository.dsl;

/**
 * Represents a validation error found during DSL parsing.
 * Provides actionable error information including line/column location
 * to help users fix their formulas quickly.
 *
 * <p>This class will be populated by the partner's xText grammar implementation.</p>
 */
public class DslValidationError {

    private final int line;
    private final int column;
    private final String message;
    private final String errorCode;
    private final ErrorSeverity severity;

    public enum ErrorSeverity {
        ERROR,
        WARNING,
        INFO
    }

    public DslValidationError(int line, int column, String message) {
        this(line, column, message, null, ErrorSeverity.ERROR);
    }

    public DslValidationError(int line, int column, String message, String errorCode, ErrorSeverity severity) {
        this.line = line;
        this.column = column;
        this.message = message;
        this.errorCode = errorCode;
        this.severity = severity;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    /**
     * Returns a formatted error message with location information.
     * Example: "Line 3, Column 15: Unknown metric 'INVALID_METRIC'"
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Line ").append(line).append(", Column ").append(column).append(": ");
        if (errorCode != null) {
            sb.append("[").append(errorCode).append("] ");
        }
        sb.append(message);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
