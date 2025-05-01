# Crear Makefile
@"
IMAGE_NAME=spring-boot-docker
build:
    docker build -t $(IMAGE_NAME) .
run:
    docker run -d -p 8080:8080 --name spring-boot-container $(IMAGE_NAME)
clean:
    docker stop spring-boot-container && docker rm spring-boot-container
"@ > Makefile

# Crear bm.stack
@"
SERVICE=service-name
"@ > bm.stack

# Crear ip_bm_stack
@"
IP=192.168.0.100
PORT=8080
"@ > ip_bm_stack

# Crear stack script
@"
#!/bin/bash
echo "Iniciando aplicación Spring Boot"
docker run -d -p 8080:8080 --name spring-boot-container spring-boot-docker
"@ > stack

Write-Host "Archivos restantes creados exitosamente."
