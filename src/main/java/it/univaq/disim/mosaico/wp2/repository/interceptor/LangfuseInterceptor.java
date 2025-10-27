package it.univaq.disim.mosaico.wp2.repository.interceptor;

import it.univaq.disim.mosaico.wp2.repository.service.LangfuseTracingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor to automatically trace Agent API requests with Langfuse.
 * Captures request details, response status, and timing.
 * Only activated when LangfuseTracingService is available.
 */
@Component
@ConditionalOnBean(LangfuseTracingService.class)
public class LangfuseInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LangfuseInterceptor.class);
    private static final String TRACE_ID_ATTR = "langfuse.traceId";
    private static final String START_TIME_ATTR = "langfuse.startTime";

    private final LangfuseTracingService langfuseService;

    public LangfuseInterceptor(@Autowired LangfuseTracingService langfuseService) {
        this.langfuseService = langfuseService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!langfuseService.isEnabled()) {
            return true;
        }

        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        
        // Extract agentId from path if present (e.g. /api/agents/{id})
        String agentId = extractAgentId(uri);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("http.method", method);
        metadata.put("http.path", uri);
        if (queryString != null) {
            metadata.put("http.query", queryString);
        }
        if (agentId != null) {
            metadata.put("agent.id", agentId);
        }
        metadata.put("http.remoteAddr", request.getRemoteAddr());
        metadata.put("http.userAgent", request.getHeader("User-Agent"));

        String traceName = method + " " + uri;
        String traceId = langfuseService.startTrace(traceName, metadata);
        
        request.setAttribute(TRACE_ID_ATTR, traceId);
        
        logger.debug("Started Langfuse trace for: {} [{}]", traceName, traceId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) {
        // Optional: log intermediate state if needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        if (!langfuseService.isEnabled()) {
            return;
        }

        String traceId = (String) request.getAttribute(TRACE_ID_ATTR);
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);

        if (traceId == null || startTime == null) {
            return;
        }

        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();

        Map<String, Object> output = new HashMap<>();
        output.put("http.status", status);
        output.put("http.statusCategory", getStatusCategory(status));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("duration.ms", duration);
        metadata.put("success", status >= 200 && status < 300);
        
        if (ex != null) {
            metadata.put("error.type", ex.getClass().getSimpleName());
            metadata.put("error.message", ex.getMessage());
        }

        langfuseService.endTrace(traceId, output, metadata);
        
        logger.debug("Ended Langfuse trace: {} ({}ms, status={})", traceId, duration, status);
    }

    /**
     * Extract agent ID from URI path.
     * Matches patterns like: /api/agents/{id} or /api/agents/{id}/...
     */
    private String extractAgentId(String uri) {
        if (uri == null || !uri.contains("/api/agents/")) {
            return null;
        }
        
        String[] parts = uri.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("agents".equals(parts[i]) && i + 1 < parts.length) {
                String candidate = parts[i + 1];
                // Simple validation: not a known sub-resource
                if (!candidate.isEmpty() && !candidate.equals("search")) {
                    return candidate;
                }
            }
        }
        return null;
    }

    /**
     * Categorize HTTP status code.
     */
    private String getStatusCategory(int status) {
        if (status >= 200 && status < 300) return "success";
        if (status >= 300 && status < 400) return "redirect";
        if (status >= 400 && status < 500) return "client_error";
        if (status >= 500) return "server_error";
        return "unknown";
    }
}
