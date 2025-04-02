package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "imagenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombreArchivo;
    
    @Column(name = "tipo_imagen")
    private String tipoImagen; // portada, contraportada, etc.
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "content_type")
    private String contentType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    // Relación con Libro - Una imagen pertenece a un libro
    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;
    
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;
    
    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }
}