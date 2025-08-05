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

import edu.usco.campusbookings.application.dto.request.RechazarReservaRequest;
import edu.usco.campusbookings.application.dto.request.ReporteReservasRequest;
import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.request.VerificarDisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.ReporteReservasResponse;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.application.port.input.ReporteReservasUseCase;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaRestController {

    private final ReservaUseCase reservaUseCase;
    private final ReporteReservasUseCase reporteReservasUseCase;

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(
            @Valid @RequestBody ReservaRequest request
    ) {
        return ResponseEntity.ok(reservaUseCase.crearReserva(request));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ReservaResponse> aprobarReserva(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.aprobarReserva(id));
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ReservaResponse> rechazarReserva(
            @PathVariable Long id,
            @Valid @RequestBody RechazarReservaRequest request
    ) {
        return ResponseEntity.ok(reservaUseCase.rechazarReserva(id, request.getMotivo()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.cancelarReserva(id));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorUsuario(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorUsuario(id));
    }

    @PostMapping("/reporte")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReporteReservasResponse>> generarReporte(
            @Valid @RequestBody ReporteReservasRequest request
    ) {
        return ResponseEntity.ok(reporteReservasUseCase.generarReporte(request));
    }

    @GetMapping("/escenario/{id}")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorEscenario(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorEscenario(id));
    }

    @GetMapping("/estado/{nombre}")
    public ResponseEntity<List<ReservaResponse>> obtenerReservasPorEstado(
            @PathVariable String nombre
    ) {
        return ResponseEntity.ok(reservaUseCase.obtenerReservasPorEstado(nombre));
    }

    @GetMapping("/admin/todas")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReservaResponse>> obtenerTodasLasReservas() {
        return ResponseEntity.ok(reservaUseCase.obtenerTodasLasReservas());
    }

    @PostMapping("/verificar-disponibilidad")
               description = "Verifica si un escenario está disponible para el horario solicitado, proporcionando alternativas si no está disponible (inspirado en Cal.com)")
    public ResponseEntity<DisponibilidadResponse> verificarDisponibilidad(
            @Valid @RequestBody VerificarDisponibilidadRequest request
    ) {
        return ResponseEntity.ok(reservaUseCase.verificarDisponibilidad(request));
    }
}
