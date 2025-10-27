#!/bin/bash

# Script to run Langfuse Integration Tests
# This script ensures Langfuse is running and executes the integration tests

set -e

echo "🚀 Langfuse Integration Test Runner"
echo "===================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if Langfuse is running
echo "📡 Checking if Langfuse is running on http://localhost:3000..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 | grep -q "200\|302"; then
    echo -e "${GREEN}✅ Langfuse is running${NC}"
else
    echo -e "${YELLOW}⚠️  Langfuse is not running on port 3000${NC}"
    echo ""
    echo "Would you like to start Langfuse now? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo "Starting Langfuse with Docker Compose..."
        ./start-langfuse.sh
        echo "Waiting 10 seconds for Langfuse to initialize..."
        sleep 10
    else
        echo -e "${RED}❌ Cannot run integration tests without Langfuse running${NC}"
        echo ""
        echo "To start Langfuse manually, run: ./start-langfuse.sh"
        exit 1
    fi
fi

echo ""
echo "🔑 Checking API keys configuration..."

# Check if API keys are set
if grep -q "langfuse.public-key=" src/main/resources/application.properties && \
   grep -q "langfuse.secret-key=" src/main/resources/application.properties; then
    echo -e "${GREEN}✅ API keys are configured in application.properties${NC}"
else
    echo -e "${RED}❌ API keys not found in application.properties${NC}"
    echo "Please configure langfuse.public-key and langfuse.secret-key"
    exit 1
fi

echo ""
echo "🧪 Running Langfuse Integration Tests..."
echo ""

# Export environment variable to enable integration tests
export LANGFUSE_INTEGRATION_TEST=true

# Run the integration tests
if ./mvnw test -Dtest=LangfuseServiceIntegrationTest; then
    echo ""
    echo -e "${GREEN}✅ All integration tests passed!${NC}"
    echo ""
    echo "📊 Test Summary:"
    echo "  - Service configuration tests"
    echo "  - Project CRUD operations"
    echo "  - API connectivity and authentication"
    echo "  - Error handling and validation"
    echo ""
    exit 0
else
    echo ""
    echo -e "${RED}❌ Integration tests failed${NC}"
    echo ""
    echo "Troubleshooting tips:"
    echo "  1. Check if Langfuse is accessible at http://localhost:3000"
    echo "  2. Verify API keys in application.properties"
    echo "  3. Check Docker logs: docker-compose -f docker-compose.langfuse.yml logs"
    echo "  4. Restart Langfuse: docker-compose -f docker-compose.langfuse.yml restart"
    echo ""
    exit 1
fi
