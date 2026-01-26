package it.univaq.disim.mosaico.wp2.repository.mcp;

import java.util.List;
import java.util.Optional;
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
import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.Skill;
import it.univaq.disim.mosaico.wp2.repository.dto.AgentSearchResult;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;
import it.univaq.disim.mosaico.wp2.repository.service.SkillService;

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
    private final BenchmarkService benchmarkService;
    private final SkillService skillService;
    Logger logger = LoggerFactory.getLogger(AgentMCP.class);

    // Constructor injection makes the class easier to test (we can pass a mock
    // AgentService)
    public AgentMCP(AgentService agentService, ObjectMapper objectMapper, BenchmarkService benchmarkService, SkillService skillService) {
        this.agentService = agentService;
        this.objectMapper = objectMapper;
        this.benchmarkService = benchmarkService;
        this.skillService = skillService;
    }

    @McpResource(name = "agents", description = "MOSAICO Agents exposed via MCP", uri = "document/agents")
    public ReadResourceResult listAllAgents() {
        List<Agent> agents = agentService.findAll();
        String json;
        try {
            json = objectMapper.writeValueAsString(agents);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fall back to empty list on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://agents",
                "application/json",
                json
        );

        return new ReadResourceResult(List.of(contents));
    }

    @McpResource(name = "skills", description = "MOSAICO Skills exposed via MCP", uri = "document/skills")
    public ReadResourceResult listAllSkills() {
        List<Skill> skills = skillService.findAll();
        String json;
        try {
            json = objectMapper.writeValueAsString(skills);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fall back to empty list on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://skills",
                "application/json",
                json
        );

        return new ReadResourceResult(List.of(contents));
    }

    @McpResource(name = "benchmarks", description = "MOSAICO Benchmarks exposed via MCP", uri = "document/benchmarks")
    public ReadResourceResult listAllBenchmarks() {
        List<Benchmark> benchmarks = benchmarkService.findAll();
        String json;
        try {
            json = objectMapper.writeValueAsString(benchmarks);
        } catch (JsonProcessingException e) {
            json = "[]"; // Fall back to empty list on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://benchmarks",
                "application/json",
                json
        );

        return new ReadResourceResult(List.of(contents));
    }

    @McpResource(name = "agent", description = "MOSAICO Agent exposed via MCP", uri = "document/agents/{id}")
    public ReadResourceResult getAgent(String id) {
        Optional<Agent> agent = agentService.findById(id);
        if (agent.isEmpty()) {
            return new ReadResourceResult(List.of());
        }

        String json;
        try {
            json = objectMapper.writeValueAsString(agent.get());
        } catch (JsonProcessingException e) {
            json = "{}"; // Fall back to empty object on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://agents/" + id,
                "application/json",
                json
        );
        return new ReadResourceResult(List.of(contents));

    }
    @McpResource(name = "skills", description = "MOSAICO Skill exposed via MCP", uri = "document/skills/{id}")
    public ReadResourceResult getSkill(String id) {
        Optional<Skill> skill = skillService.findById(id);
        if (skill.isEmpty()) {
            return new ReadResourceResult(List.of());
        }

        String json;
        try {
            json = objectMapper.writeValueAsString(skill.get());
        } catch (JsonProcessingException e) {
            json = "{}"; // Fall back to empty object on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://skills/" + id,
                "application/json",
                json
        );
        return new ReadResourceResult(List.of(contents));

    }
    @McpResource(name = "benchmark", description = "MOSAICO benchmark exposed via MCP", uri = "document/benchmarks/{id}")
    public ReadResourceResult getBenchmark(String id) {
        Optional<Benchmark> benchmark = benchmarkService.findById(id);
        if (benchmark.isEmpty()) {
            return new ReadResourceResult(List.of());
        }

        String json;
        try {
            json = objectMapper.writeValueAsString(benchmark.get());
        } catch (JsonProcessingException e) {
            json = "{}"; // Fall back to empty object on serialization errors
        }

        TextResourceContents contents = new TextResourceContents(
                "document://benchmarks/" + id,
                "application/json",
                json
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
