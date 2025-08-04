package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.CalendarioDisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.EscenarioDisponibilidadResponse;
import edu.usco.campusbookings.application.port.input.CalendarioDisponibilidadUseCase;
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
    private final CalendarioDisponibilidadUseCase calendarioDisponibilidadUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(summary = "Consultar disponibilidad de escenarios")
    public ResponseEntity<List<EscenarioDisponibilidadResponse>> consultarDisponibilidad(
            @Valid @RequestBody DisponibilidadRequest request
    ) {
        return ResponseEntity.ok(disponibilidadUseCase.consultarDisponibilidad(request));
    }

    @PostMapping("/calendario")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(summary = "Consultar disponibilidad de calendario para múltiples escenarios")
    public ResponseEntity<List<CalendarioDisponibilidadResponse>> consultarDisponibilidadCalendario(
            @Valid @RequestBody DisponibilidadRequest request
    ) {
        return ResponseEntity.ok(calendarioDisponibilidadUseCase.consultarDisponibilidadCalendarioMultiple(request));
    }

    @GetMapping("/calendario/{escenarioId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Operation(summary = "Consultar disponibilidad de calendario para un escenario específico")
    public ResponseEntity<CalendarioDisponibilidadResponse> consultarDisponibilidadCalendarioEscenario(
            @PathVariable Long escenarioId,
            @Valid DisponibilidadRequest request
    ) {
        return ResponseEntity.ok(calendarioDisponibilidadUseCase.consultarDisponibilidadCalendario(escenarioId, request));
    }
}
