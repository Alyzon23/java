package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libros")
@Getter
@Setter
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
    
    @Column(length = 2000, name = "descripcion")
    private String descripcion;
    
    @Column(name = "paginas")
    private Integer paginas;
    
    @Column(name = "file_path")
    private String filePath;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    // Relación con Editorial - Un libro pertenece a una editorial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editorial_id")
    private Editorial editorial;
    
    // Relación con Autor - Un libro puede tener varios autores
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "libro_autor",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();
    
    // Relación con Imagen - Un libro puede tener varias imágenes
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Imagen> imagenes = new HashSet<>();
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    public Libro() {
    }
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Métodos de compatibilidad
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
    
    // Métodos para manejo de relaciones
    public void addAutor(Autor autor) {
        autores.add(autor);
        if (autor.getLibros() != null) {
            autor.getLibros().add(this);
        }
    }
    
    public void removeAutor(Autor autor) {
        autores.remove(autor);
        if (autor.getLibros() != null) {
            autor.getLibros().remove(this);
        }
    }
    
    public void addImagen(Imagen imagen) {
        imagenes.add(imagen);
        imagen.setLibro(this);
    }
    
    public void removeImagen(Imagen imagen) {
        imagenes.remove(imagen);
        imagen.setLibro(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return id != null && id.equals(libro.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", isbn='" + isbn + '\'' +
                ", anioPublicacion=" + anioPublicacion +
                ", genero='" + genero + '\'' +
                '}';
    }
}