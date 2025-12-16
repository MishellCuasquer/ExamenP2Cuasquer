# Support Tickets API

API REST para gestión de tickets de soporte con filtros avanzados, paginación y ordenamiento.

## Requisitos del Entorno

- **Java**: 17 o superior
- **Maven**: 3.8+
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **MySQL**: 8.0+ (si se ejecuta sin Docker)

## Construcción de Imagen Docker

### Opción 1: Construcción Local
```bash
# Clonar el repositorio
git clone <repository-url>
cd CuasquerMishell_Leccion1

# Construir la imagen Docker
docker build -t support-tickets-api:latest .

# O construir con Maven wrapper y Docker
./mvnw clean package
docker build -t support-tickets-api:latest .
```

### Opción 2: Desde Docker Hub
```bash
# Descargar imagen desde Docker Hub
docker pull mishellcuasquer/support-tickets-api:latest
```

## Comandos para Ejecutar Contenedor

### Opción 1: Con Docker Compose (Recomendado)
```bash
# Iniciar servicios (API + Base de Datos)
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Eliminar volúmenes (cuidado: borra datos)
docker-compose down -v
```

### Opción 2: Solo el Contenedor API
```bash
# Ejecutar contenedor (necesita MySQL externo)
docker run -d \
  --name support-tickets-api \
  -p 3001:3001 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/support_tickets_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=tu_password \
  support-tickets-api:latest
```

## URL Base y Ejemplos de Consumo

### URL Base
```
http://localhost:3001/api/v1/support-tickets
```

### Endpoints Disponibles

#### 1. Crear Ticket
```bash
POST http://localhost:3001/api/v1/support-tickets
Content-Type: application/json

{
  "requesterName": "Juan Chicaiza",
  "status": "OPEN",
  "priority": "HIGH",
  "category": "Soporte Técnico",
  "estimatedCost": 150.50,
  "currency": "USD",
  "dueDate": "2025-12-20"
}
```

#### 2. Listar Tickets con Filtros

**Búsqueda de texto:**
```bash
GET http://localhost:3001/api/v1/support-tickets?q=chicaiza
```

**Por estado:**
```bash
GET http://localhost:3001/api/v1/support-tickets?status=OPEN
```

**Por moneda:**
```bash
GET http://localhost:3001/api/v1/support-tickets?currency=USD
```

**Por rango de costos:**
```bash
GET http://localhost:3001/api/v1/support-tickets?minCost=50&maxCost=300
```

**Por rango de fechas:**
```bash
GET http://localhost:3001/api/v1/support-tickets?from=2025-01-01T00:00:00&to=2025-06-30T23:59:59
```

**Combinación de filtros con paginación:**
```bash
GET http://localhost:3001/api/v1/support-tickets?q=chicaiza&status=OPEN&currency=USD&page=0&size=5&sort=createdAt,desc
```

### Filtros Disponibles

| Parámetro | Tipo | Descripción | Valores |
|-----------|------|-------------|---------|
| `q` | String | Búsqueda texto en ticketNumber y requesterName | Case-insensitive |
| `status` | String | Estado del ticket | OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED |
| `currency` | String | Moneda | USD, EUR |
| `minCost` | BigDecimal | Costo mínimo | >= 0 |
| `maxCost` | BigDecimal | Costo máximo | >= 0 |
| `from` | LocalDateTime | Fecha inicial | ISO-8601 |
| `to` | LocalDateTime | Fecha final | ISO-8601 |
| `page` | Integer | Número de página | Default: 0 |
| `size` | Integer | Tamaño página | Default: 10 |
| `sort` | String | Ordenación | campo,dirección |

## Docker Hub

### Imagen Publicada
- **Repositorio**: `mishellcuasquer/support-tickets-api`
- **Tags Disponibles**:
  - `latest`: Última versión estable
  - `v1.0`: Versión 1.0

### Descarga y Ejecución Rápida
```bash
# Descargar imagen
docker pull mishellcuasquer/support-tickets-api:latest

# Ejecutar con docker-compose
curl -O https://raw.githubusercontent.com/<repository>/docker-compose.yml
docker-compose up -d
```

## Variables de Entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://mysql:3306/support_tickets_db` | URL de base de datos |
| `SPRING_DATASOURCE_USERNAME` | `root` | Usuario BD |
| `SPRING_DATASOURCE_PASSWORD` | `rootpassword` | Password BD |
| `SERVER_PORT` | `3001` | Puerto del servidor |
| `SPRING_PROFILES_ACTIVE` | `docker` | Perfil de Spring |

## Arquitectura

- **Backend**: Spring Boot 4.0 con Java 17
- **Base de Datos**: MySQL 8.0
- **ORM**: Spring Data JPA con Hibernate
- **Validación**: Bean Validation (Jakarta)
- **Contenedorización**: Docker + Docker Compose

## Desarrollo

### Ejecución Local sin Docker
```bash
# Iniciar MySQL localmente
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS support_tickets_db;"

# Ejecutar aplicación
./mvnw spring-boot:run
```

### Construcción y Tests
```bash
# Compilar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Construir JAR
./mvnw clean package
```

## Monitoreo y Logs

### Ver Logs del Contenedor
```bash
# Logs en tiempo real
docker-compose logs -f support-tickets-api

# Logs de la base de datos
docker-compose logs -f mysql
```

### Health Check
```bash
# Verificar estado del servicio
curl http://localhost:3001/actuator/health
```

## Troubleshooting

### Problemas Comunes

1. **Error de conexión a BD**: Verificar que MySQL esté iniciado
2. **Puerto en uso**: Cambiar puerto en docker-compose.yml
3. **Permisos**: Ejecutar con sudo si es necesario en Linux

### Comandos Útiles
```bash
# Ver contenedores en ejecución
docker ps

# Reiniciar servicios
docker-compose restart

# Entrar al contenedor
docker exec -it support-tickets-api bash
```

## Licencia

MIT License - 2024
