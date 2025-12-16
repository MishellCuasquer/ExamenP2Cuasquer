#!/bin/bash

# Script para construir y publicar imagen en Docker Hub
# Support Tickets API

set -e

# Variables
IMAGE_NAME="mishellcuasquer/support-tickets-api"
TAG="latest"
FULL_IMAGE_NAME="${IMAGE_NAME}:${TAG}"

echo "=== Construyendo y publicando Support Tickets API ==="
echo "Imagen: ${FULL_IMAGE_NAME}"
echo ""

# Paso 1: Construir la imagen localmente
echo "1. Construyendo imagen Docker localmente..."
docker build -t ${FULL_IMAGE_NAME} .

# Paso 2: Verificar la imagen
echo ""
echo "2. Verificando imagen construida..."
docker images | grep ${IMAGE_NAME}

# Paso 3: Login en Docker Hub (descomentar si no estás logueado)
echo ""
echo "3. Haciendo login en Docker Hub..."
# docker login

# Paso 4: Publicar la imagen
echo ""
echo "4. Publicando imagen en Docker Hub..."
docker push ${FULL_IMAGE_NAME}

# Paso 5: Verificar publicación
echo ""
echo "5. Verificando publicación..."
docker pull ${FULL_IMAGE_NAME}

echo ""
echo "=== Proceso completado exitosamente ==="
echo "Imagen disponible: ${FULL_IMAGE_NAME}"
echo ""
echo "Para ejecutar con docker-compose:"
echo "docker-compose up -d"
echo ""
echo "Para ejecutar solo el contenedor:"
echo "docker run -d -p 3001:3001 --name support-tickets-api ${FULL_IMAGE_NAME}"
