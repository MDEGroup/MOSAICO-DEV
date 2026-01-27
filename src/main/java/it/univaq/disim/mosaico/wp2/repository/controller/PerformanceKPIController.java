package it.univaq.disim.mosaico.wp2.repository.controller;

import it.univaq.disim.mosaico.wp2.repository.data.Benchmark;
import it.univaq.disim.mosaico.wp2.repository.data.PerformanceKPI;
import it.univaq.disim.mosaico.wp2.repository.data.KPISpecification;
import it.univaq.disim.mosaico.wp2.repository.repository.BenchmarkRepository;
import it.univaq.disim.mosaico.wp2.repository.repository.PerformanceKPIRepository;
import it.univaq.disim.mosaico.wp2.repository.dsl.KPIFormulaDslService;
import it.univaq.disim.mosaico.wp2.repository.dsl.DslParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for PerformanceKPI operations.
 */
@RestController
@RequestMapping("/api/performance-kpis")
public class PerformanceKPIController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceKPIController.class);

    private final PerformanceKPIRepository performanceKPIRepository;
    private final BenchmarkRepository benchmarkRepository;
    private final KPIFormulaDslService kpiFormulaDslService;

    public PerformanceKPIController(PerformanceKPIRepository performanceKPIRepository,
                                    BenchmarkRepository benchmarkRepository,
                                    KPIFormulaDslService kpiFormulaDslService) {
        this.performanceKPIRepository = performanceKPIRepository;
        this.benchmarkRepository = benchmarkRepository;
        this.kpiFormulaDslService = kpiFormulaDslService;
    }

    @PostMapping
    public ResponseEntity<PerformanceKPI> createKPI(@RequestBody PerformanceKPIRequest request) {
        logger.info("POST /api/performance-kpis for benchmark: {}", request.benchmarkId());

        Optional<Benchmark> benchmark = benchmarkRepository.findById(request.benchmarkId());
        if (benchmark.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PerformanceKPI kpi = new PerformanceKPI();
        kpi.setBenchmark(benchmark.get());
        kpi.setDescription(request.description());

        KPISpecification specification = new KPISpecification();
        specification.setDslText(request.dslText());
        specification.setFormulaType(request.formulaType());
        specification.setDslVersion("1.0");
        kpi.setSpecification(specification);

        PerformanceKPI saved = performanceKPIRepository.save(kpi);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<PerformanceKPI>> getAllKPIs() {
        logger.info("GET /api/performance-kpis");
        List<PerformanceKPI> kpis = performanceKPIRepository.findAll();
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceKPI> getKPIById(@PathVariable String id) {
        logger.info("GET /api/performance-kpis/{}", id);
        Optional<PerformanceKPI> kpi = performanceKPIRepository.findById(id);
        return kpi.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/benchmark/{benchmarkId}")
    public ResponseEntity<List<PerformanceKPI>> getKPIsByBenchmark(@PathVariable String benchmarkId) {
        logger.info("GET /api/performance-kpis/benchmark/{}", benchmarkId);
        List<PerformanceKPI> kpis = performanceKPIRepository.findByBenchmark_Id(benchmarkId);
        return ResponseEntity.ok(kpis);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PerformanceKPI> updateKPI(@PathVariable String id,
                                                     @RequestBody PerformanceKPIRequest request) {
        logger.info("PUT /api/performance-kpis/{}", id);
        Optional<PerformanceKPI> existing = performanceKPIRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Benchmark> benchmark = benchmarkRepository.findById(request.benchmarkId());
        if (benchmark.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        PerformanceKPI kpi = existing.get();
        kpi.setBenchmark(benchmark.get());
        kpi.setDescription(request.description());

        KPISpecification specification = kpi.getSpecification();
        if (specification == null) {
            specification = new KPISpecification();
        }
        specification.setDslText(request.dslText());
        specification.setFormulaType(request.formulaType());
        specification.setDslVersion("1.0");
        kpi.setSpecification(specification);

        PerformanceKPI saved = performanceKPIRepository.save(kpi);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKPI(@PathVariable String id) {
        logger.info("DELETE /api/performance-kpis/{}", id);
        if (!performanceKPIRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        performanceKPIRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<DslValidationResponse> validateDsl(@RequestBody DslValidationRequest request) {
        logger.info("POST /api/performance-kpis/validate");
        DslParseResult result = kpiFormulaDslService.validateFormula(request.dslText());
        return ResponseEntity.ok(new DslValidationResponse(
            result.isSuccess(),
            result.isSuccess() ? "Valid DSL expression" : result.getErrorsAsString(),
            result.getReferencedMetrics()
        ));
    }

    @GetMapping("/dsl/help")
    public ResponseEntity<String> getDslHelp() {
        logger.info("GET /api/performance-kpis/dsl/help");
        return ResponseEntity.ok(kpiFormulaDslService.getDslSyntaxHelp());
    }

    @GetMapping("/dsl/metrics")
    public ResponseEntity<Set<String>> getAvailableMetrics() {
        logger.info("GET /api/performance-kpis/dsl/metrics");
        return ResponseEntity.ok(kpiFormulaDslService.getKnownMetricKeys());
    }

    public record PerformanceKPIRequest(
        String benchmarkId,
        String description,
        String dslText,
        String formulaType
    ) {}

    public record DslValidationRequest(String dslText) {}

    public record DslValidationResponse(
        boolean valid,
        String message,
        Set<String> referencedMetrics
    ) {}
}
