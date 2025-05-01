package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String nombre;
    
    private String apellidos;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ultimo_acceso", nullable = false)
    private LocalDateTime ultimoAcceso;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
    
    @Column(name = "activo", nullable = false)
    private Boolean active;
    
    public Usuario() {
        this.role = Role.ROLE_USER;
        this.active = true;
    }
    
    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
        if (this.ultimoAcceso == null) {
            this.ultimoAcceso = LocalDateTime.now();
        }
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
        if (this.role == null) {
            this.role = Role.ROLE_USER;
        }
    }
    
    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }
}