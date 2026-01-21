package it.univaq.disim.mosaico.wp2.repository.data;

import it.univaq.disim.mosaico.wp2.repository.data.enums.DeploymentMode;
import jakarta.persistence.Embeddable;

/**
 * Transient class representing Agent deployment configuration.
 * These values are set from environment files at runtime, not persisted in database.
 */
@Embeddable
public class Deployment {

    private DeploymentMode mode;
    private String dockerImageReference;

    public Deployment() {
        // JPA
    }

    public Deployment(DeploymentMode mode, String dockerImageReference) {
        this.mode = mode;
        this.dockerImageReference = dockerImageReference;
    }

    public DeploymentMode getMode() {
        return mode;
    }

    public void setMode(DeploymentMode mode) {
        this.mode = mode;
    }

    public String getDockerImageReference() {
        return dockerImageReference;
    }

    public void setDockerImageReference(String dockerImageReference) {
        this.dockerImageReference = dockerImageReference;
    }
}
