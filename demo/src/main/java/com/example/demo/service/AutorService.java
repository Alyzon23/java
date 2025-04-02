package com.example.demo.service;

import com.example.demo.model.Autor;
import com.example.demo.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;
    
    // Obtener todos los autores
    public List<Autor> getAllAutores() {
        return autorRepository.findAll();
    }
    
    // Obtener un autor por ID
    public Optional<Autor> getAutorById(Long id) {
        return autorRepository.findById(id);
    }
    
    // Buscar autores por nombre o apellidos
    public List<Autor> searchAutoresByNombre(String nombre) {
        return autorRepository.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, nombre);
    }
    
    // Guardar un autor
    public Autor saveAutor(Autor autor) {
        return autorRepository.save(autor);
    }
    
    // Actualizar un autor existente
    public Autor updateAutor(Long id, Autor autorDetails) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
        
        autor.setNombre(autorDetails.getNombre());
        autor.setApellidos(autorDetails.getApellidos());
        autor.setBiografia(autorDetails.getBiografia());
        autor.setNacionalidad(autorDetails.getNacionalidad());
        autor.setFechaNacimiento(autorDetails.getFechaNacimiento());
        
        return autorRepository.save(autor);
    }
    
    // Eliminar un autor
    public void deleteAutor(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + id));
        
        autorRepository.deleteById(id);
    }
}