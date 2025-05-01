#!/bin/bash

# Cargar variables de entorno
source .env

# URL base de la API
API_URL="https://${SUBDOMAIN}.${DOMAIN}"

# Colores para la salida
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Verificando la API de LyxaBook en ${API_URL}${NC}"
echo "-----------------------------------"

# Función para verificar un endpoint
check_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    
    echo -e "Verificando ${YELLOW}$description${NC} ($method $endpoint)"
    
    if [ "$method" == "GET" ]; then
        response=$(curl -s -o /dev/null -w "%{http_code}" -X $method "${API_URL}${endpoint}" -H "Content-Type: application/json")
    else
        response=$(curl -s -o /dev/null -w "%{http_code}" -X $method "${API_URL}${endpoint}" -H "Content-Type: application/json" -d "$data")
    fi
    
    if [[ $response -ge 200 && $response -lt 400 ]]; then
        echo -e "  ${GREEN}✓ OK ($response)${NC}"
    else
        echo -e "  ${RED}✗ ERROR ($response)${NC}"
    fi
}

# Verificar que el servidor está activo
echo -e "\n${YELLOW}Verificando que el servidor está activo:${NC}"
if curl -s --head "${API_URL}/actuator/health" | grep "200 OK" > /dev/null; then
    echo -e "  ${GREEN}✓ El servidor está activo${NC}"
else
    echo -e "  ${RED}✗ No se puede conectar al servidor${NC}"
    echo "  Posibles problemas:"
    echo "  - Verifica que el dominio apunta a la IP correcta"
    echo "  - Asegúrate de que el servidor está en ejecución"
    echo "  - Comprueba los logs con 'make logs'"
    exit 1
fi

# Verificar endpoints específicos
echo -e "\n${YELLOW}Verificando endpoints específicos:${NC}"

# Login (POST)
check_endpoint "POST" "/api/auth/login" "Endpoint de login" '{"username":"test@example.com","password":"test123"}'

# Añade más endpoints según tu API
# check_endpoint "GET" "/api/books" "Listado de libros"
# check_endpoint "POST" "/api/books" "Creación de libro" '{"title":"Nuevo Libro","author":"Autor Test"}'
# check_endpoint "PUT" "/api/books/1" "Actualización de libro" '{"title":"Libro Actualizado"}'
# check_endpoint "DELETE" "/api/books/1" "Eliminación de libro"

echo -e "\n${YELLOW}Verificación completa${NC}"
echo "Para más detalles, revisa los logs con 'make logs'"