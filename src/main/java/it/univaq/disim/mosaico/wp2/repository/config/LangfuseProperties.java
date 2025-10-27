package it.univaq.disim.mosaico.wp2.repository.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Langfuse tracing integration.
 * Maps to langfuse.* properties in application.properties.
 */
@Component
@ConfigurationProperties(prefix = "langfuse")
public class LangfuseProperties {

    /**
     * Enable/disable Langfuse tracing.
     */
    private boolean enabled = false;

    /**
     * Langfuse base URL (e.g. http://localhost:3000).
     */
    private String baseUrl = "http://localhost:3000";

    /**
     * Langfuse public key for ingestion API.
     */
    private String publicKey;

    /**
     * Langfuse secret key for ingestion API.
     */
    private String secretKey;

    /**
     * Timeout for HTTP requests in seconds.
     */
    private int timeoutSeconds = 5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Check if Langfuse is properly configured (enabled + keys present).
     */
    public boolean isConfigured() {
        return enabled && publicKey != null && !publicKey.isEmpty() 
               && secretKey != null && !secretKey.isEmpty();
    }
}
