# Integrazione Langfuse - Riepilogo

## ✅ Componenti Implementati

### 1. Infrastructure (Docker)
- **docker-compose.langfuse.yml**: Stack Langfuse v3 con PostgreSQL + ClickHouse + Redis + MinIO + Langfuse worker
- **.env.langfuse**: File di configurazione con credenziali
- **start-langfuse.sh**: Script per avviare lo stack
- **HELP_LANGFUSE.md**: Guida rapida all'uso

### 2. Spring Boot Configuration
- **LangfuseProperties**: Configurazione type-safe
- **application.properties**: Valori di default (disabled)

### 3. Tracing Service
- **LangfuseTracingService**: Client API per tracing
- **LangfuseInterceptor**: Tracing automatico per `/api/agents/**`
- **WebConfig**: Registrazione interceptor (opzionale)

### 4. Project Management Service
- **LangfuseProjectService**: Client API per gestione progetti
- **LangfuseProjectController**: REST endpoints per progetti
- **HELP_LANGFUSE_PROJECTS.md**: Documentazione servizio progetti

## 🚀 Quick Start

### 1. Avvia Langfuse
```bash
./start-langfuse.sh
```

> ℹ️ Langfuse v3 richiede PostgreSQL (configurazione), ClickHouse (analytics), Redis (cache) e uno storage compatibile S3. Lo stack compose avvia automaticamente tutti questi servizi insieme al worker.

### 2. Accedi all'UI
- URL: http://localhost:3000
- Email: `admin@mosaico.local`
- Password: `mosaico2025`

### 3. Abilita il tracing in Spring Boot
```properties
# application.properties
langfuse.enabled=true
langfuse.base-url=http://localhost:3000
langfuse.public-key=pk-mosaico-local
langfuse.secret-key=sk-mosaico-secret
```

### 4. Testa i servizi

**Tracing automatico:**
```bash
# Effettua una chiamata agli agents
curl http://localhost:8080/api/agents

# Verifica la trace in Langfuse UI
# http://localhost:3000/traces
```

**API Progetti:**
```bash
# Health check
curl http://localhost:8080/api/langfuse/projects/health

# Lista progetti
curl http://localhost:8080/api/langfuse/projects

# Dettagli progetto
curl http://localhost:8080/api/langfuse/projects/{projectId}

# Statistiche
curl http://localhost:8080/api/langfuse/projects/{projectId}/stats

# Tracce
curl http://localhost:8080/api/langfuse/projects/{projectId}/traces?limit=50
```

## 📊 Test Results

✅ **100/100 test passati**

```
Tests run: 100, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## 🔧 Comandi Utili

### Docker
```bash
# Avvia stack
./start-langfuse.sh

# Verifica stato
docker ps --filter name=langfuse

# Visualizza log
docker logs langfuse-app

# Ferma stack
docker compose -f docker-compose.langfuse.yml down

# Ferma e rimuovi dati
docker compose -f docker-compose.langfuse.yml down -v
```

### Spring Boot
```bash
# Compila
./mvnw clean compile

# Test
./mvnw test

# Avvia applicazione
./mvnw spring-boot:run
```

## 📁 Struttura File

```
repository/
├── docker-compose.langfuse.yml     # Stack Langfuse v3 (PostgreSQL + ClickHouse + Langfuse)
├── .env.langfuse                   # Configurazione Docker
├── start-langfuse.sh               # Script startup
├── HELP_LANGFUSE.md               # Guida generale
├── HELP_LANGFUSE_PROJECTS.md      # Guida servizio progetti
└── src/main/java/.../
    ├── config/
    │   ├── LangfuseProperties.java       # Configurazione
    │   └── WebConfig.java                # Interceptor registration
    ├── service/
    │   ├── LangfuseTracingService.java   # API tracing
    │   └── LangfuseProjectService.java   # API progetti
    └── controller/
        └── LangfuseProjectController.java # REST endpoints
```

## 🎯 Features Principali

### Tracing Automatico
- ✅ Intercetta automaticamente richieste a `/api/agents/**`
- ✅ Crea trace con ID univoco
- ✅ Log di durata, status code, errori
- ✅ Metadata customizzabili

### Project Management
- ✅ Lista tutti i progetti
- ✅ Dettagli progetto singolo
- ✅ Statistiche di utilizzo
- ✅ Elenco tracce per progetto
- ✅ Health check endpoint

### Optional & Resilient
- ✅ Funziona anche con Langfuse disabilitato
- ✅ Gestione errori graceful
- ✅ Timeout configurabili
- ✅ Log informativi

## 🔐 Credenziali

### Langfuse UI
- Email: `admin@mosaico.local`
- Password: `mosaico2025`

### Worker & Cache
- Redis host: `localhost:6379`
- Password: valore di `REDIS_PASSWORD` (default `changeme`, da personalizzare)

### API Keys
- Public Key: `pk-mosaico-local`
- Secret Key: `sk-mosaico-secret`

### Database PostgreSQL
- User: `langfuse`
- Password: `langfuse`
- Database: `langfuse`
- Port: `5432`

### Storage S3 (MinIO locale)
- Console: http://localhost:9091 (utente `minio`, password `miniosecret` di default)
- Endpoint interno: `http://langfuse-minio:9000`
- Buckets generati: `langfuse-events`, `langfuse-media`, `langfuse-exports`
- Variabili richieste: `MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD`, `LANGFUSE_ENCRYPTION_KEY`

## 📚 Documentazione

- **HELP_LANGFUSE.md**: Setup e configurazione generale
- **HELP_LANGFUSE_PROJECTS.md**: API e esempi d'uso servizio progetti
- Langfuse Docs: https://langfuse.com/docs

## 🐛 Troubleshooting

### Container non si avvia
```bash
# Rimuovi container esistenti
docker rm -f langfuse-app langfuse-postgres

# Riavvia
./start-langfuse.sh
```

### Variabili d'ambiente non caricate
Lo script `start-langfuse.sh` esporta automaticamente le variabili da `.env.langfuse`.

### Errori di connessione
Verifica che Langfuse sia in esecuzione:
```bash
curl http://localhost:3000/api/public/health
```

## 🎉 Conclusione

L'integrazione Langfuse è completa e funzionante:

- ✅ Infrastructure Docker attiva
- ✅ Tracing automatico configurato
- ✅ API progetti implementata
- ✅ Tutti i test passano
- ✅ Documentazione completa
- ✅ Gestione opzionale delle dipendenze

**Prossimi passi:**
1. Abilita `langfuse.enabled=true` in `application.properties`
2. Riavvia l'applicazione Spring Boot
3. Effettua chiamate agli endpoints
4. Monitora le tracce in Langfuse UI
