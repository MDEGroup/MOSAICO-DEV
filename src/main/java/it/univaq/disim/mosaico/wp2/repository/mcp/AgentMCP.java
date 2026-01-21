package it.univaq.disim.mosaico.wp2.repository.mcp;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.dto.AgentSearchResult;
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

    private final AgentService agentService;
    private final ObjectMapper objectMapper;
    Logger logger = LoggerFactory.getLogger(AgentMCP.class);

    // Constructor injection makes the class easier to test (we can pass a mock
    // AgentService)
    public AgentMCP(AgentService agentService, ObjectMapper objectMapper) {
        this.agentService = agentService;
        this.objectMapper = objectMapper;
    }

    @McpResource(name = "agents", description = "MOSAICO Agents exposed via MCP", uri = "document/agents")
    public ReadResourceResult listAllAgents() {

        // 2. Li serializzi in JSON
        // List<Agent> agents = agentService.findAll();
        Provider testProvider = testProvider = new Provider(
                "OpenAI",
                "AI company providing language models",
                "https://openai.com");
        Agent codeReviewAgent = new Agent(
                "Code Review Agent",
                "AI agent specialized in code review",
                "v1.0",
                testProvider,
                "MIT",
                "Code quality beliefs",
                "Review code efficiently",
                "Deliver high-quality reviews",
                "Specialist",
                "Code Review",
                List.of(IOModality.TEXT),
                "Background in software engineering",
                List.of(), // skills
                List.of(), // tools
                List.of(), // memory
                List.of(), // interactionProtocols
                List.of() // agentConsumption
        );
        codeReviewAgent.setId("agent1");

        Agent summaryAgent = new Agent(
                "Code Summarization",
                "AI agent specialized in code summarization",
                "v1.0",
                testProvider,
                "MIT",
                "Code quality beliefs",
                "Review code efficiently",
                "Deliver high-quality reviews",
                "Specialist",
                "Code Review",
                List.of(IOModality.TEXT),
                "Background in software engineering",
                List.of(), // skills
                List.of(), // tools
                List.of(), // memory
                List.of(), // interactionProtocols
                List.of() // agentConsumption
        );
        summaryAgent.setId("agent2");

        List<Agent> agents = List.of(codeReviewAgent, summaryAgent);
        String json;
        try {
            json = objectMapper.writeValueAsString(agents);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fallback a lista vuota in caso di errore
        }

        // 3. Costruisci il TextResourceContents (che implementa ResourceContents)
        TextResourceContents contents = new TextResourceContents(
                "document://agents", // uri del contenuto
                "application/json", // mime type
                json // payload testuale
        );

        // 4. Ritorni il ReadResourceResult con la lista di contents
        return new ReadResourceResult(List.of(contents));
    }

    @McpResource(name = "agent", description = "MOSAICO Agent exposed via MCP", uri = "document/agents/{id}")
    public ReadResourceResult getAgent(String id) {
        // Agent agent = agentService.findById(id).orElse(null);
        Provider testProvider = testProvider = new Provider(
                "OpenAI",
                "AI company providing language models",
                "https://openai.com");
        Agent agent = new Agent(
                "Code Review Agent",
                "AI agent specialized in code review",
                "v1.0",
                testProvider,
                "MIT",
                "Code quality beliefs",
                "Review code efficiently",
                "Deliver high-quality reviews",
                "Specialist",
                "Code Review",
                List.of(IOModality.TEXT),
                "Background in software engineering",
                List.of(), // skills
                List.of(), // tools
                List.of(), // memory
                List.of(), // interactionProtocols
                List.of() // agentConsumption
        );
        agent.setId(id);
        String json;
        try {
            json = objectMapper.writeValueAsString(agent);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fallback a lista vuota in caso di errore
        }
        TextResourceContents contents = new TextResourceContents(
                "document://agent/{id}", // uri del contenuto
                "application/json", // mime type
                json // payload testuale
        );
        return new ReadResourceResult(List.of(contents));

    }

    @McpTool(name = "AgentMCPTool", description = "Tool to search MOSAICO Agents via MCP")
    public String searchAgents(@McpToolParam String query, @McpToolParam int topK) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("query must not be empty");
        }
        if (topK <= 0) {
            throw new IllegalArgumentException("topK must be greater than zero");
        }
        logger.info("Query: {}", query);
        logger.info("TopK: {}", topK);

        List<AgentSearchResult> results = agentService.semanticSearchWithScores(query, Map.of(), topK);
        logger.info("Result size: {}", results.size());
        String json;
        try {
            json = objectMapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fallback a lista vuota in caso di errore
        }
        logger.info(json);
        return json;

    }
}
