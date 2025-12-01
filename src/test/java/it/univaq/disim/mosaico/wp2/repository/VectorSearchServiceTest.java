package it.univaq.disim.mosaico.wp2.repository;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.data.Provider;
import it.univaq.disim.mosaico.wp2.repository.data.enums.IOModality;
import it.univaq.disim.mosaico.wp2.repository.repository.AgentRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.ProviderRepository;
import it.univaq.disim.mosaico.wp2.repository.service.VectorSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorSearchServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private ProviderRepository providerRepository;

    @InjectMocks
    private VectorSearchService vectorSearchService;

    private String agentName;
    private String agentDescription;

    @BeforeEach
    void setUp() {
        agentName = "Code Review Agent";
        agentDescription = "Reviews code with semantic search";
    }

    @Test
    void saveAndIndexReusesExistingProvider() {
        Provider existingProvider = new Provider("provider-existing", "Existing", "desc", "contact");
        Provider payloadProvider = new Provider();
        payloadProvider.setId("provider-existing");
        payloadProvider.setName("Incoming");

        Agent agent = buildAgent(payloadProvider);

        when(providerRepository.findById("provider-existing")).thenReturn(Optional.of(existingProvider));
        when(agentRepository.save(any(Agent.class))).thenAnswer(inv -> inv.getArgument(0));

        vectorSearchService.saveAndIndex(agent);

        verify(providerRepository, never()).save(any(Provider.class));

        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(agentCaptor.capture());
        assertSame(existingProvider, agentCaptor.getValue().getProvider());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Document>> docsCaptor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(docsCaptor.capture());
        Document indexedDoc = docsCaptor.getValue().get(0);
        assertTrue(indexedDoc.getContent().contains(agentName));
        assertEquals(agentCaptor.getValue().getId(), indexedDoc.getMetadata().get("entityId"));
    }

    @Test
    void saveAndIndexPersistsProviderWhenMissing() {
        Provider providerWithoutId = new Provider();
        providerWithoutId.setName("New Provider");
        providerWithoutId.setContactUrl("https://provider.example");
        providerWithoutId.setDescription("auto created");

        Agent agent = buildAgent(providerWithoutId);

        Provider persistedProvider = new Provider("generated-id", "New Provider", providerWithoutId.getDescription(), providerWithoutId.getContactUrl());
        when(providerRepository.save(any(Provider.class))).thenReturn(persistedProvider);
        when(agentRepository.save(any(Agent.class))).thenAnswer(inv -> inv.getArgument(0));

        vectorSearchService.saveAndIndex(agent);

        verify(providerRepository).save(any(Provider.class));

        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(agentCaptor.capture());
        assertSame(persistedProvider, agentCaptor.getValue().getProvider());
    }

    @Test
    void saveAndIndexSwallowsVectorStoreFailures() {
        Agent agent = buildAgent(null);

        when(agentRepository.save(any(Agent.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("boom")).when(vectorStore).add(anyList());

        assertDoesNotThrow(() -> vectorSearchService.saveAndIndex(agent));
    }

    @Test
    void semanticSearchReturnsDocumentContents() {
        List<Document> documents = List.of(new Document("Doc A"), new Document("Doc B"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(documents);

        List<String> results = vectorSearchService.semanticSearch("agent", Map.of(), 2);

        assertEquals(List.of("Doc A", "Doc B"), results);
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }

    @Test
    void semanticSearchFiltersByMetadata() {
        Document docA = new Document("Doc A", Map.of("entityType", "Agent", "providerId", "p1"));
        Document docB = new Document("Doc B", Map.of("entityType", "Provider", "providerId", "p1"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(docA, docB));

        Map<String, Object> filters = Map.of("entityType", "Agent");
        List<String> results = vectorSearchService.semanticSearch("agent", filters, 5);

        assertEquals(List.of("Doc A"), results);
    }

    private Agent buildAgent(Provider provider) {
        return new Agent(
                agentName,
                agentDescription,
                "v1",
                provider,
                "MIT",
                "beliefs",
                "intentions",
                "desires",
                "Specialist",
                "Improve quality",
                List.of(IOModality.TEXT),
                "A helpful agent",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
    }
}
