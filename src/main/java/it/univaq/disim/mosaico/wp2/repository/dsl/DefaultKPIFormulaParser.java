package it.univaq.disim.mosaico.wp2.repository.dsl;

import it.univaq.disim.mosaico.wp2.repository.data.KPIFormula;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default placeholder implementation of KPIFormulaParser.
 *
 * <p><b>IMPORTANT:</b> This is a placeholder/dummy implementation that supports
 * basic formula patterns. The actual DSL parsing will be implemented by the partner
 * using xText grammar. This implementation provides basic functionality for testing
 * and development until the full DSL is available.</p>
 *
 * <p>Supported placeholder patterns:</p>
 * <ul>
 *   <li>AVERAGE(METRIC1, METRIC2, ...) - computes arithmetic mean</li>
 *   <li>WEIGHTED_SUM(METRIC1: weight1, METRIC2: weight2, ...) - weighted combination</li>
 *   <li>MIN(METRIC1, METRIC2, ...) - minimum value</li>
 *   <li>MAX(METRIC1, METRIC2, ...) - maximum value</li>
 *   <li>THRESHOLD(METRIC, threshold) - returns 1.0 if metric >= threshold, else 0.0</li>
 * </ul>
 */
@Component
public class DefaultKPIFormulaParser implements KPIFormulaParser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKPIFormulaParser.class);

    private final Set<String> knownMetricKeys;

    // Patterns for basic formula parsing
    private static final Pattern AVERAGE_PATTERN = Pattern.compile(
        "AVERAGE\\s*\\(\\s*([^)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern WEIGHTED_SUM_PATTERN = Pattern.compile(
        "WEIGHTED_SUM\\s*\\(\\s*([^)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MIN_PATTERN = Pattern.compile(
        "MIN\\s*\\(\\s*([^)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MAX_PATTERN = Pattern.compile(
        "MAX\\s*\\(\\s*([^)]+)\\s*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern THRESHOLD_PATTERN = Pattern.compile(
        "THRESHOLD\\s*\\(\\s*(\\w+)\\s*,\\s*([\\d.]+)\\s*\\)", Pattern.CASE_INSENSITIVE);

    public DefaultKPIFormulaParser() {
        this.knownMetricKeys = new HashSet<>();
        // Initialize with known metric types
        for (MetricType type : MetricType.values()) {
            knownMetricKeys.add(type.name());
        }
    }

    @Override
    public DslParseResult parse(String dslExpression) {
        if (dslExpression == null || dslExpression.isBlank()) {
            return DslParseResult.failure(
                new DslValidationError(1, 1, "DSL expression cannot be empty"),
                dslExpression
            );
        }

        String trimmed = dslExpression.trim();
        logger.debug("Parsing DSL expression: {}", trimmed);

        try {
            // Try each pattern in order
            Matcher averageMatcher = AVERAGE_PATTERN.matcher(trimmed);
            if (averageMatcher.matches()) {
                return parseAverageFormula(averageMatcher.group(1), dslExpression);
            }

            Matcher weightedSumMatcher = WEIGHTED_SUM_PATTERN.matcher(trimmed);
            if (weightedSumMatcher.matches()) {
                return parseWeightedSumFormula(weightedSumMatcher.group(1), dslExpression);
            }

            Matcher minMatcher = MIN_PATTERN.matcher(trimmed);
            if (minMatcher.matches()) {
                return parseMinFormula(minMatcher.group(1), dslExpression);
            }

            Matcher maxMatcher = MAX_PATTERN.matcher(trimmed);
            if (maxMatcher.matches()) {
                return parseMaxFormula(maxMatcher.group(1), dslExpression);
            }

            Matcher thresholdMatcher = THRESHOLD_PATTERN.matcher(trimmed);
            if (thresholdMatcher.matches()) {
                return parseThresholdFormula(thresholdMatcher.group(1),
                    Double.parseDouble(thresholdMatcher.group(2)), dslExpression);
            }

            // No pattern matched
            return DslParseResult.failure(
                new DslValidationError(1, 1,
                    "Unrecognized formula pattern. Supported: AVERAGE, WEIGHTED_SUM, MIN, MAX, THRESHOLD. " +
                    "Full DSL support will be provided by partner's xText implementation."),
                dslExpression
            );

        } catch (Exception e) {
            logger.error("Error parsing DSL expression: {}", e.getMessage(), e);
            return DslParseResult.failure(
                new DslValidationError(1, 1, "Parse error: " + e.getMessage()),
                dslExpression
            );
        }
    }

    @Override
    public DslParseResult validate(String dslExpression) {
        // For now, validation is the same as parsing
        // The partner's xText implementation may provide faster validation
        return parse(dslExpression);
    }

    @Override
    public Set<String> getKnownMetricKeys() {
        return Collections.unmodifiableSet(knownMetricKeys);
    }

    @Override
    public void registerMetricKeys(Set<String> metricKeys) {
        if (metricKeys != null) {
            knownMetricKeys.addAll(metricKeys);
            logger.info("Registered {} additional metric keys", metricKeys.size());
        }
    }

    private DslParseResult parseAverageFormula(String args, String originalDsl) {
        List<String> metrics = parseMetricList(args);
        List<DslValidationError> errors = validateMetrics(metrics);

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, originalDsl);
        }

        Set<String> referencedMetrics = new HashSet<>(metrics);
        KPIFormula formula = metricValues -> {
            double sum = 0.0;
            int count = 0;
            for (String metric : metrics) {
                Double value = findMetricValue(metricValues, metric);
                if (value != null) {
                    sum += value;
                    count++;
                }
            }
            return count > 0 ? sum / count : 0.0;
        };

        return DslParseResult.success(formula, referencedMetrics, originalDsl);
    }

    private DslParseResult parseWeightedSumFormula(String args, String originalDsl) {
        Map<String, Double> weights = parseWeightedMetrics(args);
        List<DslValidationError> errors = validateMetrics(new ArrayList<>(weights.keySet()));

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, originalDsl);
        }

        Set<String> referencedMetrics = weights.keySet();
        KPIFormula formula = metricValues -> {
            double sum = 0.0;
            for (Map.Entry<String, Double> entry : weights.entrySet()) {
                Double value = findMetricValue(metricValues, entry.getKey());
                if (value != null) {
                    sum += value * entry.getValue();
                }
            }
            return sum;
        };

        return DslParseResult.success(formula, referencedMetrics, originalDsl);
    }

    private DslParseResult parseMinFormula(String args, String originalDsl) {
        List<String> metrics = parseMetricList(args);
        List<DslValidationError> errors = validateMetrics(metrics);

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, originalDsl);
        }

        Set<String> referencedMetrics = new HashSet<>(metrics);
        KPIFormula formula = metricValues -> {
            double min = Double.MAX_VALUE;
            for (String metric : metrics) {
                Double value = findMetricValue(metricValues, metric);
                if (value != null && value < min) {
                    min = value;
                }
            }
            return min == Double.MAX_VALUE ? 0.0 : min;
        };

        return DslParseResult.success(formula, referencedMetrics, originalDsl);
    }

    private DslParseResult parseMaxFormula(String args, String originalDsl) {
        List<String> metrics = parseMetricList(args);
        List<DslValidationError> errors = validateMetrics(metrics);

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, originalDsl);
        }

        Set<String> referencedMetrics = new HashSet<>(metrics);
        KPIFormula formula = metricValues -> {
            double max = Double.MIN_VALUE;
            for (String metric : metrics) {
                Double value = findMetricValue(metricValues, metric);
                if (value != null && value > max) {
                    max = value;
                }
            }
            return max == Double.MIN_VALUE ? 0.0 : max;
        };

        return DslParseResult.success(formula, referencedMetrics, originalDsl);
    }

    private DslParseResult parseThresholdFormula(String metric, double threshold, String originalDsl) {
        List<DslValidationError> errors = validateMetrics(List.of(metric));

        if (!errors.isEmpty()) {
            return DslParseResult.failure(errors, originalDsl);
        }

        Set<String> referencedMetrics = Set.of(metric);
        KPIFormula formula = metricValues -> {
            Double value = findMetricValue(metricValues, metric);
            return (value != null && value >= threshold) ? 1.0 : 0.0;
        };

        return DslParseResult.success(formula, referencedMetrics, originalDsl);
    }

    private List<String> parseMetricList(String args) {
        List<String> metrics = new ArrayList<>();
        for (String part : args.split(",")) {
            String metric = part.trim().toUpperCase();
            if (!metric.isEmpty()) {
                metrics.add(metric);
            }
        }
        return metrics;
    }

    private Map<String, Double> parseWeightedMetrics(String args) {
        Map<String, Double> weights = new LinkedHashMap<>();
        for (String part : args.split(",")) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                String metric = keyValue[0].trim().toUpperCase();
                double weight = Double.parseDouble(keyValue[1].trim());
                weights.put(metric, weight);
            }
        }
        return weights;
    }

    private List<DslValidationError> validateMetrics(List<String> metrics) {
        List<DslValidationError> errors = new ArrayList<>();
        for (String metric : metrics) {
            if (!knownMetricKeys.contains(metric)) {
                errors.add(new DslValidationError(1, 1,
                    "Unknown metric '" + metric + "'. Known metrics: " + knownMetricKeys,
                    "UNKNOWN_METRIC",
                    DslValidationError.ErrorSeverity.ERROR));
            }
        }
        return errors;
    }

    @SuppressWarnings("unchecked")
    private Double findMetricValue(Map<?, Double> metricValues, String metricName) {
        // Try direct key lookup first
        for (Map.Entry<?, Double> entry : metricValues.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof Class<?> clazz) {
                if (clazz.getSimpleName().toUpperCase().contains(metricName)) {
                    return entry.getValue();
                }
            } else if (key instanceof String strKey) {
                if (strKey.equalsIgnoreCase(metricName)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
