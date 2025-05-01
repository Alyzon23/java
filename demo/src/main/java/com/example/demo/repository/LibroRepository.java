package com.example.demo.repository;

import com.example.demo.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Búsqueda por título (ignorando mayúsculas/minúsculas)
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    
    // Búsqueda por género (ignorando mayúsculas/minúsculas)
    List<Libro> findByGeneroContainingIgnoreCase(String genero);
    
    // Búsqueda por autor
    List<Libro> findByAutoresId(Long autorId);
    
    // Búsqueda por editorial
    List<Libro> findByEditorialId(Long editorialId);
    
    // Búsqueda por año de publicación
    List<Libro> findByAnioPublicacion(Integer anioPublicacion);
    
    // Contar libros por género
    long countByGenero(String genero);
    
    // Obtener los últimos libros añadidos
    List<Libro> findTop10ByOrderByFechaCreacionDesc();
    
    // Búsqueda por ISBN
    Optional<Libro> findByIsbn(String isbn);
    
    // Búsqueda por título exacto
    Optional<Libro> findByTituloIgnoreCase(String titulo);
    
    // Libros publicados entre años específicos
    List<Libro> findByAnioPublicacionBetween(Integer startYear, Integer endYear);
}