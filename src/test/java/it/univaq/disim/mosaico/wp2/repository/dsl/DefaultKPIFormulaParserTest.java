package it.univaq.disim.mosaico.wp2.repository.dsl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DefaultKPIFormulaParser.
 *
 * Test Plan:
 * 1. Parse valid AVERAGE formulas
 * 2. Parse valid WEIGHTED_SUM formulas
 * 3. Parse valid MIN/MAX formulas
 * 4. Parse valid THRESHOLD formulas
 * 5. Handle invalid/empty expressions
 * 6. Handle unknown metrics
 * 7. Validate formula evaluation results
 * 8. Test metric key registration
 */
@DisplayName("DefaultKPIFormulaParser Tests")
class DefaultKPIFormulaParserTest {

    private DefaultKPIFormulaParser parser;

    @BeforeEach
    void setUp() {
        parser = new DefaultKPIFormulaParser();
    }

    @Nested
    @DisplayName("AVERAGE Formula Tests")
    class AverageFormulaTests {

        @Test
        @DisplayName("Should parse valid AVERAGE formula with two metrics")
        void shouldParseAverageWithTwoMetrics() {
            DslParseResult result = parser.parse("AVERAGE(ROUGE, BLEU)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
            assertEquals(Set.of("ROUGE", "BLEU"), result.getReferencedMetrics());
        }

        @Test
        @DisplayName("Should parse AVERAGE formula case insensitively")
        void shouldParseAverageCaseInsensitive() {
            DslParseResult result = parser.parse("average(rouge, bleu)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
        }

        @Test
        @DisplayName("Should correctly evaluate AVERAGE formula")
        void shouldEvaluateAverageFormula() {
            DslParseResult result = parser.parse("AVERAGE(ROUGE, BLEU)");
            assertTrue(result.isSuccess());

            // Use String keys for direct metric name matching
            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);
            values.put("BLEU", 0.6);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(0.7, kpiValue, 0.001);
        }

        @Test
        @DisplayName("Should handle AVERAGE with single available metric")
        void shouldHandleAverageWithPartialMetrics() {
            DslParseResult result = parser.parse("AVERAGE(ROUGE, BLEU)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(0.8, kpiValue, 0.001); // Only one metric available
        }
    }

    @Nested
    @DisplayName("WEIGHTED_SUM Formula Tests")
    class WeightedSumFormulaTests {

        @Test
        @DisplayName("Should parse valid WEIGHTED_SUM formula")
        void shouldParseWeightedSum() {
            DslParseResult result = parser.parse("WEIGHTED_SUM(ROUGE: 0.7, BLEU: 0.3)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
            assertEquals(Set.of("ROUGE", "BLEU"), result.getReferencedMetrics());
        }

        @Test
        @DisplayName("Should correctly evaluate WEIGHTED_SUM formula")
        void shouldEvaluateWeightedSum() {
            DslParseResult result = parser.parse("WEIGHTED_SUM(ROUGE: 0.6, BLEU: 0.4)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);
            values.put("BLEU", 0.5);

            double kpiValue = result.getFormula().evaluate(values);
            // 0.8 * 0.6 + 0.5 * 0.4 = 0.48 + 0.2 = 0.68
            assertEquals(0.68, kpiValue, 0.001);
        }
    }

    @Nested
    @DisplayName("MIN/MAX Formula Tests")
    class MinMaxFormulaTests {

        @Test
        @DisplayName("Should parse valid MIN formula")
        void shouldParseMin() {
            DslParseResult result = parser.parse("MIN(ROUGE, BLEU)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
        }

        @Test
        @DisplayName("Should parse valid MAX formula")
        void shouldParseMax() {
            DslParseResult result = parser.parse("MAX(ROUGE, BLEU)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
        }

        @Test
        @DisplayName("Should correctly evaluate MIN formula")
        void shouldEvaluateMin() {
            DslParseResult result = parser.parse("MIN(ROUGE, BLEU)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);
            values.put("BLEU", 0.5);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(0.5, kpiValue, 0.001);
        }

        @Test
        @DisplayName("Should correctly evaluate MAX formula")
        void shouldEvaluateMax() {
            DslParseResult result = parser.parse("MAX(ROUGE, BLEU)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);
            values.put("BLEU", 0.5);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(0.8, kpiValue, 0.001);
        }
    }

    @Nested
    @DisplayName("THRESHOLD Formula Tests")
    class ThresholdFormulaTests {

        @Test
        @DisplayName("Should parse valid THRESHOLD formula")
        void shouldParseThreshold() {
            DslParseResult result = parser.parse("THRESHOLD(ROUGE, 0.7)");

            assertTrue(result.isSuccess());
            assertNotNull(result.getFormula());
            assertEquals(Set.of("ROUGE"), result.getReferencedMetrics());
        }

        @Test
        @DisplayName("Should return 1.0 when metric meets threshold")
        void shouldReturnOneWhenMeetsThreshold() {
            DslParseResult result = parser.parse("THRESHOLD(ROUGE, 0.7)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.8);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(1.0, kpiValue, 0.001);
        }

        @Test
        @DisplayName("Should return 0.0 when metric below threshold")
        void shouldReturnZeroWhenBelowThreshold() {
            DslParseResult result = parser.parse("THRESHOLD(ROUGE, 0.7)");
            assertTrue(result.isSuccess());

            Map<String, Double> values = new HashMap<>();
            values.put("ROUGE", 0.5);

            double kpiValue = result.getFormula().evaluate(values);
            assertEquals(0.0, kpiValue, 0.001);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should fail on null expression")
        void shouldFailOnNullExpression() {
            DslParseResult result = parser.parse(null);

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
            assertFalse(result.getErrors().isEmpty());
        }

        @Test
        @DisplayName("Should fail on empty expression")
        void shouldFailOnEmptyExpression() {
            DslParseResult result = parser.parse("");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
        }

        @Test
        @DisplayName("Should fail on blank expression")
        void shouldFailOnBlankExpression() {
            DslParseResult result = parser.parse("   ");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
        }

        @Test
        @DisplayName("Should fail on unrecognized pattern")
        void shouldFailOnUnrecognizedPattern() {
            DslParseResult result = parser.parse("UNKNOWN_FORMULA(ROUGE)");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
        }

        @Test
        @DisplayName("Should fail on unknown metric")
        void shouldFailOnUnknownMetric() {
            DslParseResult result = parser.parse("AVERAGE(ROUGE, UNKNOWN_METRIC)");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());

            DslValidationError error = result.getErrors().get(0);
            assertEquals("UNKNOWN_METRIC", error.getErrorCode());
        }

        @Test
        @DisplayName("Should provide formatted error message with line and column")
        void shouldProvideFormattedErrorMessage() {
            DslParseResult result = parser.parse("AVERAGE(INVALID_METRIC)");

            assertFalse(result.isSuccess());
            DslValidationError error = result.getErrors().get(0);

            String formatted = error.getFormattedMessage();
            assertTrue(formatted.contains("Line"));
            assertTrue(formatted.contains("Column"));
        }

        @Test
        @DisplayName("Should throw when getting formula from failed result")
        void shouldThrowWhenGettingFormulaFromFailedResult() {
            DslParseResult result = parser.parse("");

            assertThrows(IllegalStateException.class, result::getFormula);
        }
    }

    @Nested
    @DisplayName("Metric Key Registration Tests")
    class MetricKeyRegistrationTests {

        @Test
        @DisplayName("Should have default metric keys registered")
        void shouldHaveDefaultMetricKeys() {
            Set<String> keys = parser.getKnownMetricKeys();

            assertTrue(keys.contains("ROUGE"));
            assertTrue(keys.contains("BLEU"));
            assertTrue(keys.contains("ACCURACY"));
            assertTrue(keys.contains("PRECISION"));
            assertTrue(keys.contains("RECALL"));
            assertTrue(keys.contains("F1_SCORE"));
        }

        @Test
        @DisplayName("Should allow registering custom metric keys")
        void shouldAllowRegisteringCustomKeys() {
            parser.registerMetricKeys(Set.of("CUSTOM_METRIC"));

            assertTrue(parser.getKnownMetricKeys().contains("CUSTOM_METRIC"));
        }

        @Test
        @DisplayName("Should successfully parse formula with custom metric after registration")
        void shouldParseWithCustomMetricAfterRegistration() {
            parser.registerMetricKeys(Set.of("CUSTOM_METRIC"));

            DslParseResult result = parser.parse("AVERAGE(ROUGE, CUSTOM_METRIC)");

            assertTrue(result.isSuccess());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Validate should behave like parse")
        void validateShouldBehaveLikeParse() {
            DslParseResult parseResult = parser.parse("AVERAGE(ROUGE, BLEU)");
            DslParseResult validateResult = parser.validate("AVERAGE(ROUGE, BLEU)");

            assertEquals(parseResult.isSuccess(), validateResult.isSuccess());
        }

        @Test
        @DisplayName("Validate should report errors for invalid formulas")
        void validateShouldReportErrors() {
            DslParseResult result = parser.validate("INVALID");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
        }
    }
}
