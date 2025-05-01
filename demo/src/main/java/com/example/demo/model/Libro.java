package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String titulo;
    
    private String isbn;
    
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;
    
    private String genero;
    
    @Column(length = 2000, name = "descripcion")    // Cambiado de sinopsis a descripcion
    private String descripcion;                     // Cambiado de sinopsis a descripcion
    
    @Column(name = "paginas")                       // Cambiado de numero_paginas a paginas
    private Integer paginas;                        // Cambiado de numeroPaginas a paginas
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    // Relación con Editorial - Un libro pertenece a una editorial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editorial_id")
    @JsonBackReference
    private Editorial editorial;
    
    // Relación con Autor - Un libro puede tener varios autores
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "libro_autor",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    @JsonManagedReference
    private Set<Autor> autores = new HashSet<>();
    
    // Relación con Imagen - Un libro puede tener varias imágenes
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<Imagen> imagenes = new HashSet<>();
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de compatibilidad para mantener backward compatibility
    // Estos métodos permiten usar tanto los nuevos nombres como los antiguos
    
    public String getSinopsis() {
        return this.descripcion;
    }
    
    public void setSinopsis(String sinopsis) {
        this.descripcion = sinopsis;
    }
    
    public Integer getNumeroPaginas() {
        return this.paginas;
    }
    
    public void setNumeroPaginas(Integer numeroPaginas) {
        this.paginas = numeroPaginas;
    }
    
    // Métodos útiles para manejar las relaciones
    public void addAutor(Autor autor) {
        this.autores.add(autor);
        autor.getLibros().add(this);
    }
    
    public void removeAutor(Autor autor) {
        this.autores.remove(autor);
        autor.getLibros().remove(this);
    }
    
    public void addImagen(Imagen imagen) {
        this.imagenes.add(imagen);
        imagen.setLibro(this);
    }
    
    public void removeImagen(Imagen imagen) {
        this.imagenes.remove(imagen);
        imagen.setLibro(null);
    }
}