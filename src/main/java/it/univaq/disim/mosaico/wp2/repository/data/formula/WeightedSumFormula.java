package it.univaq.disim.mosaico.wp2.repository.data.formula;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;

import java.util.Map;

/**
 * Computes a weighted sum of metric values.
 * Each metric can be assigned a weight to reflect its importance.
 *
 * Example: Quality = 0.6 * Accuracy + 0.4 * Completeness
 */
public class WeightedSumFormula implements KPIFormula {
    private final Map<Class<? extends MetricKey>, Double> weights;

    /**
     * Creates a weighted sum formula.
     *
     * @param weights Map of MetricKey classes to their weights
     */
    public WeightedSumFormula(Map<Class<? extends MetricKey>, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            throw new IllegalArgumentException("Weights cannot be null or empty");
        }
        this.weights = weights;
    }

    @Override
    public double evaluate(Map<Class<? extends MetricKey>, Double> metricValues) {
        if (metricValues == null || metricValues.isEmpty()) {
            throw new IllegalArgumentException("No metric values provided");
        }

        double sum = 0.0;
        for (Map.Entry<Class<? extends MetricKey>, Double> entry : weights.entrySet()) {
            Class<? extends MetricKey> key = entry.getKey();
            Double weight = entry.getValue();

            if (!metricValues.containsKey(key)) {
                throw new IllegalArgumentException("Missing required metric: " + key.getSimpleName());
            }

            sum += metricValues.get(key) * weight;
        }

        return sum;
    }
}
