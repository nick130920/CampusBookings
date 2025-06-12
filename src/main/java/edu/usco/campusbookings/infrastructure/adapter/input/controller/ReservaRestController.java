package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.usco.campusbookings.application.dto.request.ReporteReservasRequest;
import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReporteReservasResponse;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.application.port.input.ReporteReservasUseCase;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones relacionadas con la gestión de reservas")
public class ReservaRestController {

    private final ReservaUseCase reservaUseCase;
    private final ReporteReservasUseCase reporteReservasUseCase;

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponse> crearReserva(
            @Valid @RequestBody ReservaRequest request
    ) {
        return ResponseEntity.ok(reservaUseCase.crearReserva(request));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Aprobar una reserva")
    public ResponseEntity<ReservaResponse> aprobarReserva(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.aprobarReserva(id));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Rechazar una reserva")
    public ResponseEntity<ReservaResponse> rechazarReserva(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.rechazarReserva(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.cancelarReserva(id));
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Obtener reservas por usuario")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorUsuario(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorUsuario(id));
    }

    @PostMapping("/reporte")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Generar reporte de reservas")
    public ResponseEntity<List<ReporteReservasResponse>> generarReporte(
            @Valid @RequestBody ReporteReservasRequest request
    ) {
        return ResponseEntity.ok(reporteReservasUseCase.generarReporte(request));
    }

    @GetMapping("/escenario/{id}")
    @Operation(summary = "Obtener reservas por escenario")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorEscenario(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorEscenario(id));
    }

    @GetMapping("/estado/{nombre}")
    @Operation(summary = "Obtener reservas por estado")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorEstado(
            @PathVariable String nombre
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorEstado(nombre));
    }
}
