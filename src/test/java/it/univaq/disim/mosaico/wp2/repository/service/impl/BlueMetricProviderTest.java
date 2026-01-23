package it.univaq.disim.mosaico.wp2.repository.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;

class BlueMetricProviderTest {

    private BlueMetricProvider provider;
    private Agent agent;

    @BeforeEach
    void setUp() {
        provider = new BlueMetricProvider();
        agent = new Agent();
    }

    @Test
    void computeReturnsPerfectScoreForIdenticalTexts() {
        Metric metric = provider.compute(agent, "Hello World", "Hello World", null);

        assertNotNull(metric);
        assertEquals("BLEU Score", metric.getName());
        assertEquals(MetricType.BLEU, metric.getType());
        assertEquals("score", metric.getUnit());
        assertEquals(1.0f, metric.getFloatValue().orElseThrow(), 1e-5f);
    }

    @Test
    void computeReturnsZeroWhenGeneratedTextEmpty() {
        Metric metric = provider.compute(agent, "Reference text", "", null);

        assertEquals(0.0f, metric.getFloatValue().orElseThrow(), 1e-5f);
    }

    @Test
    void computeCalculatesExpectedScoreForPartialOverlap() {
        Metric metric = provider.compute(agent, "hello world", "hello", null);

        assertEquals(2f / 3f, metric.getFloatValue().orElseThrow(), 1e-5f);
    }
}
