package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status);
        errorDetails.put("message", message);
        
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        try {
            // Logging detallado de la solicitud
            logger.debug("Request URL: {}", request.getRequestURL());
            logger.debug("Request Method: {}", request.getMethod());
            
            final String requestTokenHeader = request.getHeader("Authorization");
            logger.debug("Authorization Header: {}", requestTokenHeader);

            String username = null;
            String jwtToken = null;

            // Validar y extraer token
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);
                
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    logger.info("Username extraído del token: {}", username);
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token ha expirado para usuario: {}", e.getClaims().getSubject());
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
                    return;
                } catch (SignatureException e) {
                    logger.error("Firma de JWT inválida");
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Firma de token inválida");
                    return;
                } catch (JwtException e) {
                    logger.error("Error de procesamiento de JWT", e);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Error de token");
                    return;
                }
            } else {
                logger.warn("No se encontró token JWT en el encabezado de autorización");
            }

            // Validar usuario y establecer autenticación
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validar token
                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        logger.info("Token JWT validado exitosamente para: {}", username);
                    } else {
                        logger.warn("Validación de token fallida para: {}", username);
                    }
                } catch (UsernameNotFoundException e) {
                    logger.error("Usuario no encontrado: {}", username);
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
                    return;
                }
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Error inesperado en filtro de autenticación", e);
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno de autenticación");
        }
    }
}