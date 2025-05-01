package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Log para depuración
        logger.debug("Intentando cargar usuario: {}", username);

        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> {
                logger.error("Usuario no encontrado: {}", username);
                return new UsernameNotFoundException("Usuario no encontrado: " + username);
            });

        // Log adicional de seguridad
        logger.info("Usuario encontrado: {}", username);

        return new org.springframework.security.core.userdetails.User(
            usuario.getUsername(), 
            usuario.getPassword(), 
            true,           // enabled
            true,           // accountNonExpired
            true,           // credentialsNonExpired
            true,           // accountNonLocked
            Collections.singletonList(new SimpleGrantedAuthority(usuario.getRole().name()))
        );
    }
}