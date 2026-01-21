package it.univaq.disim.mosaico.wp2.repository.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for enabling Spring scheduling and async processing.
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {
    // Configuration is enabled through annotations
}
