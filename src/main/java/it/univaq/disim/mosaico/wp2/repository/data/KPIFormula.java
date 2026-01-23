package it.univaq.disim.mosaico.wp2.repository.data;

import java.util.Map;

/**
 * Functional interface for KPI formula evaluation.
 * A KPIFormula takes a map of metric values (keyed by MetricKey class)
 * and computes a single KPI value.
 *
 * Example formulas:
 * - Average: (metric1 + metric2) / 2
 * - Weighted sum: 0.6 * metric1 + 0.4 * metric2
 * - Threshold check: metric1 > 0.8 ? 1.0 : 0.0
 */
@FunctionalInterface
public interface KPIFormula {
    /**
     * Evaluates the KPI formula given metric values.
     *
     * <p>The map keys can be either:</p>
     * <ul>
     *   <li>{@code Class<? extends MetricKey>} - for type-safe metric references</li>
     *   <li>{@code String} - for string-based metric name lookups (used by DSL parser)</li>
     * </ul>
     *
     * @param metricValues Map of metric identifiers to their computed values
     * @return The computed KPI value
     * @throws IllegalArgumentException if required metrics are missing
     */
    double evaluate(Map<?, Double> metricValues);
}
