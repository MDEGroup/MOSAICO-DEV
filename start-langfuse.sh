#!/bin/bash
# Script to start Langfuse stack

cd "$(dirname "$0")"

# Export environment variables from .env.langfuse
set -a
source .env.langfuse
set +a

docker compose \
  -f docker-compose.langfuse.yml \
  up -d

echo ""
echo "✅ Langfuse stack starting..."
echo "📊 Web UI: http://localhost:3000"
echo "📧 Email: admin@mosaico.local"
echo "🔑 Password: mosaico2025"
echo ""
echo "Check status with:"
echo "  docker compose -f docker-compose.langfuse.yml ps"
