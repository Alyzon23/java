package com.example.demo.service;

import com.example.demo.exceptions.UsuarioYaExisteException;
import com.example.demo.exceptions.CampoInvalidoException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class RegistroService {
    private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        logger.info("Intentando registrar un nuevo usuario: {}", usuario.getUsername());

        // Verificar si el usuario ya existe
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            logger.error("El nombre de usuario ya existe: {}", usuario.getUsername());
            throw new UsuarioYaExisteException("El usuario ya existe");
        }

        // Verificar si el email ya está registrado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            logger.error("El email ya está registrado: {}", usuario.getEmail());
            throw new UsuarioYaExisteException("El email ya está registrado");
        }

        // Validar campos requeridos
        validarCamposUsuario(usuario);

        // Codificar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Establecer fechas y valores predeterminados
        if (usuario.getRole() == null) {
            usuario.setRole(Usuario.Role.ROLE_USER);
        }
        
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuario.setActive(Boolean.TRUE);

        try {
            // Guardar el usuario
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            logger.info("Usuario registrado exitosamente: {}", usuarioGuardado.getUsername());
            return usuarioGuardado;
        } catch (Exception e) {
            // Log detallado de cualquier error de persistencia
            logger.error("Error al guardar el usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al registrar el usuario", e);
        }
    }

    private void validarCamposUsuario(Usuario usuario) {
        // Validar nombre de usuario
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new CampoInvalidoException("El nombre de usuario es requerido");
        }

        // Validar longitud del nombre de usuario
        if (usuario.getUsername().length() < 3 || usuario.getUsername().length() > 20) {
            throw new CampoInvalidoException("El nombre de usuario debe tener entre 3 y 20 caracteres");
        }

        // Validar contraseña
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new CampoInvalidoException("La contraseña es requerida");
        }

        // Validar fortaleza de la contraseña 
        if (!Pattern.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", usuario.getPassword())) {
            throw new CampoInvalidoException("La contraseña debe tener al menos 8 caracteres, una mayúscula, un número y un carácter especial");
        }

        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new CampoInvalidoException("El email es requerido");
        }

        // Validar formato de email
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", usuario.getEmail())) {
            throw new CampoInvalidoException("Formato de email inválido");
        }

        // Validar nombre
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new CampoInvalidoException("El nombre es requerido");
        }

        // Validar apellidos
        if (usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            throw new CampoInvalidoException("Los apellidos son requeridos");
        }
    }

    public boolean existeUsuario(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }
}