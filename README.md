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
