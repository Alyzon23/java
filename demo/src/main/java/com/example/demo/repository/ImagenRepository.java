package com.example.demo.repository;

import com.example.demo.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByLibroId(Long libroId);
    List<Imagen> findByTipoImagen(String tipoImagen);
    long countByLibroId(Long libroId);
    List<Imagen> findByNombreArchivoContainingIgnoreCase(String nombreArchivo);
}