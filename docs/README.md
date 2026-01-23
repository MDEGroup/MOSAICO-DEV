# MOSAICO Documentation

Questa directory contiene la documentazione del sistema di benchmarking MOSAICO.

## Documenti

| Documento | Descrizione |
|-----------|-------------|
| [HANDOVER_BENCHMARKING_SYSTEM.md](HANDOVER_BENCHMARKING_SYSTEM.md) | Documento di handover completo del sistema di benchmarking |

## Diagrammi PlantUML

I diagrammi sono nella directory `diagrams/` e possono essere visualizzati con:
- [PlantUML Online Server](https://www.plantuml.com/plantuml/uml)
- Plugin VS Code "PlantUML"
- IntelliJ IDEA con plugin PlantUML

| Diagramma | Descrizione |
|-----------|-------------|
| [benchmarking-architecture.puml](diagrams/benchmarking-architecture.puml) | Architettura completa del sistema |
| [benchmark-run-sequence.puml](diagrams/benchmark-run-sequence.puml) | Sequence diagram esecuzione benchmark |
| [scheduling-flow.puml](diagrams/scheduling-flow.puml) | Flusso scheduling automatico |
| [entity-relationships.puml](diagrams/entity-relationships.puml) | ER Diagram delle entità |
| [kpi-dsl-flow.puml](diagrams/kpi-dsl-flow.puml) | Flusso parsing formule KPI |
| [run-state-machine.puml](diagrams/run-state-machine.puml) | State machine BenchmarkRun |

## Generazione Diagrammi PNG

```bash
# Con PlantUML installato
plantuml diagrams/*.puml

# Con Docker
docker run -v $(pwd)/diagrams:/data plantuml/plantuml *.puml
```

## Demo Interattiva

Esegui la demo del sistema:

```bash
mvn compile exec:java \
    -Dexec.mainClass="it.univaq.disim.mosaico.wp2.repository.demo.BenchmarkingSystemDemo" \
    -Dexec.classpathScope=test
```
