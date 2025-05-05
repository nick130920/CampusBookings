package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ReporteReservasRequest;
import edu.usco.campusbookings.application.dto.response.ReporteReservasResponse;
import edu.usco.campusbookings.application.port.input.ReporteReservasUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteReservasService implements ReporteReservasUseCase {

    private final ReservaRepositoryPort reservaRepositoryPort;
    private final EscenarioRepositoryPort escenarioRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<ReporteReservasResponse> generarReporte(ReporteReservasRequest request) {
        // Obtener todas las reservas en el rango de fechas
        List<Reserva> reservas = reservaRepositoryPort.findAll();
        
        if (reservas.isEmpty()) {
            return new ArrayList<>();
        }

        // Filtrar por fecha
        reservas = reservas.stream()
                .filter(r -> r.getFechaInicio().isAfter(request.getFechaInicio())
                        && r.getFechaFin().isBefore(request.getFechaFin()))
                .collect(Collectors.toList());

        // Filtrar por tipo si se especifica
        if (request.getTipo() != null) {
            List<Escenario> escenarios = escenarioRepositoryPort.findAll();
            List<Long> escenarioIds = escenarios.stream()
                    .filter(e -> e.getTipo().equalsIgnoreCase(request.getTipo()))
                    .map(Escenario::getId)
                    .collect(Collectors.toList());

            reservas = reservas.stream()
                    .filter(r -> escenarioIds.contains(r.getEscenario().getId()))
                    .collect(Collectors.toList());
        }

        // Filtrar por estado si se especifica
        if (request.getEstado() != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getEstado().getNombre().equalsIgnoreCase(request.getEstado()))
                    .collect(Collectors.toList());
        }

        // Agrupar por escenario y generar el reporte
        Map<Long, List<Reserva>> reservasPorEscenario = reservas.stream()
                .collect(Collectors.groupingBy(r -> r.getEscenario().getId()));

        List<ReporteReservasResponse> reporte = new ArrayList<>();
        for (Map.Entry<Long, List<Reserva>> entry : reservasPorEscenario.entrySet()) {
            Long escenarioId = entry.getKey();
            List<Reserva> reservasEscenario = entry.getValue();

            Escenario escenario = escenarioRepositoryPort.findById(escenarioId)
                    .orElseThrow(() -> new RuntimeException("Escenario no encontrado"));

            ReporteReservasResponse response = ReporteReservasResponse.builder()
                    .escenarioId(escenarioId)
                    .escenarioNombre(escenario.getNombre())
                    .tipo(escenario.getTipo())
                    .estado(reservasEscenario.get(0).getEstado().getNombre())
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .cantidadReservas(reservasEscenario.size())
                    .build();

            reporte.add(response);
        }

        return reporte;
    }
}
