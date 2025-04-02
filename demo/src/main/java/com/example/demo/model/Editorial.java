package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "editoriales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Editorial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    private String direccion;
    
    private String ciudad;
    
    private String pais;
    
    private String telefono;
    
    private String email;
    
    private String sitioWeb;
    
    // Relación con Libro - Una editorial puede tener muchos libros
    @OneToMany(mappedBy = "editorial", cascade = CascadeType.ALL)
    private Set<Libro> libros = new HashSet<>();
}