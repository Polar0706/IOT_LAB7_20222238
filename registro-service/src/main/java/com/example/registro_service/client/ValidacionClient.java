package com.example.registro_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "validacion-service")
public interface ValidacionClient {

    @GetMapping("/validar/dni/{dni}")
    ResponseEntity<String> validarDni(@PathVariable("dni") String dni);

    @GetMapping("/validar/correo/{correo}")
    ResponseEntity<String> validarCorreo(@PathVariable("correo") String correo);
}
