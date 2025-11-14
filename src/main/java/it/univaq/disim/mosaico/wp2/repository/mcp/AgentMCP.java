package it.univaq.disim.mosaico.wp2.repository.mcp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;

/**
 * AgentMCP provides a lightweight, non-invasive MCP-compatible adapter for
 * exposing Agent resources. This class intentionally does not depend on the
 * optional `spring-ai-starter-mcp-server-webmvc` at compile time; instead it
 * offers programmatic access to Agent resources and a simple metadata
 * descriptor that can be used by an MCP server when the starter is enabled.
 */
@Component
public class AgentMCP {

    @Autowired
    private AgentService agentService;
    @Autowired
    private ObjectMapper objectMapper;
    @McpResource(
        name = "agents",
        description = "MOSAICO Agents exposed via MCP",
        uri = "document/agents"
    )
    public ReadResourceResult listAllAgents() {

        // 2. Li serializzi in JSON
        List<Agent> agents = agentService.findAll();
        String json;
        try {
            json = objectMapper.writeValueAsString(agents);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fallback a lista vuota in caso di errore
        }

        // 3. Costruisci il TextResourceContents (che implementa ResourceContents)
        TextResourceContents contents = new TextResourceContents(
            "document://agents",      // uri del contenuto
            "application/json",       // mime type
            json                      // payload testuale
        );

        // 4. Ritorni il ReadResourceResult con la lista di contents
        return new ReadResourceResult(List.of(contents));
    }

    @McpResource(
        name = "agent",
        description = "MOSAICO Agent exposed via MCP",
        uri = "document/agents/{id}"
    )
    public ReadResourceResult getAgent(String id) {
        Agent agent = agentService.findById(id).orElse(null);
        String json;
        try {
            json = objectMapper.writeValueAsString(agent);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fallback a lista vuota in caso di errore
        }
        TextResourceContents contents = new TextResourceContents(
            "document://agent/{id}",      // uri del contenuto
            "application/json",       // mime type
            json                      // payload testuale
        );
        return new ReadResourceResult(List.of(contents));
        
    }
}
