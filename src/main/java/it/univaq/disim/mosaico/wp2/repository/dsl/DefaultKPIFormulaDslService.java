package it.univaq.disim.mosaico.wp2.repository.dsl;

import it.univaq.disim.mosaico.wp2.repository.data.KPISpecification;
import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslParseException;
import it.univaq.disim.mosaico.wp2.repository.service.exception.DslValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of KPIFormulaDslService.
 *
 * <p><b>IMPORTANT:</b> This is a placeholder implementation that will be enhanced
 * when the partner's xText DSL grammar is integrated. It provides basic functionality
 * for testing and development.</p>
 */
@Service
public class DefaultKPIFormulaDslService implements KPIFormulaDslService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKPIFormulaDslService.class);

    private final KPIFormulaParser parser;

    @Autowired
    public DefaultKPIFormulaDslService(KPIFormulaParser parser) {
        this.parser = parser;
    }

    @Override
    public KPIFormula parseFormula(String dslExpression) throws DslParseException, DslValidationException {
        logger.debug("Parsing DSL formula: {}", dslExpression);

        DslParseResult result = parser.parse(dslExpression);

        if (!result.isSuccess()) {
            String errorMessage = result.getErrorsAsString();
            logger.error("DSL parsing failed: {}", errorMessage);
            throw new DslParseException(errorMessage, result.getErrors());
        }

        return result.getFormula();
    }

    @Override
    public KPIFormula buildFromSpecification(KPISpecification specification)
            throws DslParseException, DslValidationException {
        if (specification == null) {
            throw new DslValidationException("KPISpecification cannot be null");
        }

        String dslText = specification.getDslText();
        if (dslText == null || dslText.isBlank()) {
            // Fall back to formula type for backward compatibility
            String formulaType = specification.getFormulaType();
            if (formulaType != null && !formulaType.isBlank()) {
                logger.debug("No DSL text found, using formula type: {}", formulaType);
                // Return the legacy formula if available
                KPIFormula legacyFormula = specification.getFormula();
                if (legacyFormula != null) {
                    return legacyFormula;
                }
                // Try to construct a basic formula from type
                dslText = constructDslFromType(formulaType, specification.getFormulaConfig());
            } else {
                throw new DslValidationException("KPISpecification has no DSL text or formula type");
            }
        }

        return parseFormula(dslText);
    }

    @Override
    public DslParseResult validateFormula(String dslExpression) {
        logger.debug("Validating DSL formula: {}", dslExpression);
        return parser.validate(dslExpression);
    }

    @Override
    public DslParseResult validateFormulaAgainstMetrics(String dslExpression, Set<String> availableMetrics) {
        logger.debug("Validating DSL formula against metrics: {}", availableMetrics);

        DslParseResult result = parser.validate(dslExpression);

        if (!result.isSuccess()) {
            return result;
        }

        // Check that all referenced metrics are available
        Set<String> referenced = result.getReferencedMetrics();
        List<DslValidationError> errors = new ArrayList<>();

        for (String metric : referenced) {
            if (!availableMetrics.contains(metric)) {
                errors.add(new DslValidationError(1, 1,
                    "Metric '" + metric + "' is referenced but not available. Available metrics: " + availableMetrics,
                    "METRIC_NOT_AVAILABLE",
                    DslValidationError.ErrorSeverity.ERROR));
            }
        }

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, dslExpression);
        }

        return result;
    }

    @Override
    public KPISpecification createSpecification(String dslExpression, String formulaType)
            throws DslValidationException {
        // Validate first
        DslParseResult result = validateFormula(dslExpression);

        if (!result.isSuccess()) {
            throw new DslValidationException(
                "DSL validation failed: " + result.getErrorsAsString(),
                result.getErrors()
            );
        }

        // Create specification
        KPISpecification spec = new KPISpecification();
        spec.setDslText(dslExpression);
        spec.setFormulaType(formulaType != null ? formulaType : detectFormulaType(dslExpression));
        spec.setDslVersion("1.0"); // Placeholder version

        logger.info("Created KPISpecification with DSL: {}", dslExpression);
        return spec;
    }

    @Override
    public Set<String> getKnownMetricKeys() {
        return parser.getKnownMetricKeys();
    }

    @Override
    public void registerCustomMetricKeys(Set<String> metricKeys) {
        parser.registerMetricKeys(metricKeys);
        logger.info("Registered custom metric keys: {}", metricKeys);
    }

    @Override
    public String getDslSyntaxHelp() {
        return """
            KPI Formula DSL Syntax (Placeholder Implementation)
            ====================================================

            Note: This is a placeholder implementation. Full DSL support will be
            provided by the partner's xText grammar implementation.

            Currently Supported Patterns:

            1. AVERAGE(metric1, metric2, ...)
               Computes the arithmetic mean of the specified metrics.
               Example: AVERAGE(ROUGE, BLEU, F1_SCORE)

            2. WEIGHTED_SUM(metric1: weight1, metric2: weight2, ...)
               Computes a weighted sum of metrics.
               Example: WEIGHTED_SUM(ROUGE: 0.6, BLEU: 0.4)

            3. MIN(metric1, metric2, ...)
               Returns the minimum value among the specified metrics.
               Example: MIN(ROUGE, BLEU)

            4. MAX(metric1, metric2, ...)
               Returns the maximum value among the specified metrics.
               Example: MAX(ROUGE, BLEU)

            5. THRESHOLD(metric, value)
               Returns 1.0 if metric >= threshold, 0.0 otherwise.
               Example: THRESHOLD(ROUGE, 0.7)

            Available Metrics:
            - ROUGE, BLEU, ACCURACY, PRECISION, RECALL, F1_SCORE
            - Custom metrics can be registered via registerCustomMetricKeys()

            Future DSL Features (Partner Implementation):
            - Arithmetic expressions: (ROUGE + BLEU) / 2
            - Conditional expressions: IF ROUGE > 0.8 THEN 1.0 ELSE 0.0
            - Mathematical functions: SQRT, POW, ABS, etc.
            - Variable bindings and named expressions
            """;
    }

    private String detectFormulaType(String dslExpression) {
        String upper = dslExpression.toUpperCase().trim();
        if (upper.startsWith("AVERAGE")) return "AVERAGE";
        if (upper.startsWith("WEIGHTED_SUM")) return "WEIGHTED_SUM";
        if (upper.startsWith("MIN")) return "MIN";
        if (upper.startsWith("MAX")) return "MAX";
        if (upper.startsWith("THRESHOLD")) return "THRESHOLD";
        return "CUSTOM";
    }

    private String constructDslFromType(String formulaType, String formulaConfig) {
        // Try to construct a basic DSL expression from legacy formula type
        return switch (formulaType.toUpperCase()) {
            case "AVERAGE" -> "AVERAGE(ROUGE, BLEU)";
            case "WEIGHTED_SUM" -> "WEIGHTED_SUM(ROUGE: 0.5, BLEU: 0.5)";
            case "THRESHOLD" -> "THRESHOLD(ROUGE, 0.7)";
            default -> "AVERAGE(ROUGE, BLEU)"; // Default fallback
        };
    }
}
