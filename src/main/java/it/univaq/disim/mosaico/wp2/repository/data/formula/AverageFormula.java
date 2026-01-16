package it.univaq.disim.mosaico.wp2.repository.data.formula;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;

import java.util.Map;

/**
 * Computes the average of all provided metric values.
 * Useful for overall performance assessment.
 */
public class AverageFormula implements KPIFormula {
    @Override
    public double evaluate(Map<Class<? extends MetricKey>, Double> metricValues) {
        if (metricValues == null || metricValues.isEmpty()) {
            throw new IllegalArgumentException("No metric values provided for average calculation");
        }

        return metricValues.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
}
