package it.univaq.disim.mosaico.wp2.repository.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

/**
 * Specification for how to compute a KPI.
 * Contains the formula DSL text and metadata about the computation.
 *
 * <p><b>User Story:</b> As a repository maintainer, I need KPISpecification to store
 * the raw DSL text and metadata so the system can reload and evaluate formulas after restarts.</p>
 */
@Embeddable
public class KPISpecification {
    private String formulaType; // e.g., "AVERAGE", "WEIGHTED_SUM", "THRESHOLD", "CUSTOM"

    @Column(name = "formula_config", columnDefinition = "text")
    private String formulaConfig; // JSON or other serialized configuration

    @Column(name = "dsl_text", columnDefinition = "text")
    private String dslText; // Raw DSL formula text for persistence and reload

    @Column(name = "dsl_version")
    private String dslVersion; // Version of DSL grammar used (for future compatibility)

    @Transient
    private KPIFormula formula; // The actual formula implementation (built from DSL on demand)

    public KPISpecification() {}

    public KPISpecification(String formulaType, String formulaConfig, KPIFormula formula) {
        this.formulaType = formulaType;
        this.formulaConfig = formulaConfig;
        this.formula = formula;
    }

    public KPISpecification(String dslText, String formulaType) {
        this.dslText = dslText;
        this.formulaType = formulaType;
        this.dslVersion = "1.0";
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

    public String getDslText() {
        return dslText;
    }

    public void setDslText(String dslText) {
        this.dslText = dslText;
    }

    public String getDslVersion() {
        return dslVersion;
    }

    public void setDslVersion(String dslVersion) {
        this.dslVersion = dslVersion;
    }

    public KPIFormula getFormula() {
        return formula;
    }

    public void setFormula(KPIFormula formula) {
        this.formula = formula;
    }

    /**
     * Checks if this specification has DSL text that can be parsed.
     */
    public boolean hasDslText() {
        return dslText != null && !dslText.isBlank();
    }

    /**
     * Checks if this specification has a pre-built formula.
     */
    public boolean hasFormula() {
        return formula != null;
    }
}
