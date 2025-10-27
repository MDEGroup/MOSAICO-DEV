# Quick Start: Running Langfuse Integration Tests

## Prerequisites

1. **Start Langfuse**
   ```bash
   ./start-langfuse.sh
   ```
   Wait for the message: "Langfuse server is ready at http://localhost:3000"

2. **Create a Project in Langfuse UI**
   - Open http://localhost:3000 in your browser
   - Login to Langfuse (check Langfuse docs for default credentials)
   - Create at least one project via the UI
   - **Important:** The Langfuse Public API does NOT support project creation, so this manual step is required

3. **Verify Langfuse is running**
   ```bash
   curl http://localhost:3000
   ```
   Should return HTML content (not connection error)

## Run Integration Tests

### Easy Way (Recommended)

```bash
./run-integration-tests.sh
```

This script will:
- ✅ Check if Langfuse is running
- ✅ Validate API configuration
- ✅ Run all 13 integration tests
- ✅ Show detailed results

### Manual Way

```bash
# Enable integration tests
export LANGFUSE_INTEGRATION_TEST=true

# Run all integration tests
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

### Run Specific Test

```bash
export LANGFUSE_INTEGRATION_TEST=true

# Test project creation
./mvnw test -Dtest=LangfuseServiceIntegrationTest#testCreateAndRetrieveProject

# Test project listing
./mvnw test -Dtest=LangfuseServiceIntegrationTest#testGetProjectsReturnsValidResponse

# Test error handling
./mvnw test -Dtest=LangfuseServiceIntegrationTest#testCreateProjectWithNullNameShouldFail
```

## What Gets Tested

✅ **12 Integration Tests:**

1. `testServiceIsConfiguredAndEnabled` - Verify configuration
2. `testGetProjectsReturnsValidResponse` - List all projects (✅ API supported)
3. `testCreateProjectReturns405MethodNotAllowed` - Verify API limitation
4. `testGetProjectByIdWithValidId` - Test API limitation (404 expected)
5. `testGetProjectByIdWithInvalidId` - Error handling
6. `testGetProjectStats` - Test stats endpoint availability
7. `testGetProjectTraces` - Test traces endpoint availability
8. `testCreateProjectWithNullNameShouldFail` - Validation
9. `testCreateProjectWithEmptyNameShouldFail` - Validation
10. `testCreateProjectWithBlankNameShouldFail` - Validation
11. `testCreateProjectWithNullDescription` - API limitation test
12. `testMultipleProjectsRetrieval` - List and verify projects

### API Limitations Tested

These tests confirm that certain operations are **NOT supported** by Langfuse Public API:
- ❌ Create project (405 Method Not Allowed)
- ❌ Get project by ID (404 Not Found)
- ❌ Get project statistics (endpoint may not exist)
- ❌ Get project traces (endpoint may not exist)

## Expected Output

```
🚀 Langfuse Integration Test Runner
====================================

📡 Checking if Langfuse is running on http://localhost:3000...
✅ Langfuse is running

🔑 Checking API keys configuration...
✅ API keys are configured in application.properties

🧪 Running Langfuse Integration Tests...

[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running it.univaq.disim.mosaico.wp2.repository.LangfuseServiceIntegrationTest

Found 1 projects in Langfuse
First project: mosaico-test (id: cmh38krpn000611bkp7219qv4)
✓ Confirmed: Project creation not supported via API (as expected)
ℹ️  Projects must be created manually in Langfuse UI
ℹ️  GET /api/public/projects/{id} not supported by Langfuse Public API (404)
API limitation confirmed
Correctly returned null for invalid project ID
...

[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

✅ All integration tests passed!
```

## Troubleshooting

### Tests are Skipped

**Problem:**
```
Tests run: 0, Failures: 0, Errors: 0, Skipped: 13
```

**Solution:**
```bash
# Make sure to export the environment variable
export LANGFUSE_INTEGRATION_TEST=true
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

### Connection Refused

**Problem:**
```
Connection refused: localhost/127.0.0.1:3000
```

**Solution:**
```bash
# Start Langfuse
./start-langfuse.sh

# Wait 10 seconds for initialization
sleep 10

# Verify it's running
curl http://localhost:3000
```

### Unauthorized Error

**Problem:**
```
401 Unauthorized
```

**Solution:**
Check API keys in `src/main/resources/application.properties`:
```properties
langfuse.public-key=pk-lf-72af177c-f55e-46f5-a617-0619b95f36da
langfuse.secret-key=sk-lf-9cae9864-b2d6-4a06-9b49-96af21624361
```

## Viewing Results in Langfuse UI

1. Open http://localhost:3000 in your browser
2. Login (check Langfuse documentation for default credentials)
3. Go to **Projects** section
4. Look for test projects (names contain "Test Project" + UUID)

## Cleanup Test Data

Test projects are created with unique names like:
- `Integration Test Project a1b2c3d4`
- `Stats Test Project e5f6g7h8`
- `Batch Test i9j0k1l2 - 1`

You can delete them manually from Langfuse UI if needed.

## Next Steps

After successful integration tests:

1. **Verify in Langfuse UI** - Check that projects were created
2. **Test tracing** - Make API calls to `/api/agents/*` endpoints
3. **Check traces** - View traces in Langfuse UI
4. **Monitor performance** - Review stats and metrics

## Documentation

- `INTEGRATION_TESTS.md` - Full documentation
- `HELP_LANGFUSE_PROJECTS.md` - API endpoint reference
- `LANGFUSE_INTEGRATION_SUMMARY.md` - Integration overview

## Comparison with Unit Tests

| Test Type | File | Tests | Speed | Purpose |
|-----------|------|-------|-------|---------|
| **Unit** | `LangfuseServiceTest.java` | 14 | Fast (1s) | Test logic with mocks |
| **Integration** | `LangfuseServiceIntegrationTest.java` | 12 | Medium (3-10s) | Test real API calls & limitations |

Run both for complete coverage:
```bash
# Unit tests (fast)
./mvnw test -Dtest=LangfuseServiceTest

# Integration tests (requires Langfuse running + projects created)
export LANGFUSE_INTEGRATION_TEST=true
./mvnw test -Dtest=LangfuseServiceIntegrationTest
```

## Important Notes

### Langfuse Public API Limitations

The integration tests discovered that the Langfuse Public API has limited functionality:

- ✅ **Supported:** `GET /api/public/projects` - List all projects
- ❌ **NOT Supported:** `POST /api/public/projects` - Create project (405 Method Not Allowed)
- ❌ **NOT Supported:** `GET /api/public/projects/{id}` - Get project by ID (404 Not Found)
- ❌ **NOT Supported:** Project statistics endpoint
- ❌ **NOT Supported:** Project traces endpoint

**Conclusion:** Projects must be created manually via Langfuse UI. The API is primarily for listing projects and sending observability data (traces, spans, events).
