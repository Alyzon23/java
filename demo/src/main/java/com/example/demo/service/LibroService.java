package com.example.demo.service;

import com.example.demo.model.Autor;
import com.example.demo.model.Editorial;
import com.example.demo.model.Libro;
import com.example.demo.repository.AutorRepository;
import com.example.demo.repository.EditorialRepository;
import com.example.demo.repository.LibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {
    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);

    @Autowired
    private LibroRepository libroRepository;
    
    @Autowired
    private AutorRepository autorRepository;
    
    @Autowired
    private EditorialRepository editorialRepository;
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    // Obtener todos los libros
    @Transactional(readOnly = true)
    public List<Libro> getAllLibros() {
        return libroRepository.findAll();
    }
    
    // Obtener un libro por ID
    @Transactional(readOnly = true)
    public Optional<Libro> getLibroById(Long id) {
        return libroRepository.findById(id);
    }
    
    // Verificar si existe un libro por ID
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return libroRepository.existsById(id);
    }
    
    // Buscar libros por título
    @Transactional(readOnly = true)
    public List<Libro> searchLibrosByTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    // Buscar libros por género
    @Transactional(readOnly = true)
    public List<Libro> searchLibrosByGenero(String genero) {
        return libroRepository.findByGeneroContainingIgnoreCase(genero);
    }
    
    // Buscar libros por autor
    @Transactional(readOnly = true)
    public List<Libro> searchLibrosByAutor(Long autorId) {
        return libroRepository.findByAutoresId(autorId);
    }
    
    // Buscar libros por editorial
    @Transactional(readOnly = true)
    public List<Libro> searchLibrosByEditorial(Long editorialId) {
        return libroRepository.findByEditorialId(editorialId);
    }
    
    // Buscar libros por año de publicación
    @Transactional(readOnly = true)
    public List<Libro> searchLibrosByAnioPublicacion(Integer anioPublicacion) {
        return libroRepository.findByAnioPublicacion(anioPublicacion);
    }
    
    // Guardar un libro
    @Transactional
    public Libro saveLibro(Libro libro) {
        // Validaciones
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro es obligatorio");
        }

        // Log de depuración
        logger.info("Guardando libro: {}", libro.getTitulo());
        
        // Manejar editorial de manera opcional
        if (libro.getEditorial() != null && libro.getEditorial().getId() != null) {
            Editorial editorial = editorialRepository.findById(libro.getEditorial().getId())
                .orElse(null);
            
            // Log de editorial
            if (editorial == null) {
                logger.warn("Editorial con ID {} no encontrada", libro.getEditorial().getId());
            }
            
            libro.setEditorial(editorial);
        }
        
        // Establecer fechas
        if (libro.getId() == null) {
            libro.setFechaCreacion(LocalDateTime.now());
            libro.setFechaActualizacion(LocalDateTime.now());
        } else {
            libro.setFechaActualizacion(LocalDateTime.now());
        }
        
        return libroRepository.save(libro);
    }
    
    // Actualizar un libro existente
    @Transactional
    public Libro updateLibro(Long id, Libro libroDetails) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con id: " + id));
        
        // Actualizar propiedades básicas
        libro.setTitulo(libroDetails.getTitulo());
        libro.setIsbn(libroDetails.getIsbn());
        libro.setAnioPublicacion(libroDetails.getAnioPublicacion());
        libro.setGenero(libroDetails.getGenero());
        libro.setDescripcion(libroDetails.getDescripcion()); // Cambiado de setSinopsis a setDescripcion
        libro.setPaginas(libroDetails.getPaginas()); // Cambiado de setNumeroPaginas a setPaginas
        
        // Actualizar autores si se proporcionan
        if (libroDetails.getAutores() != null) {
            libro.setAutores(libroDetails.getAutores());
        }
        
        // Actualizar editorial de manera opcional
        if (libroDetails.getEditorial() != null && libroDetails.getEditorial().getId() != null) {
            Editorial editorial = editorialRepository.findById(libroDetails.getEditorial().getId())
                    .orElse(null);
            libro.setEditorial(editorial);
        }
        
        // Actualizar fecha de modificación
        libro.setFechaActualizacion(LocalDateTime.now());
        
        return libroRepository.save(libro);
    }
    
    // Eliminar un libro
    @Transactional
    public void deleteLibro(Long id) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado con id: " + id));
        
        // Eliminar archivo asociado si existe
        Optional.ofNullable(libro.getFilePath())
            .filter(path -> !path.isEmpty())
            .ifPresent(path -> {
                try {
                    Files.deleteIfExists(Paths.get(path));
                } catch (IOException e) {
                    logger.error("Error al eliminar archivo del libro", e);
                    throw new RuntimeException("Error al eliminar archivo del libro", e);
                }
            });
        
        libroRepository.deleteById(id);
    }
    
    // Añadir un autor a un libro
    @Transactional
    public Libro addAutorToLibro(Long libroId, Long autorId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        
        Autor autor = autorRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        libro.addAutor(autor);
        libro.setFechaActualizacion(LocalDateTime.now());
        return libroRepository.save(libro);
    }
    
    // Quitar un autor de un libro
    @Transactional
    public Libro removeAutorFromLibro(Long libroId, Long autorId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        
        Autor autor = autorRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        libro.removeAutor(autor);
        libro.setFechaActualizacion(LocalDateTime.now());
        return libroRepository.save(libro);
    }
    
    // Establecer editorial para un libro
    @Transactional
    public Libro setEditorialForLibro(Long libroId, Long editorialId) {
        Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        
        Editorial editorial = editorialRepository.findById(editorialId)
                .orElseThrow(() -> new RuntimeException("Editorial no encontrada"));
        
        libro.setEditorial(editorial);
        libro.setFechaActualizacion(LocalDateTime.now());
        return libroRepository.save(libro);
    }
    
    // Contar libros por género
    @Transactional(readOnly = true)
    public long countLibrosByGenero(String genero) {
        return libroRepository.countByGenero(genero);
    }
    
    // Obtener libros recientes con paginación
    @Transactional(readOnly = true)
    public List<Libro> getRecentLibros(int count) {
        Pageable pageable = PageRequest.of(0, count, Sort.by("fechaCreacion").descending());
        Page<Libro> page = libroRepository.findAll(pageable);
        return page.getContent();
    }
}