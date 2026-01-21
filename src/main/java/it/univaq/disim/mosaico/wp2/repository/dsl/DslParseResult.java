package it.univaq.disim.mosaico.wp2.repository.dsl;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Result of parsing a DSL formula expression.
 * Contains either a successfully parsed KPIFormula or a list of validation errors.
 *
 * <p>This class encapsulates the result of the partner's xText grammar parsing,
 * allowing BenchmarkServiceImpl to continue using the existing evaluate(...) contract.</p>
 */
public class DslParseResult {

    private final boolean success;
    private final KPIFormula formula;
    private final List<DslValidationError> errors;
    private final Set<String> referencedMetrics;
    private final String originalDsl;

    private DslParseResult(boolean success, KPIFormula formula, List<DslValidationError> errors,
                           Set<String> referencedMetrics, String originalDsl) {
        this.success = success;
        this.formula = formula;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        this.referencedMetrics = referencedMetrics != null ? Set.copyOf(referencedMetrics) : Set.of();
        this.originalDsl = originalDsl;
    }

    /**
     * Creates a successful parse result with the compiled formula.
     */
    public static DslParseResult success(KPIFormula formula, Set<String> referencedMetrics, String originalDsl) {
        return new DslParseResult(true, formula, null, referencedMetrics, originalDsl);
    }

    /**
     * Creates a failed parse result with validation errors.
     */
    public static DslParseResult failure(List<DslValidationError> errors, String originalDsl) {
        return new DslParseResult(false, null, errors, null, originalDsl);
    }

    /**
     * Creates a failed parse result with a single error.
     */
    public static DslParseResult failure(DslValidationError error, String originalDsl) {
        return new DslParseResult(false, null, List.of(error), null, originalDsl);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public KPIFormula getFormula() {
        if (!success) {
            throw new IllegalStateException("Cannot get formula from failed parse result. Check errors first.");
        }
        return formula;
    }

    public List<DslValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    /**
     * Returns the set of metric keys referenced in the DSL formula.
     * Used for validation to ensure all referenced metrics are known/computable.
     */
    public Set<String> getReferencedMetrics() {
        return referencedMetrics;
    }

    public String getOriginalDsl() {
        return originalDsl;
    }

    /**
     * Returns a formatted string of all errors for display to users.
     */
    public String getErrorsAsString() {
        if (errors.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("DSL Validation Errors (").append(errors.size()).append("):\n");
        for (DslValidationError error : errors) {
            sb.append("  - ").append(error.getFormattedMessage()).append("\n");
        }
        return sb.toString();
    }
}
