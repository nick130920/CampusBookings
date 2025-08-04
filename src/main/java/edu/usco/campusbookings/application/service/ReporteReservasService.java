package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ReporteReservasRequest;
import edu.usco.campusbookings.application.dto.response.ReporteReservasResponse;
import edu.usco.campusbookings.application.port.input.ReporteReservasUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteReservasService implements ReporteReservasUseCase {

    private final ReservaPersistencePort reservaPersistencePort;
    private final EscenarioRepositoryPort escenarioRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteReservasResponse> generarReporte(ReporteReservasRequest request) {
        // Obtener todas las reservas
        List<Reserva> reservas = reservaPersistencePort.findAll();
        
        if (reservas.isEmpty()) {
            return new ArrayList<>();
        }

        // Filtrar por fechas de manera inclusiva (reservas que se solapan con el rango)
        reservas = reservas.stream()
                .filter(r -> {
                    // Una reserva está en el rango si:
                    // - Su fecha de inicio es antes o igual al fin del rango Y
                    // - Su fecha de fin es después o igual al inicio del rango
                    return !r.getFechaInicio().isAfter(request.getFechaFin()) && 
                           !r.getFechaFin().isBefore(request.getFechaInicio());
                })
                .toList();

        // Filtrar por tipo si se especifica
        if (request.getTipo() != null && !request.getTipo().trim().isEmpty()) {
            List<Escenario> escenarios = escenarioRepositoryPort.findAll();
            List<Long> escenarioIds = escenarios.stream()
                    .filter(e -> e.getTipo() != null && 
                              e.getTipo().getNombre() != null &&
                              e.getTipo().getNombre().equalsIgnoreCase(request.getTipo().trim()))
                    .map(Escenario::getId)
                    .toList();

            reservas = reservas.stream()
                    .filter(r -> escenarioIds.contains(r.getEscenario().getId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por estado si se especifica
        if (request.getEstado() != null && !request.getEstado().trim().isEmpty()) {
            reservas = reservas.stream()
                    .filter(r -> r.getEstado().getNombre().equalsIgnoreCase(request.getEstado().trim()))
                    .collect(Collectors.toList());
        }

        // Crear reporte detallado por reserva individual
        List<ReporteReservasResponse> reporte = reservas.stream()
                .map(reserva -> ReporteReservasResponse.builder()
                        .escenarioId(reserva.getEscenario().getId())
                        .escenarioNombre(reserva.getEscenario().getNombre())
                        .tipo(reserva.getEscenario().getTipo() != null ? 
                              reserva.getEscenario().getTipo().getNombre() : "Sin tipo")
                        .estado(reserva.getEstado().getNombre())
                        .fechaInicio(reserva.getFechaInicio())
                        .fechaFin(reserva.getFechaFin())
                        .cantidadReservas(1) // Cada fila representa una reserva
                        .usuarioEmail(reserva.getUsuario().getEmail())
                        .observaciones(reserva.getObservaciones())
                        .build())
                .collect(Collectors.toList());

        return reporte;
    }
}
