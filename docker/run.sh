#!/bin/bash

set -e

echo "Building application..."
cd ..
mvn clean package -DskipTests

echo "Building Docker image..."
docker build -t coupon-api:latest -f docker/Dockerfile .

echo "Starting containers..."
cd docker
docker-compose up -d

echo ""
echo "Waiting for application to be ready..."

# Aguarda até a aplicação estar saudável
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "Application is ready!"
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo "Waiting... ($ATTEMPT/$MAX_ATTEMPTS)"
    sleep 2
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    echo "Warning: Application took too long to start"
    echo "Check logs with: docker-compose logs -f"
else
    echo ""
    echo "Opening Swagger UI..."

    # Detecta o sistema operacional e abre o navegador
    if command -v xdg-open > /dev/null; then
        xdg-open http://localhost:8080/swagger-ui.html
    elif command -v open > /dev/null; then
        open http://localhost:8080/swagger-ui.html
    elif command -v start > /dev/null; then
        start http://localhost:8080/swagger-ui.html
    elif [ -n "$BROWSER" ]; then
        $BROWSER http://localhost:8080/swagger-ui.html
    else
        echo "Could not detect browser. Please open manually:"
        echo "http://localhost:8080/swagger-ui.html"
    fi
fi

echo ""
echo "Application URLs:"
echo "  Swagger: http://localhost:8080/swagger-ui.html"
echo "  API: http://localhost:8080"
echo "  H2 Console: http://localhost:8080/h2-console"
echo ""
echo "Run 'docker-compose logs -f' to see logs"
