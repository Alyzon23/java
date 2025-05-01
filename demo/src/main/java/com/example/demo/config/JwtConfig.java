package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {
    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);
    
    // Valores literales en lugar de propiedades inyectadas
    private final String secret = "esDY51cgt9a2#3gYn*7xPrQmK$fW@Lz8";
    private final long expiration = 86400000; // 24 horas en milisegundos
    
    public JwtConfig() {
        // Log de seguridad (puedes eliminarlo en producción)
        logger.warn("Usando clave secreta hardcodeada. Considera usar una clave más segura.");
    }
    
    public String getSecret() {
        return secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
}