package it.univaq.disim.mosaico.wp2.repository.data.formula;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;

import java.util.Map;

/**
 * Checks if a specific metric meets a threshold.
 * Returns 1.0 if the threshold is met, 0.0 otherwise.
 *
 * Useful for binary KPIs like "Meets quality standard" or "Passes acceptance criteria".
 */
public class ThresholdFormula implements KPIFormula {
    private final Class<? extends MetricKey> metricKey;
    private final double threshold;
    private final boolean greaterThan;

    /**
     * Creates a threshold formula.
     *
     * @param metricKey The metric to check
     * @param threshold The threshold value
     * @param greaterThan If true, checks if metric > threshold. If false, checks if metric < threshold.
     */
    public ThresholdFormula(Class<? extends MetricKey> metricKey, double threshold, boolean greaterThan) {
        this.metricKey = metricKey;
        this.threshold = threshold;
        this.greaterThan = greaterThan;
    }

    @Override
    public double evaluate(Map<?, Double> metricValues) {
        if (!metricValues.containsKey(metricKey)) {
            throw new IllegalArgumentException("Missing required metric: " + metricKey.getSimpleName());
        }

        double value = metricValues.get(metricKey);

        if (greaterThan) {
            return value > threshold ? 1.0 : 0.0;
        } else {
            return value < threshold ? 1.0 : 0.0;
        }
    }
}
