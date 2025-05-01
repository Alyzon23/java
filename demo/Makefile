# Definir variables
SERVICE_NAME=lyxabook
DATABASE_VOLUME=pg_tendencia_data
NETWORK_NAME=biblioteca_net

# Definir el nombre de la imagen
IMAGE_NAME=biblioteca

# Construir la imagen
build-image:
	docker build -f Dockerfile -t $(IMAGE_NAME):latest .

# Desplegar la aplicación
deploy:
	# Crear volumen si no existe
	docker volume create $(DATABASE_VOLUME) || true
	
	# Desplegar el stack
	docker stack deploy --with-registry-auth -c bm.stack.yml $(SERVICE_NAME)
	
	@echo "Esperando a que los servicios se inicien..."
	sleep 10
	@echo "Comprobando el estado de los servicios..."
	docker stack services $(SERVICE_NAME)

# Verificar que la API está funcionando
check-api:
	@echo "Verificando la API..."
	@echo "Nota: Para verificar la API realmente, necesitamos el script check-api.sh"
	@echo "Por ahora, verificamos el estado de los servicios"
	docker stack services $(SERVICE_NAME)

# Limpiar recursos
rm:
	docker stack rm $(SERVICE_NAME)
	docker volume rm $(DATABASE_VOLUME)

# Mostrar logs del servicio API
logs:
	docker service logs $(SERVICE_NAME)_api

# Ver el status de los servicios
status:
	docker stack services $(SERVICE_NAME)

# Reiniciar el servicio API
restart-api:
	docker service update --force $(SERVICE_NAME)_api