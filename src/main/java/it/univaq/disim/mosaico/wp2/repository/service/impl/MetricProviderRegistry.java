package it.univaq.disim.mosaico.wp2.repository.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;

import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.service.MetricProvider;
@Service
public class MetricProviderRegistry {
    private final Map<Class<? extends MetricKey>, MetricProvider<?>> byKey = new HashMap<>();

    public MetricProviderRegistry(List<MetricProvider<?>> providers) {
        for (var p : providers) 
            byKey.put(p.key(), p);
    }

    @SuppressWarnings("unchecked")
    public <K extends MetricKey> MetricProvider<K> providerFor(Class<K> key) {
        var p = byKey.get(key);
        if (p == null) throw new IllegalArgumentException("No provider for " + key.getSimpleName());
        return (MetricProvider<K>) p;
    }
}