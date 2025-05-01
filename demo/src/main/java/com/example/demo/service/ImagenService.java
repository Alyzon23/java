package com.example.demo.service;

import com.example.demo.model.Imagen;
import com.example.demo.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;
    
    // Obtener todas las imágenes
    public List<Imagen> getAllImagenes() {
        return imagenRepository.findAll();
    }
    
    // Obtener una imagen por ID
    public Optional<Imagen> getImagenById(Long id) {
        return imagenRepository.findById(id);
    }
    
    // Guardar una imagen
    public Imagen saveImagen(Imagen imagen) {
        return imagenRepository.save(imagen);
    }
    
    // Eliminar una imagen
    public void deleteImagen(Long id) {
        imagenRepository.deleteById(id);
    }
    
    // Obtener imágenes por ID de libro
    public List<Imagen> getImagenesByLibroId(Long libroId) {
        return imagenRepository.findByLibroId(libroId);
    }
    
    // Obtener imágenes por tipo
    public List<Imagen> getImagenesByTipo(String tipoImagen) {
        return imagenRepository.findByTipoImagen(tipoImagen);
    }
    
    // Contar imágenes por libro
    public long countImagenesByLibroId(Long libroId) {
        return imagenRepository.countByLibroId(libroId);
    }
    
    // Buscar imágenes por nombre de archivo
    public List<Imagen> searchImagenesByNombreArchivo(String nombreArchivo) {
        return imagenRepository.findByNombreArchivoContainingIgnoreCase(nombreArchivo);
    }
    
    // Actualizar información de una imagen
    public Imagen updateImagen(Long id, Imagen imagenDetails) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada con id: " + id));
        
        // Actualizar campos
        if (imagenDetails.getNombreArchivo() != null) {
            imagen.setNombreArchivo(imagenDetails.getNombreArchivo());
        }
        
        if (imagenDetails.getTipoImagen() != null) {
            imagen.setTipoImagen(imagenDetails.getTipoImagen());
        }
        
        // No actualizamos filePath ni contentType normalmente, ya que son propiedades
        // del archivo físico que no deberían cambiar sin cambiar el archivo
        
        return imagenRepository.save(imagen);
    }
}