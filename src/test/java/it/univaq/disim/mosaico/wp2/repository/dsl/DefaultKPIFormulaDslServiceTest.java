package it.univaq.disim.mosaico.wp2.repository.dsl;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.data.KPISpecification;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslParseException;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DefaultKPIFormulaDslService.
 *
 * Test Plan:
 * 1. Parse valid formulas successfully
 * 2. Handle parse errors gracefully
 * 3. Build formula from KPISpecification
 * 4. Validate formulas against available metrics
 * 5. Create KPISpecification from DSL
 * 6. Test DSL syntax help generation
 */
@DisplayName("DefaultKPIFormulaDslService Tests")
class DefaultKPIFormulaDslServiceTest {

    private DefaultKPIFormulaDslService service;
    private KPIFormulaParser parser;

    @BeforeEach
    void setUp() {
        parser = new DefaultKPIFormulaParser();
        service = new DefaultKPIFormulaDslService(parser);
    }

    @Nested
    @DisplayName("parseFormula Tests")
    class ParseFormulaTests {

        @Test
        @DisplayName("Should parse valid DSL expression")
        void shouldParseValidExpression() {
            KPIFormula formula = service.parseFormula("AVERAGE(ROUGE, BLEU)");

            assertNotNull(formula);
        }

        @Test
        @DisplayName("Should throw DslParseException for invalid expression")
        void shouldThrowForInvalidExpression() {
            assertThrows(DslParseException.class, () -> {
                service.parseFormula("INVALID_FORMULA");
            });
        }

        @Test
        @DisplayName("Should throw DslParseException with error details")
        void shouldThrowWithErrorDetails() {
            DslParseException exception = assertThrows(DslParseException.class, () -> {
                service.parseFormula("AVERAGE(UNKNOWN_METRIC)");
            });

            assertFalse(exception.getErrors().isEmpty());
        }
    }

    @Nested
    @DisplayName("buildFromSpecification Tests")
    class BuildFromSpecificationTests {

        @Test
        @DisplayName("Should build formula from specification with DSL text")
        void shouldBuildFromSpecificationWithDslText() {
            KPISpecification spec = new KPISpecification();
            spec.setDslText("AVERAGE(ROUGE, BLEU)");
            spec.setFormulaType("AVERAGE");

            KPIFormula formula = service.buildFromSpecification(spec);

            assertNotNull(formula);
        }

        @Test
        @DisplayName("Should throw for null specification")
        void shouldThrowForNullSpecification() {
            assertThrows(DslValidationException.class, () -> {
                service.buildFromSpecification(null);
            });
        }

        @Test
        @DisplayName("Should fall back to legacy formula when DSL text is empty")
        void shouldFallbackToLegacyFormula() {
            KPISpecification spec = new KPISpecification();
            spec.setFormulaType("AVERAGE");
            // Set a legacy formula
            spec.setFormula(values -> 0.5);

            KPIFormula formula = service.buildFromSpecification(spec);

            assertNotNull(formula);
            assertEquals(0.5, formula.evaluate(null), 0.001);
        }

        @Test
        @DisplayName("Should throw when no DSL text and no legacy formula")
        void shouldThrowWhenNoDslAndNoLegacy() {
            KPISpecification spec = new KPISpecification();
            // No DSL text and no formula type

            assertThrows(DslValidationException.class, () -> {
                service.buildFromSpecification(spec);
            });
        }
    }

    @Nested
    @DisplayName("validateFormula Tests")
    class ValidateFormulaTests {

        @Test
        @DisplayName("Should return success for valid formula")
        void shouldReturnSuccessForValidFormula() {
            DslParseResult result = service.validateFormula("AVERAGE(ROUGE, BLEU)");

            assertTrue(result.isSuccess());
        }

