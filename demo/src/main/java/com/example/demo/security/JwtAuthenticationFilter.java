package com.example.demo.security;

import com.example.demo.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    // Constructor para inyección de dependencias
    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, 
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Omitir autenticación para endpoints públicos
            if (isPublicEndpoint(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extraer token del encabezado
            String jwt = extractTokenFromHeader(request);

            if (jwt != null) {
                // Obtener nombre de usuario del token
                String username = jwtTokenUtil.getUsernameFromToken(jwt);

                // Verificar que el nombre de usuario no sea nulo y que no haya autenticación previa
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Cargar detalles del usuario
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validar token
                    if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                        // Crear token de autenticación
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        // Establecer detales de la autenticación
                        authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Establecer autenticación en el contexto de seguridad
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            // Registrar cualquier error en el procesamiento del token
            logger.error("No se pudo establecer la autenticación del usuario", e);
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Método para extraer el token del encabezado de autorización
     * @param request Solicitud HTTP
     * @return Token JWT o null si no existe
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        
        return null;
    }

    /**
     * Método para identificar endpoints públicos
     * @param request Solicitud HTTP
     * @return true si es un endpoint público, false en caso contrario
     */
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/auth/login") || 
               path.contains("/api/auth/registro") || 
               path.contains("/api/public/");
    }
}