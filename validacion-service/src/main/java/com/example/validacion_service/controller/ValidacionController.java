package com.example.validacion_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validar")
public class ValidacionController {

    @GetMapping("/dni/{dni}")
    public ResponseEntity<String> validarDni(@PathVariable String dni) {
        if (dni == null || dni.length() != 8) {
            return ResponseEntity.badRequest().body("El DNI debe tener 8 caracteres");
        }
        
        if (!dni.matches("\\d+")) {
            return ResponseEntity.badRequest().body("El DNI debe contener solo números");
        }
        
        return ResponseEntity.ok("DNI válido");
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<String> validarCorreo(@PathVariable String correo) {
        if (correo == null || !correo.endsWith("@pucp.edu.pe")) {
            return ResponseEntity.badRequest().body("El correo debe terminar en @pucp.edu.pe");
        }
        
        return ResponseEntity.ok("Correo válido");
    }
}
