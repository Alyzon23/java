package com.example.demo.service;

import com.example.demo.model.Editorial;
import com.example.demo.repository.EditorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorialService {

    @Autowired
    private EditorialRepository editorialRepository;
    
    // Obtener todas las editoriales
    public List<Editorial> getAllEditoriales() {
        return editorialRepository.findAll();
    }
    
    // Obtener una editorial por ID
    public Optional<Editorial> getEditorialById(Long id) {
        return editorialRepository.findById(id);
    }
    
    // Buscar editoriales por nombre
    public List<Editorial> searchEditorialesByNombre(String nombre) {
        return editorialRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    // Guardar una editorial
    public Editorial saveEditorial(Editorial editorial) {
        return editorialRepository.save(editorial);
    }
    
    // Actualizar una editorial existente
    public Editorial updateEditorial(Long id, Editorial editorialDetails) {
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editorial no encontrada con id: " + id));
        
        editorial.setNombre(editorialDetails.getNombre());
        editorial.setDireccion(editorialDetails.getDireccion());
        editorial.setCiudad(editorialDetails.getCiudad());
        editorial.setPais(editorialDetails.getPais());
        editorial.setTelefono(editorialDetails.getTelefono());
        editorial.setEmail(editorialDetails.getEmail());
        editorial.setSitioWeb(editorialDetails.getSitioWeb());
        
        return editorialRepository.save(editorial);
    }
    
    // Eliminar una editorial
    public void deleteEditorial(Long id) {
        Editorial editorial = editorialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editorial no encontrada con id: " + id));
        
        editorialRepository.deleteById(id);
    }
}