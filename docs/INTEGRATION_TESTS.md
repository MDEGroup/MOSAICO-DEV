# Langfuse Integration Tests

This document describes the integration tests for the Langfuse API connector.

## Overview

The `LangfuseServiceIntegrationTest` class contains real integration tests that actually invoke the Langfuse API, unlike the unit tests in `LangfuseServiceTest` which use mocks.

## ⚠️ Important: Langfuse Public API Limitations

The Langfuse Public API has some limitations that affect these integration tests:

- ✅ **Supported:** List all projects (`GET /api/public/projects`)
- ❌ **NOT Supported:** Get project by ID (`GET /api/public/projects/{id}`) - Returns 404
- ❌ **NOT Supported:** Create project (`POST /api/public/projects`) - Returns 405 Method Not Allowed
- ❌ **NOT Supported:** Get project statistics - Endpoint not available
- ❌ **NOT Supported:** Get project traces - Endpoint not available

**Projects must be created manually via the Langfuse UI** at http://localhost:3000

The integration tests are designed to handle these limitations gracefully and will report API availability status.

## Test Coverage

The integration tests verify:

1. **Service Configuration**
   - Service is properly configured and enabled
   - API keys are valid
   - Base URL is correct

2. **Project Management (Limited by API)**
   - ✅ List all projects (`getProjects()`)
   - ⚠️  Get project by ID - Tests API limitation (404 expected)
   - ⚠️  Create projects - Verifies 405 Method Not Allowed (expected)
   - ⚠️  Get project statistics - Tests API availability
   - ⚠️  Get project traces - Tests API availability

3. **Error Handling**
   - Null/empty project name validation
   - Invalid project ID handling
   - Network error resilience
   - API limitation handling (404, 405 responses)

4. **API Limitations Testing**
   - Confirms which endpoints are not supported
   - Gracefully handles API errors
   - Provides informative messages about API capabilities

## Prerequisites

Before running integration tests, ensure:

1. **Langfuse Server Running**
   ```bash
   ./start-langfuse.sh
   ```
   Langfuse should be accessible at `http://localhost:3000`

2. **At Least One Project Created**
   
   Since the API doesn't support project creation, you must create projects manually:
   - Open http://localhost:3000 in your browser
   - Login to Langfuse
   - Create at least one project via the UI
   - Note: Tests will skip some checks if no projects exist

3. **API Keys Configured**
   
   Check `src/main/resources/application.properties`:
   ```properties
   langfuse.enabled=true
   langfuse.base-url=http://localhost:3000
   langfuse.public-key=pk-lf-72af177c-f55e-46f5-a617-0619b95f36da
   langfuse.secret-key=sk-lf-9cae9864-b2d6-4a06-9b49-96af21624361
   ```

4. **Network Connectivity**
   - Port 3000 must be accessible
   - No firewall blocking localhost connections

## Running Integration Tests

### Option 1: Using the Helper Script (Recommended)

```bash
./run-integration-tests.sh
```

This script will:
- Check if Langfuse is running
- Offer to start it if not running
- Verify API key configuration
- Run all integration tests
- Provide detailed feedback

### Option 2: Manual Execution

```bash
# Set environment variable to enable integration tests
export LANGFUSE_INTEGRATION_TEST=true

# Run the tests
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

### Option 3: Run Specific Test

```bash
export LANGFUSE_INTEGRATION_TEST=true
./mvnw test -Dtest=LangfuseServiceIntegrationTest#testCreateAndRetrieveProject
```

## Test Design

### Why @EnabledIfEnvironmentVariable?

Integration tests are disabled by default and only run when `LANGFUSE_INTEGRATION_TEST=true` is set. This prevents:

- Accidental execution during regular unit test runs
- CI/CD failures when Langfuse is not available
- Unnecessary API calls during development

### Test Isolation

Each test method:
- Is independent and can run in any order
- Creates unique test data (using UUID)
- Does not rely on specific existing data
- Cleans up implicitly (Langfuse manages data lifecycle)

### Test Data Naming Convention

All test-created projects use descriptive names:
- `Integration Test Project <uuid>` - General integration tests
- `Stats Test Project <uuid>` - Statistics testing
- `Traces Test Project <uuid>` - Traces testing
- `Batch Test <uuid> - N` - Batch creation tests

This makes it easy to identify and clean up test data in the Langfuse UI.

## Expected Test Results

When all tests pass, you should see:

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

Each test will also print informative messages:
```
Found 1 projects in Langfuse
First project: mosaico-test (id: cmh38krpn000611bkp7219qv4)
✓ Confirmed: Project creation not supported via API (as expected)
ℹ️  Projects must be created manually in Langfuse UI
ℹ️  GET /api/public/projects/{id} not supported by Langfuse Public API (404)
API limitation confirmed
```

## Troubleshooting

### Tests are Skipped

**Problem:** All tests show as "skipped"

**Solution:**
```bash
export LANGFUSE_INTEGRATION_TEST=true
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

