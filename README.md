## MOSAICO - Langfuse Repository

This repository contains code and tests that interact with a Langfuse instance (self-hosted). This README explains how to start Langfuse, run the application, run unit tests, and run the integration tests that call a live Langfuse instance.

## Prerequisites

- Java 21 (or the JDK configured for the project)
- Maven (the project includes the Maven wrapper `mvnw`)
- Docker & Docker Compose (required to run Langfuse locally via the provided compose file)
- A running Langfuse instance on `http://localhost:3000` (the default used in tests)

If you prefer to use the included scripts, make sure they have execute permission:

```bash
chmod +x start-langfuse.sh run-integration-tests.sh
```

## Start Langfuse (local, self-hosted)

Option A — start with the helper script (recommended):

```bash
./start-langfuse.sh
```

Option B — use Docker Compose directly:

```bash
docker-compose -f docker-compose.langfuse.yml up -d
```

Verify Langfuse is reachable:

```bash
curl -I http://localhost:3000
# or
curl -s -o /dev/null -w "%{http_code}" http://localhost:3000
```

Expected HTTP codes: 200 or 302 depending on the server setup.

## Configure API keys

Integration tests require API keys to be present in `src/main/resources/application.properties` (the `run-integration-tests.sh` script also checks these properties). Add or edit these properties:

```
langfuse.public-key=your_public_key_here
langfuse.secret-key=your_secret_key_here
langfuse.url=http://localhost:3000
```

Note: Tests use Basic Auth constructed from these keys when calling the Langfuse public API.

## Run the application (development)

Build and run using the Maven wrapper:

```bash
./mvnw -DskipTests=true spring-boot:run
```

Or build a jar and run it:

```bash
./mvnw package
java -jar target/*.jar
```

## Run unit tests

To run all unit tests:

```bash
./mvnw test
```

To run a specific unit test class (example):

```bash
./mvnw test -Dtest=LangfuseServiceTest
```

Unit tests are fast and do not require a running Langfuse instance (they use mocks in test resources).

## Run integration tests (real Langfuse calls)

Integration tests in this repository actually call a live Langfuse instance. They are protected by an environment variable guard to avoid accidental runs in environments without Langfuse.

The easiest way to run them is the provided runner script which verifies Langfuse availability and API keys, sets the guard variable, and runs the integration test class:

```bash
./run-integration-tests.sh
```

What the script does:
- Checks `http://localhost:3000` is up. If not, it offers to start Langfuse using `./start-langfuse.sh`.
- Verifies `langfuse.public-key` and `langfuse.secret-key` exist in `src/main/resources/application.properties`.
- Exports `LANGFUSE_INTEGRATION_TEST=true` and runs:
  `./mvnw test -Dtest=LangfuseServiceIntegrationTest`

Manual alternative (without the script):

```bash
export LANGFUSE_INTEGRATION_TEST=true
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

Notes and tips:
- The integration tests expect a Langfuse server at `http://localhost:3000` by default. If your server is elsewhere, update the properties file or the test configuration.
- Some Langfuse endpoints vary by deployment. Tests are defensive and may accept empty results if the public API on your instance does not expose admin endpoints (for example, per-project apiKeys endpoints may return 404 on some Langfuse builds). See `docs/WHY_WEBCLIENT_NOT_SDK.md` and `docs/INTEGRATION_TESTS.md` for more details.

## Troubleshooting

- Langfuse is not running or port unreachable: Start Langfuse via `./start-langfuse.sh` or `docker-compose -f docker-compose.langfuse.yml up -d` and re-run the script.
- API keys not found: Ensure `src/main/resources/application.properties` contains `langfuse.public-key` and `langfuse.secret-key`. The integration runner checks for these.
- Logs: Inspect Docker Compose logs:

```bash
docker-compose -f docker-compose.langfuse.yml logs
```

- If an integration test fails with 404/405, check whether the Langfuse instance exposes the specific public endpoints the tests call. Some endpoints are admin-only and may not be present in public builds.

## CI / Automation

Do not run integration tests in CI unless the pipeline provides a running Langfuse instance and configured API keys. Unit tests are safe to run in CI.

## Additional references

- Integration test runner script: `run-integration-tests.sh`
- Langfuse startup script: `start-langfuse.sh`
- Reasoning for using WebClient vs official SDK: `docs/WHY_WEBCLIENT_NOT_SDK.md`
- Integration test notes: `docs/INTEGRATION_TESTS.md`

## Contact / Next steps

