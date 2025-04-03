package com.example.demo.controller;

import com.example.demo.model.Editorial;
import com.example.demo.service.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {

    @Autowired
    private EditorialService editorialService;
    
    // Obtener todas las editoriales
    @GetMapping
    public ResponseEntity<List<Editorial>> getAllEditoriales() {
        return ResponseEntity.ok(editorialService.getAllEditoriales());
    }
    
    // Obtener una editorial por ID
    @GetMapping("/{id}")
    public ResponseEntity<Editorial> getEditorialById(@PathVariable("id") Long id) {
        return editorialService.getEditorialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar editoriales por nombre
    @GetMapping("/search")
    public ResponseEntity<List<Editorial>> searchEditoriales(@RequestParam String nombre) {
        return ResponseEntity.ok(editorialService.searchEditorialesByNombre(nombre));
    }
    
    // Crear una nueva editorial
    @PostMapping
    public ResponseEntity<Editorial> createEditorial(@RequestBody Editorial editorial) {
        Editorial savedEditorial = editorialService.saveEditorial(editorial);
        return new ResponseEntity<>(savedEditorial, HttpStatus.CREATED);
    }
    
    // Actualizar una editorial existente
    @PutMapping("/{id}")
    public ResponseEntity<Editorial> updateEditorial(
            @PathVariable("id") Long id,
            @RequestBody Editorial editorialDetails) {
        try {
            Editorial updatedEditorial = editorialService.updateEditorial(id, editorialDetails);
            return ResponseEntity.ok(updatedEditorial);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar una editorial
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEditorial(@PathVariable("id") Long id) {
        try {
            editorialService.deleteEditorial(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}