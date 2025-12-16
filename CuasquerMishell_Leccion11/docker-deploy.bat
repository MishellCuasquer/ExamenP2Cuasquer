@echo off
echo === Despliegue Docker - Sistema de Tickets de Soporte ===

REM 1. Crear red
echo 1. Creando red Docker...
docker network create support-ticket-network

REM 2. Construir imagenes
echo 2. Construyendo imagenes Docker...
echo    Construyendo frontend...
cd Front
docker build -t support-ticket-frontend .

echo    Construyendo backend...
cd ..
docker build -t support-ticket-backend .

REM 3. Iniciar MySQL
echo 3. Iniciando base de datos MySQL...
docker run -d --name mysql-support -p 3307:3306 --network support-ticket-network -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=support_tickets_db -e MYSQL_USER=user -e MYSQL_PASSWORD=password mysql:8.0

REM 4. Iniciar Backend
echo 4. Iniciando backend...
docker run -d --name backend -p 8081:8081 --network support-ticket-network -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql-support:3306/support_tickets_db?useSSL=false&allowPublicKeyRetrieval=true" -e SPRING_DATASOURCE_USERNAME=user -e SPRING_DATASOURCE_PASSWORD=password -e SPRING_JPA_HIBERNATE_DDL_AUTO=update support-ticket-backend

REM 5. Iniciar Frontend
echo 5. Iniciando frontend...
docker run -d --name frontend -p 5500:80 --network support-ticket-network -e REACT_APP_API_URL=http://localhost:8081/api/v1 support-ticket-frontend

REM 6. Verificar todo
echo 6. Verificando contenedores...
docker ps

echo.
echo === Despliegue completado ===
echo Frontend: http://127.0.0.1:5500/
echo Backend: http://localhost:8081/api/v1/support-tickets
echo MySQL: localhost:3307
echo.
echo === Comandos de control ===
echo # Ver logs:
echo docker logs backend
echo docker logs frontend
echo.
echo # Detener todo:
echo docker stop frontend backend mysql-support
echo.
echo # Eliminar todo:
echo docker rm frontend backend mysql-support
echo docker network rm support-ticket-network

pause
