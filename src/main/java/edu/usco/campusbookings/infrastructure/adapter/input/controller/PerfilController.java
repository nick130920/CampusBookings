package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ActualizarPerfilRequest;
import edu.usco.campusbookings.application.dto.response.PerfilResponse;
import edu.usco.campusbookings.application.port.input.PerfilUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilUseCase perfilUseCase;

    @GetMapping
    public ResponseEntity<PerfilResponse> obtenerPerfil() {
        return ResponseEntity.ok(perfilUseCase.obtenerPerfil());
    }

    @PutMapping
    public ResponseEntity<PerfilResponse> actualizarPerfil(@RequestBody ActualizarPerfilRequest request) {
        return ResponseEntity.ok(perfilUseCase.actualizarPerfil(request));
    }
}
