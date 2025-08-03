package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.HistorialReservasRequest;
import edu.usco.campusbookings.application.dto.response.HistorialReservasResponse;
import edu.usco.campusbookings.application.mapper.HistorialReservasMapper;
import edu.usco.campusbookings.application.port.input.HistorialReservasUseCase;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistorialReservasService implements HistorialReservasUseCase {

    private final ReservaPersistencePort reservaPersistencePort;
    private final HistorialReservasMapper historialReservasMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HistorialReservasResponse> consultarHistorial(HistorialReservasRequest request) {
        // Obtener el usuario actual
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = new Usuario();
        usuario.setEmail(userDetails.getUsername());

        // Obtener todas las reservas del usuario
        List<Reserva> reservas = reservaPersistencePort.findByUsuarioId(usuario.getId());

        // Aplicar filtros
        if (request.getFechaInicio() != null && request.getFechaFin() != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getFechaInicio().isAfter(request.getFechaInicio())
                            && r.getFechaFin().isBefore(request.getFechaFin()))
                    .toList();
        }

        if (request.getEstado() != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getEstado().getNombre().equalsIgnoreCase(request.getEstado()))
                    .toList();
        }

        if (request.getTipo() != null) {
            reservas = reservas.stream()
                    .filter(r -> r.getEscenario().getTipo() != null && 
                              r.getEscenario().getTipo().getNombre() != null &&
                              r.getEscenario().getTipo().getNombre().equalsIgnoreCase(request.getTipo()))
                    .toList();
        }

        // Convertir a DTO
        return reservas.stream()
                .map(historialReservasMapper::toResponse)
                .toList();
    }
}
