#!/bin/bash
set -e

echo "=== Ollama Model Initialization ==="
echo "Pulling nomic-embed-text embedding model..."

ollama pull nomic-embed-text

if [ $? -eq 0 ]; then
  echo "✓ Model installation successful!"
else
  echo "✗ Model installation failed!"
  exit 1
fi

# Verify model was installed
echo "Verifying model installation..."
ollama list | grep nomic-embed-text

echo "=== Initialization Complete ==="
