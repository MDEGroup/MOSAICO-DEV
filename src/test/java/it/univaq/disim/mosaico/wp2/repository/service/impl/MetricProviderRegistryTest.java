package it.univaq.disim.mosaico.wp2.repository.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.univaq.disim.mosaico.wp2.repository.data.BlueMetric;
import it.univaq.disim.mosaico.wp2.repository.data.RougeMetric;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.service.exception.MetricProviderNotFoundException;

/**
 * Tests for MetricProviderRegistry.
 */
class MetricProviderRegistryTest {

    private MetricProviderRegistry registry;
    private BlueMetricProvider blueProvider;
    private RougeMetricProvider rougeProvider;

    @BeforeEach
    void setUp() {
        blueProvider = new BlueMetricProvider();
        rougeProvider = new RougeMetricProvider();
        registry = new MetricProviderRegistry(Arrays.asList(blueProvider, rougeProvider));
    }

    @Test
    void testProviderForBlueMetric() {
        MetricProvider<BlueMetric> provider = registry.providerFor(BlueMetric.class);
        assertNotNull(provider);
        assertEquals(blueProvider, provider);
    }

    @Test
    void testProviderForRougeMetric() {
        MetricProvider<RougeMetric> provider = registry.providerFor(RougeMetric.class);
        assertNotNull(provider);
        assertEquals(rougeProvider, provider);
    }

    @Test
    void testProviderForNonExistentMetric() {
        assertThrows(MetricProviderNotFoundException.class, () -> {
            registry.providerFor(NonExistentMetric.class);
        });
    }

    @Test
    void testGetAllProviders() {
        Collection<MetricProvider<?>> providers = registry.getAllProviders();
        assertNotNull(providers);
        assertEquals(2, providers.size());
        assertTrue(providers.contains(blueProvider));
        assertTrue(providers.contains(rougeProvider));
    }

    @Test
    void testGetRegisteredKeys() {
        var keys = registry.getRegisteredKeys();
        assertNotNull(keys);
        assertEquals(2, keys.size());
        assertTrue(keys.contains(BlueMetric.class));
        assertTrue(keys.contains(RougeMetric.class));
    }

    @Test
    void testHasProvider() {
        assertTrue(registry.hasProvider(BlueMetric.class));
        assertTrue(registry.hasProvider(RougeMetric.class));
        assertFalse(registry.hasProvider(NonExistentMetric.class));
    }

    // Test helper class
    private static class NonExistentMetric implements it.univaq.disim.mosaico.wp2.repository.data.MetricKey {
    }
}
