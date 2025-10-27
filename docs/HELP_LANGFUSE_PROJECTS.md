# Langfuse Project Service

## Overview

Il `LangfuseProjectService` fornisce un'interfaccia Java per interagire con l'API Projects di Langfuse, permettendo di recuperare informazioni sui progetti, statistiche e tracce.

## Componenti

### 1. LangfuseProjectService

Service principale per la gestione dei progetti Langfuse.

**Metodi disponibili:**

- `getProjects()`: Recupera tutti i progetti
- `getProjectById(String projectId)`: Recupera un progetto specifico
- `getProjectStats(String projectId)`: Recupera le statistiche di un progetto
- `getProjectTraces(String projectId, int limit)`: Recupera le tracce di un progetto
- `createProject(String projectName, String description)`: Crea un nuovo progetto

### 2. LangfuseProjectController

Controller REST che espone gli endpoint HTTP.

**Endpoints:**

```http
GET  /api/langfuse/projects
POST /api/langfuse/projects
GET  /api/langfuse/projects/{projectId}
GET  /api/langfuse/projects/{projectId}/stats
GET  /api/langfuse/projects/{projectId}/traces?limit=100
GET  /api/langfuse/projects/health
```

## Configurazione

Il servizio utilizza le stesse configurazioni di `LangfuseTracingService`:

```properties
# application.properties
langfuse.enabled=true
langfuse.base-url=http://localhost:3000
langfuse.public-key=pk-mosaico-local
langfuse.secret-key=sk-mosaico-secret
langfuse.timeout-seconds=5
```

## Esempi d'uso

### 1. Recuperare tutti i progetti

```bash
curl http://localhost:8080/api/langfuse/projects
```

**Risposta:**
```json
[
  {
    "id": "project-123",
    "name": "My Project",
    "createdAt": "2025-10-23T10:00:00Z",
    "members": [...]
  }
]
```

### 2. Recuperare un progetto specifico

```bash
curl http://localhost:8080/api/langfuse/projects/project-123
```

### 3. Recuperare statistiche

```bash
curl http://localhost:8080/api/langfuse/projects/project-123/stats
```

**Risposta:**
```json
{
  "totalTraces": 1234,
  "totalGenerations": 5678,
  "totalCost": 12.34,
  "period": "last-30-days"
}
```

### 4. Recuperare tracce

```bash
curl http://localhost:8080/api/langfuse/projects/project-123/traces?limit=10
```

**Risposta:**
```json
[
  {
    "id": "trace-abc",
    "name": "GET /api/agents",
    "timestamp": "2025-10-23T11:00:00Z",
    "metadata": {...},
    "duration": 123
  }
]
```

### 5. Health check

```bash
curl http://localhost:8080/api/langfuse/projects/health
```

**Risposta:**
```json
{
  "enabled": true,
  "status": "connected"
}
```

## Uso programmatico

### Esempio in un Service

```java
@Service
public class MyAnalyticsService {
    
    private final LangfuseProjectService langfuseProjectService;
    
    public MyAnalyticsService(LangfuseProjectService langfuseProjectService) {
        this.langfuseProjectService = langfuseProjectService;
    }
    
    public void analyzeProjects() {
        if (!langfuseProjectService.isEnabled()) {
            logger.warn("Langfuse is not enabled");
            return;
        }
        
        // Recupera tutti i progetti
        List<Map<String, Object>> projects = langfuseProjectService.getProjects();
        
        for (Map<String, Object> project : projects) {
            String projectId = (String) project.get("id");
            
            // Recupera le statistiche
            Map<String, Object> stats = langfuseProjectService.getProjectStats(projectId);
            
            // Recupera le ultime 50 tracce
            List<Map<String, Object>> traces = 
                langfuseProjectService.getProjectTraces(projectId, 50);
            
            // Analizza i dati...
        }
    }
}
```

## Note

- Il servizio è **opzionale**: se Langfuse non è configurato (`langfuse.enabled=false`), tutti i metodi ritornano valori vuoti o null senza errori
- Le chiamate API sono **sincrone** e bloccanti (usano `.block()`)
- Il timeout è configurabile tramite `langfuse.timeout-seconds`
- Il controller è attivo solo se `LangfuseProjectService` è disponibile (grazie a `@ConditionalOnBean`)

## Gestione errori

Il servizio gestisce automaticamente gli errori:

- **Langfuse non configurato**: ritorna liste vuote o null
- **Timeout**: log di warning e ritorno di valori di default
- **Errori API**: log di errore e ritorno di valori di default
- **404 Not Found**: ritorna null per metodi singoli, lista vuota per liste

## Testing

Il servizio è testato insieme agli altri componenti. I test passano anche quando Langfuse è disabilitato grazie alla gestione opzionale delle dipendenze.

```bash
./mvnw test
```

## Integrazione con Langfuse UI

Per visualizzare i dati nell'interfaccia web di Langfuse:

1. Avvia Langfuse: `./start-langfuse.sh`
2. Accedi a http://localhost:3000
3. Login con: `admin@mosaico.local` / `mosaico2025`
4. Visualizza progetti, tracce e metriche nell'UI

## Prossimi passi

Possibili estensioni:

- Aggiungere endpoint per creare/aggiornare progetti
- Implementare filtri avanzati per le tracce
- Aggiungere cache per ridurre le chiamate API
- Implementare pagination per grandi dataset
- Aggiungere metriche custom (costo, latenza, errori)
