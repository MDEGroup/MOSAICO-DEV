package it.univaq.disim.mosaico.wp2.repository.dsl;

import it.univaq.disim.mosaico.wp2.repository.data.KPISpecification;
import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslParseException;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslValidationException;

import java.util.Set;

/**
 * Service for managing KPI formula DSL operations.
 *
 * <p><b>User Story:</b> As a benchmark designer, I want to define KPI aggregation formulas
 * in a human-readable DSL so that I can change how metrics are combined without touching Java code.</p>
 *
 * <p><b>User Story:</b> As a repository maintainer, I need KPISpecification to store the raw DSL
 * text and metadata so the system can reload and evaluate formulas after restarts.</p>
 *
 * <p>This service acts as the main entry point for DSL-related operations, coordinating
 * between the parser, validation, and the KPISpecification storage.</p>
 */
public interface KPIFormulaDslService {

    /**
     * Parses a DSL expression and returns a ready-to-use KPIFormula.
     *
     * @param dslExpression the DSL formula expression
     * @return compiled KPIFormula that can be evaluated
     * @throws DslParseException if parsing fails
     * @throws DslValidationException if validation fails
     */
    KPIFormula parseFormula(String dslExpression) throws DslParseException, DslValidationException;

    /**
     * Builds a KPIFormula from a KPISpecification.
     * The specification contains the stored DSL text and metadata.
     *
     * <p><b>User Story:</b> As a KPI factory service, I want to parse the stored DSL
     * (xText grammar) and build a KPIFormula instance on demand so BenchmarkServiceImpl
     * can keep using the existing evaluate(...) contract.</p>
     *
     * @param specification the KPI specification with DSL text
     * @return compiled KPIFormula
     * @throws DslParseException if parsing fails
     * @throws DslValidationException if validation fails
     */
    KPIFormula buildFromSpecification(KPISpecification specification)
        throws DslParseException, DslValidationException;

    /**
     * Validates a DSL expression without fully compiling it.
     *
     * <p><b>User Story:</b> As a QA engineer, I need automated validation that DSL formulas
     * reference only known metrics and produce deterministic results to prevent runtime
     * errors during benchmark runs.</p>
     *
     * @param dslExpression the DSL formula expression to validate
     * @return validation result with any errors found
     */
    DslParseResult validateFormula(String dslExpression);

    /**
     * Validates a DSL expression against a specific set of available metrics.
     * Ensures the formula only references metrics that will be available at runtime.
     *
     * @param dslExpression the DSL formula expression to validate
     * @param availableMetrics the set of metric keys that will be available
     * @return validation result with any errors found
     */
    DslParseResult validateFormulaAgainstMetrics(String dslExpression, Set<String> availableMetrics);

    /**
     * Creates a KPISpecification from a DSL expression.
     * Validates the DSL and stores it along with metadata.
     *
     * @param dslExpression the DSL formula expression
     * @param formulaType optional type hint (e.g., "AVERAGE", "WEIGHTED_SUM", "CUSTOM")
     * @return KPISpecification ready to be persisted
     * @throws DslValidationException if validation fails
     */
    KPISpecification createSpecification(String dslExpression, String formulaType)
        throws DslValidationException;

    /**
     * Returns the set of all known/valid metric keys that can be used in DSL formulas.
     *
     * @return set of valid metric key names
     */
    Set<String> getKnownMetricKeys();

    /**
     * Registers additional custom metric keys that can be referenced in DSL formulas.
     *
     * @param metricKeys the metric keys to register
     */
    void registerCustomMetricKeys(Set<String> metricKeys);

    /**
     * Gets a human-readable description of the DSL syntax.
     * Useful for documentation and error messages.
     *
     * @return DSL syntax documentation
     */
    String getDslSyntaxHelp();
}
