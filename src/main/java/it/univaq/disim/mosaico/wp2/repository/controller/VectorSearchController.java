package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.Agent;
import it.univaq.disim.mosaico.wp2.repository.service.VectorSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vector/agents")
public class VectorSearchController {

    private final VectorSearchService vectorSearchService;

    public VectorSearchController(VectorSearchService vectorSearchService) {
        this.vectorSearchService = vectorSearchService;
    }

    @PostMapping("/add")
    public ResponseEntity<Agent> addAgent(@RequestBody Agent agent) {
        Agent saved = vectorSearchService.saveAndIndex(agent);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/search")
    public ResponseEntity<List<String>> semanticSearch(@RequestBody SemanticSearchRequest request) {
        String query = request.getQuery();
        int topK = request.getTopK() == null ? 5 : request.getTopK();
        Map<String, Object> filters = request.getFilters() == null ? Map.of() : request.getFilters();

        List<String> results = vectorSearchService.semanticSearch(query, filters, topK);
        return ResponseEntity.ok(results);
    }
}
