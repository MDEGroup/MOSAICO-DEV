package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
import it.univaq.disim.mosaico.wp2.repository.service.exception.MetricProviderNotFoundException;

/**
 * Registry for managing MetricProvider instances.
 * Automatically discovers and registers all MetricProvider beans at startup.
 * Provides type-safe lookup of providers by their MetricKey.
 */
@Service
public class MetricProviderRegistry {
    private final Map<Class<? extends MetricKey>, MetricProvider<?>> byKey = new HashMap<>();

    /**
     * Constructor that auto-discovers all MetricProvider beans via Spring dependency injection.
     * @param providers List of all MetricProvider instances managed by Spring
     */
    public MetricProviderRegistry(List<MetricProvider<?>> providers) {
        for (var p : providers)
            byKey.put(p.key(), p);
    }

    /**
     * Retrieves a MetricProvider for a specific MetricKey type.
     * @param key The class of the MetricKey
     * @return The corresponding MetricProvider
     * @throws MetricProviderNotFoundException if no provider is registered for the given key
     */
    @SuppressWarnings("unchecked")
    public <K extends MetricKey> MetricProvider<K> providerFor(Class<K> key) {
        var p = byKey.get(key);
        if (p == null) throw new MetricProviderNotFoundException(key);
        return (MetricProvider<K>) p;
    }

    /**
     * Returns all registered MetricProviders.
     * Useful for computing all available metrics in a benchmark.
     * @return Collection of all registered providers
     */
    public Collection<MetricProvider<?>> getAllProviders() {
        return byKey.values();
    }

    /**
     * Returns all registered MetricKey classes.
     * @return Set of MetricKey classes that have registered providers
     */
    public Set<Class<? extends MetricKey>> getRegisteredKeys() {
        return byKey.keySet();
    }

    /**
     * Checks if a provider is registered for a given MetricKey.
     * @param key The MetricKey class to check
     * @return true if a provider exists, false otherwise
     */
    public boolean hasProvider(Class<? extends MetricKey> key) {
        return byKey.containsKey(key);
    }
}