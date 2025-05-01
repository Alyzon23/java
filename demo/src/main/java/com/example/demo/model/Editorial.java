package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "sitio_web")
    private String sitioWeb;

    @OneToMany(mappedBy = "editorial", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Libro> libros = new HashSet<>();
}