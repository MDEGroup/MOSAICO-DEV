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
    private String dockerFileUrl;
    private String envFileUrl;

    public Deployment() {
        // JPA
    }

    public Deployment(DeploymentMode mode, String dockerFileUrl, String envFileUrl) {
        this.mode = mode;
        this.dockerFileUrl = dockerFileUrl;
        this.envFileUrl = envFileUrl;
    }

    public DeploymentMode getMode() {
        return mode;
    }

    public void setMode(DeploymentMode mode) {
        this.mode = mode;
    }

    public String getDockerFileUrl() {
        return dockerFileUrl;
    }

    public void setDockerFileUrl(String dockerFileUrl) {
        this.dockerFileUrl = dockerFileUrl;
    }

    public String getEnvFileUrl() {
        return envFileUrl;
    }

    public void setEnvFileUrl(String envFileUrl) {
        this.envFileUrl = envFileUrl;
    }
}
