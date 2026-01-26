package it.univaq.disim.mosaico.wp2.repository.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRunRepository;
import it.univaq.disim.mosaico.wp2.repository.service.AgentService;
import it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService;
import it.univaq.disim.mosaico.wp2.repository.service.SkillService;

/**
 * Plain unit tests that instantiate AgentMCP directly (no Spring context).
 * This avoids MCP autoconfiguration/proxying and tests the method logic
 * that assembles MCP resource payloads.
 */
class AgentMcpTest {

    @Test
    void listAllAgents_returnsReadResourceResultWithJsonArray() throws Exception {
        // create mock AgentService
        AgentService agentService = mock(it.univaq.disim.mosaico.wp2.repository.service.AgentService.class);
        BenchmarkService benchmarkService = mock(it.univaq.disim.mosaico.wp2.repository.service.BenchmarkService.class);
        SkillService skillService = mock(it.univaq.disim.mosaico.wp2.repository.service.SkillService.class);
        ObjectMapper mapper = new ObjectMapper();

        // prepare test data
        Provider testProvider = new it.univaq.disim.mosaico.wp2.repository.data.Provider("OpenAI", "AI company providing language models", "https://openai.com"
        );
        Agent a1 = new it.univaq.disim.mosaico.wp2.repository.data.Agent(
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
            List.of(),
            "Background",
            null, // a2aAgentCardUrl
            null, // deployment
            List.of(), List.of(), List.of(), List.of(), List.of()
        );
        a1.setId("agent1");
        when(agentService.findAll()).thenReturn(List.of(a1));

        AgentMCP agentMCP = new AgentMCP(agentService, mapper, benchmarkService, skillService, mock(BenchmarkRunRepository.class));

        ReadResourceResult res = agentMCP.listAllAgents();
        assertThat(res).isNotNull();
        List<ResourceContents> contents = res.contents();
        assertThat(contents).isNotEmpty();
        ResourceContents rc = contents.get(0);
        assertThat(rc).isInstanceOf(TextResourceContents.class);
        String text = ((TextResourceContents) rc).text();
        assertThat(text).contains("agent1");
        assertThat(text).startsWith("[");
    }

    @Test
    void getAgent_returnsReadResourceResultWithAgentJson() throws Exception {
        var agentService = mock(it.univaq.disim.mosaico.wp2.repository.service.AgentService.class);
        ObjectMapper mapper = new ObjectMapper();
        it.univaq.disim.mosaico.wp2.repository.data.Provider testProvider = new it.univaq.disim.mosaico.wp2.repository.data.Provider("OpenAI", "AI company providing language models", "https://openai.com"
        );
        it.univaq.disim.mosaico.wp2.repository.data.Agent a1 = new it.univaq.disim.mosaico.wp2.repository.data.Agent(
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
            List.of(),
            "Background",
            null, // a2aAgentCardUrl
            null, // deployment
            List.of(), List.of(), List.of(), List.of(), List.of()
        );
        a1.setId("agent1");
        when(agentService.findById("agent1")).thenReturn(java.util.Optional.of(a1));

        AgentMCP agentMCP = new AgentMCP(agentService, mapper, mock(BenchmarkService.class), mock(SkillService.class), mock(BenchmarkRunRepository.class));

        ReadResourceResult res = agentMCP.getAgent("agent1");
        assertThat(res).isNotNull();
        List<ResourceContents> contents = res.contents();
        assertThat(contents).isNotEmpty();
        ResourceContents rc = contents.get(0);
        assertThat(rc).isInstanceOf(TextResourceContents.class);
        String text = ((TextResourceContents) rc).text();
        assertThat(text).contains("agent1");
        assertThat(text).contains("Code Review Agent");
    }
}