If you need to run integration tests against a remote Langfuse or adjust endpoints, update `src/main/resources/application.properties` or modify the integration tests to point at a different `langfuse.url` and API keys.

Happy testing!

## Configuration: profiles, credentials and local development

We keep a minimal `src/main/resources/application.properties` in the repository with non-sensitive defaults and placeholders. For local development you should use the `dev` profile which contains example (non-production) credentials and convenience settings.

Files involved
- `src/main/resources/application.properties` — canonical repo file; contains placeholders and global settings. Sensitive values are NOT stored here.
- `src/main/resources/application-dev.properties` — local development profile (example values). Activate this profile to load local DB credentials and enable convenient dev settings (e.g. `spring.jpa.hibernate.ddl-auto=update`).

How to run locally using the `dev` profile

1. Use the Maven wrapper (recommended):

```bash
./mvnw -Dspring-boot.run.profiles=dev -DskipTests spring-boot:run
```

2. Or build and run the jar with the profile active:

```bash
./mvnw package
java -jar -Dspring.profiles.active=dev target/*.jar
```

Environment variables alternative

If you prefer not to use the `application-dev.properties` file, you can provide DB credentials through environment variables instead. The application reads the following variables (if set):

- `SPRING_DATASOURCE_URL` (e.g. `jdbc:postgresql://localhost:5432/mosaico_db`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Example (macOS / zsh):

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mosaico_db
export SPRING_DATASOURCE_USERNAME=mosaico
export SPRING_DATASOURCE_PASSWORD=change_me
./mvnw -DskipTests spring-boot:run
```

Security note

- Do NOT commit production credentials into repository files. `application-dev.properties` is intended for local development examples only. For CI and production, prefer using environment variables or secret management (GitHub/GitLab CI secrets, HashiCorp Vault, etc.).

CI / Production notes

- In CI pipelines, inject the required `SPRING_DATASOURCE_*` environment variables or provide a secured properties file via the pipeline's secret mechanism. Avoid committing secrets in the repo.

Flyway note
---------

This project previously included Flyway migrations, but Flyway is disabled by default for the application in this branch because the target database is empty and automatic migrations are not required. If you need to enable Flyway:

- Set the environment variable `SPRING_FLYWAY_ENABLED=true` for the application container, or
- Add `spring.flyway.enabled=true` to the active profile (for example `application-dev.properties`).

If you do enable Flyway, make sure its migration locations do not conflict with any other service sharing the same database (e.g., Langfuse). Alternatively, keep using a dedicated database for this app.

If you want, I can:
- add a small `README` warning header into `application-dev.properties` so it's clear it's for local use only, and/or
- add a `.env.example` file showing the environment variables you should set locally.

Postgres init and JSONB mapping
--------------------------------

This branch includes two conveniences for local development and the Mongo->JPA migration:

- Database init script: `docker/postgres-init/01-create-mosaico-db.sql` is mounted into the Postgres service under `/docker-entrypoint-initdb.d`. On a fresh Postgres initialization (when the volume is empty) the script will create the `mosaico_db` database automatically. If you already have a `langfuse_pg_data` volume, the script won't run — delete the volume to re-run initialization or create the DB manually:

  ```bash
  # manual create (non destructive)
  docker exec -it langfuse-postgres psql -U $POSTGRES_USER -c "CREATE DATABASE mosaico_db;"
  ```

- JSON/JSONB mapping: several entities in the codebase use flexible JSON-like structures (for example `Map<String,Object>` or `List<Map<String,Object>>`). To support persisting these fields in Postgres we added a small Jackson-based JPA `AttributeConverter` located at `src/main/java/.../converter/JsonAttributeConverter.java` and annotated the corresponding fields with `@Convert` and `@Column(columnDefinition = "jsonb")` so Hibernate will persist them as JSONB columns. This keeps the migration fast and avoids creating many small normalized tables for nested, loosely-typed data.

Notes on the JSON mapping:
- The converter stores arbitrary JSON-serialisable objects as JSON in the DB and deserialises them back to Java types (`Map`, `List`, primitives) when read. These values are treated as opaque JSON data by JPA (they are not managed entities).
- In dev the project uses `spring.jpa.hibernate.ddl-auto=update` (see `application-dev.properties`) so the new `jsonb` columns are created automatically when the application starts. In production/CI you should create proper Flyway migrations to add the columns explicitly if you prefer controlled schema management.

If you'd like, I can also:
- make the DB-init script idempotent (check existence before creating), or
- convert more fields from `@Transient` to real relationships/embeddables instead of JSON (recommended for long-term data integrity).