        @Test
        @DisplayName("Should return errors for invalid formula")
        void shouldReturnErrorsForInvalidFormula() {
            DslParseResult result = service.validateFormula("INVALID");

            assertFalse(result.isSuccess());
            assertTrue(result.hasErrors());
        }
    }

    @Nested
    @DisplayName("validateFormulaAgainstMetrics Tests")
    class ValidateAgainstMetricsTests {

        @Test
        @DisplayName("Should succeed when all metrics are available")
        void shouldSucceedWhenAllMetricsAvailable() {
            Set<String> availableMetrics = Set.of("ROUGE", "BLEU");

            DslParseResult result = service.validateFormulaAgainstMetrics(
                "AVERAGE(ROUGE, BLEU)", availableMetrics);

            assertTrue(result.isSuccess());
        }

        @Test
        @DisplayName("Should fail when metric is not available")
        void shouldFailWhenMetricNotAvailable() {
            Set<String> availableMetrics = Set.of("ROUGE"); // BLEU not available

            DslParseResult result = service.validateFormulaAgainstMetrics(
                "AVERAGE(ROUGE, BLEU)", availableMetrics);

            assertFalse(result.isSuccess());
            assertTrue(result.getErrorsAsString().contains("BLEU"));
        }
    }

    @Nested
    @DisplayName("createSpecification Tests")
    class CreateSpecificationTests {

        @Test
        @DisplayName("Should create specification with valid DSL")
        void shouldCreateSpecificationWithValidDsl() {
            KPISpecification spec = service.createSpecification(
                "AVERAGE(ROUGE, BLEU)", "AVERAGE");

            assertNotNull(spec);
            assertEquals("AVERAGE(ROUGE, BLEU)", spec.getDslText());
            assertEquals("AVERAGE", spec.getFormulaType());
            assertEquals("1.0", spec.getDslVersion());
        }

        @Test
        @DisplayName("Should auto-detect formula type when not provided")
        void shouldAutoDetectFormulaType() {
            KPISpecification spec = service.createSpecification(
                "WEIGHTED_SUM(ROUGE: 0.5, BLEU: 0.5)", null);

            assertEquals("WEIGHTED_SUM", spec.getFormulaType());
        }

        @Test
        @DisplayName("Should throw DslValidationException for invalid DSL")
        void shouldThrowForInvalidDsl() {
            assertThrows(DslValidationException.class, () -> {
                service.createSpecification("INVALID_FORMULA", null);
            });
        }
    }

    @Nested
    @DisplayName("Metric Key Management Tests")
    class MetricKeyManagementTests {

        @Test
        @DisplayName("Should return known metric keys")
        void shouldReturnKnownMetricKeys() {
            Set<String> keys = service.getKnownMetricKeys();

            assertFalse(keys.isEmpty());
            assertTrue(keys.contains("ROUGE"));
            assertTrue(keys.contains("BLEU"));
        }

        @Test
        @DisplayName("Should allow registering custom metric keys")
        void shouldAllowRegisteringCustomKeys() {
            service.registerCustomMetricKeys(Set.of("MY_METRIC"));

            assertTrue(service.getKnownMetricKeys().contains("MY_METRIC"));
        }
    }

    @Nested
    @DisplayName("DSL Syntax Help Tests")
    class DslSyntaxHelpTests {

        @Test
        @DisplayName("Should return non-empty syntax help")
        void shouldReturnNonEmptySyntaxHelp() {
            String help = service.getDslSyntaxHelp();

            assertNotNull(help);
            assertFalse(help.isBlank());
        }

        @Test
        @DisplayName("Should mention supported patterns in help")
        void shouldMentionSupportedPatterns() {
            String help = service.getDslSyntaxHelp();

            assertTrue(help.contains("AVERAGE"));
            assertTrue(help.contains("WEIGHTED_SUM"));
            assertTrue(help.contains("MIN"));
            assertTrue(help.contains("MAX"));
            assertTrue(help.contains("THRESHOLD"));
        }
    }
}