### Connection Refused

**Problem:** `Connection refused: localhost/127.0.0.1:3000`

**Solutions:**
1. Start Langfuse: `./start-langfuse.sh`
2. Check Docker status: `docker-compose -f docker-compose.langfuse.yml ps`
3. Verify port 3000 is not in use: `lsof -i :3000`

### Unauthorized (401 Error)

**Problem:** API returns 401 Unauthorized

**Solutions:**
1. Verify API keys in `application.properties`
2. Check keys match Langfuse project settings
3. Generate new API keys in Langfuse UI if needed

### Timeout Errors

**Problem:** Tests timeout waiting for response

**Solutions:**
1. Increase timeout in `application.properties`:
   ```properties
   langfuse.timeout-seconds=30
   ```
2. Check Langfuse server logs for errors:
   ```bash
   docker-compose -f docker-compose.langfuse.yml logs langfuse
   ```

### Tests Fail Intermittently

**Problem:** Random test failures

**Solutions:**
1. Langfuse may be under load - increase timeout
2. Check database connection:
   ```bash
   docker-compose -f docker-compose.langfuse.yml logs db
   ```
3. Restart Langfuse stack:
   ```bash
   docker-compose -f docker-compose.langfuse.yml restart
   ```

## Viewing Test Data in Langfuse UI

1. Open Langfuse UI: http://localhost:3000
2. Login with default credentials (check your Langfuse setup)
3. Navigate to Projects section
4. Look for projects with test names (containing "Test Project")
5. You can manually delete test projects if needed

## CI/CD Integration

To run integration tests in CI/CD pipeline:

```yaml
# Example GitHub Actions workflow
- name: Start Langfuse
  run: docker-compose -f docker-compose.langfuse.yml up -d
  
- name: Wait for Langfuse
  run: |
    timeout 60 bash -c 'until curl -s http://localhost:3000; do sleep 2; done'
  
- name: Run Integration Tests
  env:
    LANGFUSE_INTEGRATION_TEST: true
  run: ./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

## Comparison: Unit Tests vs Integration Tests

| Aspect | Unit Tests | Integration Tests |
|--------|-----------|-------------------|
| **File** | `LangfuseServiceTest.java` | `LangfuseServiceIntegrationTest.java` |
| **Dependencies** | Mocked | Real Langfuse server |
| **Speed** | Fast (~1s) | Slower (~3-10s) |
| **Reliability** | Always pass | Depends on server |
| **Purpose** | Test logic | Test API connectivity & limitations |
| **When to run** | Every build | Pre-deployment |
| **Test count** | 14 tests | 12 tests |
| **API calls** | None (mocked) | Real HTTP calls to Langfuse |

## Best Practices

1. **Always run unit tests first** - They're faster and catch logic errors
2. **Run integration tests before deployment** - Verify real connectivity
3. **Don't commit test data** - Integration tests create data automatically
4. **Monitor test execution time** - Slow tests may indicate server issues
5. **Check logs on failure** - Both application and Langfuse logs provide context

## Maintenance

### Updating Tests

When adding new LangfuseService methods:

1. Add unit test in `LangfuseServiceTest.java` (with mocks)
2. Add integration test in `LangfuseServiceIntegrationTest.java` (real calls)
3. Update this README with new test coverage
4. Run both test suites to verify

### Cleaning Up Test Data

Test data is automatically created with unique names. To clean up:

```bash
# Option 1: Via Langfuse UI
# Navigate to Projects → Delete test projects manually

# Option 2: Keep test data for debugging
# Test projects don't interfere with production data
```

## Support

For issues or questions:

1. Check Langfuse logs: `docker-compose -f docker-compose.langfuse.yml logs`
2. Review application logs: `tail -f logs/app.log`
3. Consult `HELP_LANGFUSE_PROJECTS.md` for API documentation
4. Check Langfuse documentation: https://langfuse.com/docs
