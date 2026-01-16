package it.univaq.disim.mosaico.wp2.repository.service;

import com.langfuse.client.resources.commons.types.TraceWithFullDetails;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;

public interface MetricProvider <K extends MetricKey> {
    Metric compute(Agent agent, String referenceText, String generatedText, TraceWithFullDetails trace);
    Class<K> key();
}
