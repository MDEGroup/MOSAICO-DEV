package it.univaq.disim.mosaico.wp2.repository.service.exception;

import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;

/**
 * Exception thrown when a MetricProvider is not found for a given MetricKey.
 */
public class MetricProviderNotFoundException extends RuntimeException {
    private final Class<? extends MetricKey> metricKey;

    public MetricProviderNotFoundException(Class<? extends MetricKey> metricKey) {
        super("No metric provider found for: " + metricKey.getSimpleName());
        this.metricKey = metricKey;
    }

    public Class<? extends MetricKey> getMetricKey() {
        return metricKey;
    }
}
