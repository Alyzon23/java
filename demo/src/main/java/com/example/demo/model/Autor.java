package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String apellidos;
    
    private String biografia;
    
    private String nacionalidad;
    
    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;
    
    // Relación con Libro - Un autor puede tener muchos libros
    @ManyToMany(mappedBy = "autores")
    private Set<Libro> libros = new HashSet<>();
}