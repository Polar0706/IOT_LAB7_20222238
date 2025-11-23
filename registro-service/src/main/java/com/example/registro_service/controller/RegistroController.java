package com.example.registro_service.controller;

import com.example.registro_service.client.ValidacionClient;
import com.example.registro_service.dto.RegistroRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private ValidacionClient validacionClient;

    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody RegistroRequest request) {
        try {
            ResponseEntity<String> dniResponse = validacionClient.validarDni(request.getDni());
            if (dniResponse.getStatusCode().value() != 200) {
                return ResponseEntity.badRequest().body(dniResponse.getBody());
            }

            ResponseEntity<String> correoResponse = validacionClient.validarCorreo(request.getCorreo());
            if (correoResponse.getStatusCode().value() != 200) {
                return ResponseEntity.badRequest().body(correoResponse.getBody());
            }

            return ResponseEntity.ok("Validación exitosa");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error en la validación: " + e.getMessage());
        }
    }
}
