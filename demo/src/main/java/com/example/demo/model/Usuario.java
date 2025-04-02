package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String nombre;
    
    private String apellidos;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "active")
    private boolean active = true;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        ultimoAcceso = LocalDateTime.now();
    }
    
    // Rol de usuario (puedes expandir según necesites)
    public enum Role {
        ADMIN, USER
    }
}