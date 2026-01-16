package it.univaq.disim.mosaico.wp2.repository.data.formula;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import it.univaq.disim.mosaico.wp2.repository.data.BlueMetric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.data.RougeMetric;

/**
 * Tests for KPI formulas.
 */
class FormulaTest {

    @Test
    void testAverageFormula() {
        AverageFormula formula = new AverageFormula();

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.8);
        values.put(RougeMetric.class, 0.6);

        double result = formula.evaluate(values);
        assertEquals(0.7, result, 0.001);
    }

    @Test
    void testAverageFormulaWithEmptyValues() {
        AverageFormula formula = new AverageFormula();
        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> {
            formula.evaluate(values);
        });
    }

    @Test
    void testWeightedSumFormula() {
        Map<Class<? extends MetricKey>, Double> weights = new HashMap<>();
        weights.put(BlueMetric.class, 0.6);
        weights.put(RougeMetric.class, 0.4);

        WeightedSumFormula formula = new WeightedSumFormula(weights);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.8);
        values.put(RougeMetric.class, 0.9);

        // Expected: 0.8 * 0.6 + 0.9 * 0.4 = 0.48 + 0.36 = 0.84
        double result = formula.evaluate(values);
        assertEquals(0.84, result, 0.001);
    }

    @Test
    void testWeightedSumFormulaWithMissingMetric() {
        Map<Class<? extends MetricKey>, Double> weights = new HashMap<>();
        weights.put(BlueMetric.class, 0.6);
        weights.put(RougeMetric.class, 0.4);

        WeightedSumFormula formula = new WeightedSumFormula(weights);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.8);
        // Missing RougeMetric

        assertThrows(IllegalArgumentException.class, () -> {
            formula.evaluate(values);
        });
    }

    @Test
    void testThresholdFormulaGreaterThan() {
        ThresholdFormula formula = new ThresholdFormula(BlueMetric.class, 0.7, true);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.8);

        double result = formula.evaluate(values);
        assertEquals(1.0, result);
    }

    @Test
    void testThresholdFormulaGreaterThanFails() {
        ThresholdFormula formula = new ThresholdFormula(BlueMetric.class, 0.7, true);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.6);

        double result = formula.evaluate(values);
        assertEquals(0.0, result);
    }

    @Test
    void testThresholdFormulaLessThan() {
        ThresholdFormula formula = new ThresholdFormula(BlueMetric.class, 0.5, false);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        values.put(BlueMetric.class, 0.3);

        double result = formula.evaluate(values);
        assertEquals(1.0, result);
    }

    @Test
    void testThresholdFormulaWithMissingMetric() {
        ThresholdFormula formula = new ThresholdFormula(BlueMetric.class, 0.7, true);

        Map<Class<? extends MetricKey>, Double> values = new HashMap<>();
        // Missing BlueMetric

        assertThrows(IllegalArgumentException.class, () -> {
            formula.evaluate(values);
        });
    }
}
