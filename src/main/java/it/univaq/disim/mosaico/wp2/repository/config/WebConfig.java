package it.univaq.disim.mosaico.wp2.repository.config;

import it.univaq.disim.mosaico.wp2.repository.interceptor.LangfuseInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for MOSAICO Repository.
 * Registers Langfuse interceptor for agent API tracing when available.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    private final LangfuseInterceptor langfuseInterceptor;
    private final LangfuseProperties langfuseProperties;

    public WebConfig(@Autowired(required = false) LangfuseInterceptor langfuseInterceptor, 
                     @Autowired(required = false) LangfuseProperties langfuseProperties) {
        this.langfuseInterceptor = langfuseInterceptor;
        this.langfuseProperties = langfuseProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (langfuseInterceptor != null && langfuseProperties != null && langfuseProperties.isEnabled()) {
            registry.addInterceptor(langfuseInterceptor)
                    .addPathPatterns("/api/agents/**");
            logger.info("Langfuse interceptor registered for /api/agents/** endpoints");
        } else {
            logger.debug("Langfuse tracing is disabled or not available");
        }
    }
}
