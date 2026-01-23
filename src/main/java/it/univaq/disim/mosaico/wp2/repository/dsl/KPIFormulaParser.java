package it.univaq.disim.mosaico.wp2.repository.dsl;

import java.util.Set;

/**
 * Interface for parsing KPI formula DSL expressions.
 *
 * <p>This interface defines the contract that the partner's xText-based
 * DSL implementation must fulfill. The actual parsing logic will be
 * implemented using the xText grammar provided by the partner.</p>
 *
 * <p><b>User Story:</b> As a KPI factory service, I want to parse the stored DSL
 * (xText grammar) and build a KPIFormula instance on demand so BenchmarkServiceImpl
 * can keep using the existing evaluate(...) contract.</p>
 */
public interface KPIFormulaParser {

    /**
     * Parses a DSL formula expression and returns a parse result.
     *
     * <p>The DSL should support human-readable formulas like:</p>
     * <pre>
     * AVERAGE(ROUGE, BLEU)
     * WEIGHTED_SUM(ROUGE: 0.7, BLEU: 0.3)
     * IF ROUGE > 0.8 THEN 1.0 ELSE 0.0
     * (ROUGE + BLEU + F1_SCORE) / 3
     * MIN(ROUGE, BLEU) * 100
     * </pre>
     *
     * @param dslExpression the DSL formula expression to parse
     * @return DslParseResult containing either the compiled formula or validation errors
     */
    DslParseResult parse(String dslExpression);

    /**
     * Validates a DSL expression without fully compiling it.
     * Useful for quick validation in editors or before saving.
     *
     * @param dslExpression the DSL formula expression to validate
     * @return DslParseResult with validation status and any errors found
     */
    DslParseResult validate(String dslExpression);

    /**
     * Returns the set of known/valid metric keys that can be referenced in DSL formulas.
     * Used for validation to ensure formulas only reference computable metrics.
     *
     * @return set of valid metric key names (e.g., "ROUGE", "BLEU", "F1_SCORE")
     */
    Set<String> getKnownMetricKeys();

    /**
     * Registers additional metric keys that can be referenced in DSL formulas.
     * Allows dynamic extension of the metric vocabulary.
     *
     * @param metricKeys the metric keys to register
     */
    void registerMetricKeys(Set<String> metricKeys);
}
