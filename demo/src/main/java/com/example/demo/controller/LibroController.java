package com.example.demo.controller;

import com.example.demo.model.Autor;
import com.example.demo.model.Editorial;
import com.example.demo.model.Libro;
import com.example.demo.service.AutorService;
import com.example.demo.service.EditorialService;
import com.example.demo.service.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @Autowired
    private LibroService libroService;
    
    @Autowired
    private AutorService autorService;
    
    @Autowired
    private EditorialService editorialService;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    // Obtener todos los libros
    @GetMapping
    public ResponseEntity<List<Libro>> getAllLibros() {
        return ResponseEntity.ok(libroService.getAllLibros());
    }
    
    // Obtener un libro por ID
    @GetMapping("/{id}")
    public ResponseEntity<Libro> getLibroById(@PathVariable("id") Long id) {
        return libroService.getLibroById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar libros por título
    @GetMapping("/search/titulo")
    public ResponseEntity<List<Libro>> searchLibrosByTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(libroService.searchLibrosByTitulo(titulo));
    }
    
    // Buscar libros por género
    @GetMapping("/search/genero")
    public ResponseEntity<List<Libro>> searchLibrosByGenero(@RequestParam String genero) {
        return ResponseEntity.ok(libroService.searchLibrosByGenero(genero));
    }
    
    // Método para crear libro con JSON
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createLibroJson(@RequestBody Libro libro) {
        try {
            // Logging detallado
            logger.info("Intentando crear libro: {}", libro);
            
            // Validaciones básicas
            if (libro.getTitulo() == null || libro.getTitulo().isEmpty()) {
                logger.warn("Intento de crear libro sin título");
                return ResponseEntity.badRequest().body("El título es obligatorio");
            }

            // Procesar editorial si se proporciona
            if (libro.getEditorial() != null && libro.getEditorial().getId() != null) {
                logger.info("Buscando editorial con ID: {}", libro.getEditorial().getId());
                Editorial editorial = editorialService.getEditorialById(libro.getEditorial().getId())
                    .orElse(null);
                
                if (editorial == null) {
                    logger.warn("Editorial no encontrada con ID: {}", libro.getEditorial().getId());
                }
                
                libro.setEditorial(editorial);
            }

            // Establecer fechas
            libro.setFechaCreacion(LocalDateTime.now());
            libro.setFechaActualizacion(LocalDateTime.now());

            // Guardar libro
            Libro savedLibro = libroService.saveLibro(libro);
            logger.info("Libro creado exitosamente: {}", savedLibro);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLibro);
        } catch (Exception e) {
            logger.error("Error al crear libro", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear libro: " + e.getMessage());
        }
    }
    
    // AÑADIR: Método para eliminar un libro
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLibro(@PathVariable("id") Long id) {
        logger.info("Intentando eliminar libro con ID: {}", id);
        try {
            if (!libroService.existsById(id)) {
                logger.warn("Intento de eliminar libro inexistente con ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            libroService.deleteLibro(id);
            logger.info("Libro con ID: {} eliminado exitosamente", id);
            return ResponseEntity.ok().body("Libro eliminado correctamente");
        } catch (Exception e) {
            logger.error("Error al eliminar libro con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar libro: " + e.getMessage());
        }
    }
    
    // AÑADIR: Método para actualizar un libro completamente (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLibro(@PathVariable("id") Long id, @RequestBody Libro libro) {
        logger.info("Intentando actualizar libro con ID: {}", id);
        try {
            return libroService.getLibroById(id)
                    .map(existingLibro -> {
                        // Actualizar campos
                        existingLibro.setTitulo(libro.getTitulo());
                        existingLibro.setIsbn(libro.getIsbn());
                        existingLibro.setGenero(libro.getGenero());
                        existingLibro.setPaginas(libro.getPaginas());
                        existingLibro.setDescripcion(libro.getDescripcion());
                        
                        // Procesar editorial si se proporciona
                        if (libro.getEditorial() != null && libro.getEditorial().getId() != null) {
                            Editorial editorial = editorialService.getEditorialById(libro.getEditorial().getId())
                                    .orElse(null);
                            existingLibro.setEditorial(editorial);
                        }
                        
                        // Actualizar autores si se proporcionan
                        if (libro.getAutores() != null) {
                            existingLibro.setAutores(libro.getAutores());
                        }
                        
                        // Actualizar fecha
                        existingLibro.setFechaActualizacion(LocalDateTime.now());
                        
                        Libro updatedLibro = libroService.saveLibro(existingLibro);
                        logger.info("Libro actualizado exitosamente: {}", updatedLibro);
                        return ResponseEntity.ok(updatedLibro);
                    })
                    .orElseGet(() -> {
                        logger.warn("Libro no encontrado con ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error al actualizar libro con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar libro: " + e.getMessage());
        }
    }
    
    // AÑADIR: Método para actualizar campos específicos de un libro (PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLibro(@PathVariable("id") Long id, @RequestBody Map<String, Object> updates) {
        logger.info("Intentando actualizar parcialmente libro con ID: {}", id);
        try {
            return libroService.getLibroById(id)
                    .map(existingLibro -> {
                        // Actualizar solo los campos proporcionados
                        if (updates.containsKey("titulo")) {
                            existingLibro.setTitulo((String) updates.get("titulo"));
                        }
                        if (updates.containsKey("isbn")) {
                            existingLibro.setIsbn((String) updates.get("isbn"));
                        }
                        if (updates.containsKey("genero")) {
                            existingLibro.setGenero((String) updates.get("genero"));
                        }
                        if (updates.containsKey("paginas")) {
                            existingLibro.setPaginas((Integer) updates.get("paginas"));
                        }
                        if (updates.containsKey("descripcion")) {
                            existingLibro.setDescripcion((String) updates.get("descripcion"));
                        }
                        
                        // Actualizar editorial si se proporciona
                        if (updates.containsKey("editorial") && updates.get("editorial") != null) {
                            Map<String, Object> editorialMap = (Map<String, Object>) updates.get("editorial");
                            if (editorialMap.containsKey("id")) {
                                Long editorialId = ((Number) editorialMap.get("id")).longValue();
                                Editorial editorial = editorialService.getEditorialById(editorialId)
                                        .orElse(null);
                                existingLibro.setEditorial(editorial);
                            }
                        }
                        
                        // Actualizar fecha
                        existingLibro.setFechaActualizacion(LocalDateTime.now());
                        
                        Libro updatedLibro = libroService.saveLibro(existingLibro);
                        logger.info("Libro actualizado parcialmente: {}", updatedLibro);
                        return ResponseEntity.ok(updatedLibro);
                    })
                    .orElseGet(() -> {
                        logger.warn("Libro no encontrado con ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error al actualizar parcialmente libro con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar parcialmente libro: " + e.getMessage());
        }
    }
    
    // Método para crear libro con multipart form-data (aquí deberías completar tu código)
    // ...
}