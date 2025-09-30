package it.univaq.disim.mosaico.wp2.repository.data;

/**
 * UsageBreakdown class for MOSAICO taxonomy.
 * Represents detailed usage breakdown.
 */
public record UsageBreakdown(
    long promptTokens,
    long completionTokens,
    int requests,
    long bytesIn,
    long bytesOut
) {}