package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

/**
 * Specification for how to compute a KPI.
 * Contains the formula and metadata about the computation.
 */
@Embeddable
public class KPISpecification {
    private String formulaType; // e.g., "AVERAGE", "WEIGHTED_SUM", "THRESHOLD"
    private String formulaConfig; // JSON or other serialized configuration

    @Transient
    private KPIFormula formula; // The actual formula implementation

    public KPISpecification() {}

    public KPISpecification(String formulaType, String formulaConfig, KPIFormula formula) {
        this.formulaType = formulaType;
        this.formulaConfig = formulaConfig;
        this.formula = formula;
    }

    public String getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(String formulaType) {
        this.formulaType = formulaType;
    }

    public String getFormulaConfig() {
        return formulaConfig;
    }

    public void setFormulaConfig(String formulaConfig) {
        this.formulaConfig = formulaConfig;
    }

    public KPIFormula getFormula() {
        return formula;
    }

    public void setFormula(KPIFormula formula) {
        this.formula = formula;
    }
}
