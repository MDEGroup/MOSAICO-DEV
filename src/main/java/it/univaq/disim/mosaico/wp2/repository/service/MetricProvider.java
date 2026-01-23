package it.univaq.disim.mosaico.wp2.repository.service;


import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.MetricKey;
import it.univaq.disim.mosaico.wp2.repository.service.LangfuseService.TraceData;

public interface MetricProvider <K extends MetricKey> {
    Metric compute(Agent agent, String referenceText, String generatedText, TraceData trace);
    Class<K> key();
}
