package com.example.demo.controller;

import com.example.demo.model.Imagen;
import com.example.demo.model.Libro;
import com.example.demo.service.ImagenService;
import com.example.demo.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @Autowired
    private LibroService libroService;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    // Obtener todas las imágenes
    @GetMapping
    public ResponseEntity<List<Imagen>> getAllImagenes() {
        return ResponseEntity.ok(imagenService.getAllImagenes());
    }

    // Obtener una imagen por ID
    @GetMapping("/{id}")
    public ResponseEntity<Imagen> getImagenById(@PathVariable("id") Long id) {
        return imagenService.getImagenById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Subir una imagen para un libro
    @PostMapping("/libro/{libroId}")
    public ResponseEntity<?> uploadImagen(
            @PathVariable Long libroId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("tipoImagen") String tipoImagen) {
        
        try {
            // Verificar que el libro existe
            Libro libro = libroService.getLibroById(libroId)
                    .orElseThrow(() -> new RuntimeException("Libro no encontrado con id: " + libroId));
            
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generar nombre único para el archivo
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            
            // Guardar el archivo en el sistema de archivos
            Files.copy(file.getInputStream(), filePath);
            
            // Crear entidad Imagen
            Imagen imagen = new Imagen();
            imagen.setNombreArchivo(fileName);
            imagen.setFilePath(filePath.toString());
            imagen.setContentType(file.getContentType());
            imagen.setFileSize(file.getSize());
            imagen.setTipoImagen(tipoImagen);
            imagen.setLibro(libro);
            
            // Guardar imagen en base de datos
            Imagen savedImagen = imagenService.saveImagen(imagen);
            
            return new ResponseEntity<>(savedImagen, HttpStatus.CREATED);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir imagen: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener el contenido de una imagen
    @GetMapping("/{id}/content")
    public ResponseEntity<?> getImagenContent(@PathVariable("id") Long id) {
        try {
            Imagen imagen = imagenService.getImagenById(id)
                    .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
            
            Path imagePath = Paths.get(imagen.getFilePath());
            byte[] imageBytes = Files.readAllBytes(imagePath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(imagen.getContentType()))
                    .body(imageBytes);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al recuperar la imagen: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener imágenes por libro
    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<Imagen>> getImagenesByLibro(@PathVariable Long libroId) {
        return ResponseEntity.ok(imagenService.getImagenesByLibroId(libroId));
    }

    // Eliminar una imagen
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImagen(@PathVariable("id") Long id) {
        try {
            Imagen imagen = imagenService.getImagenById(id)
                    .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
            
            // Eliminar archivo físico
            Path imagePath = Paths.get(imagen.getFilePath());
            Files.deleteIfExists(imagePath);
            
            // Eliminar registro de base de datos
            imagenService.deleteImagen(id);
            
            return ResponseEntity.noContent().build();
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}