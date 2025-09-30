package it.univaq.disim.mosaico.wp2.repository.data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MonetaryCharge class for MOSAICO taxonomy.
 * Represents monetary charges for agent usage.
 */
public record MonetaryCharge(
    UUID id,
    BigDecimal amount,
    String currency
) {}