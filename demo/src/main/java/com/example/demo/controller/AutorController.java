package com.example.demo.controller;

import com.example.demo.model.Autor;
import com.example.demo.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;
    
    // Obtener todos los autores
    @GetMapping
    public ResponseEntity<List<Autor>> getAllAutores() {
        return ResponseEntity.ok(autorService.getAllAutores());
    }
    
    // Obtener un autor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Autor> getAutorById(@PathVariable Long id) {
        return autorService.getAutorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Buscar autores por nombre o apellidos
    @GetMapping("/search")
    public ResponseEntity<List<Autor>> searchAutores(@RequestParam String nombre) {
        return ResponseEntity.ok(autorService.searchAutoresByNombre(nombre));
    }
    
    // Crear un nuevo autor
    @PostMapping
    public ResponseEntity<Autor> createAutor(@RequestBody Autor autor) {
        Autor savedAutor = autorService.saveAutor(autor);
        return new ResponseEntity<>(savedAutor, HttpStatus.CREATED);
    }
    
    // Actualizar un autor existente
    @PutMapping("/{id}")
    public ResponseEntity<Autor> updateAutor(
            @PathVariable Long id,
            @RequestBody Autor autorDetails) {
        try {
            Autor updatedAutor = autorService.updateAutor(id, autorDetails);
            return ResponseEntity.ok(updatedAutor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Eliminar un autor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutor(@PathVariable Long id) {
        try {
            autorService.deleteAutor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}