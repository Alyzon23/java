package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class testController {

    @GetMapping("/publico")
    public ResponseEntity<String> accesoPublico() {
        return ResponseEntity.ok("Endpoint público");
    }

    @GetMapping("/protegido")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> accesoProtegido() {
        return ResponseEntity.ok("Endpoint protegido - Solo usuarios autenticados");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> accesoAdmin() {
        return ResponseEntity.ok("Endpoint de administrador");
    }
}