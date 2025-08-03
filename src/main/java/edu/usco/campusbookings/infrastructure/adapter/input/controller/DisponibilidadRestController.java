package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioDisponibilidadResponse;
import edu.usco.campusbookings.application.port.input.DisponibilidadUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
@Tag(name = "Disponibilidad", description = "Operaciones relacionadas con la consulta de disponibilidad de escenarios")
public class DisponibilidadRestController {

    private final DisponibilidadUseCase disponibilidadUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(summary = "Consultar disponibilidad de escenarios")
    public ResponseEntity<List<EscenarioDisponibilidadResponse>> consultarDisponibilidad(
            @Valid @RequestBody DisponibilidadRequest request
    ) {
        return ResponseEntity.ok(disponibilidadUseCase.consultarDisponibilidad(request));
    }
}
