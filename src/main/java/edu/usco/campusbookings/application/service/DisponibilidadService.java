package edu.usco.campusbookings.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.EscenarioDisponibilidadResponse;
import edu.usco.campusbookings.application.port.input.DisponibilidadUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Escenario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisponibilidadService implements DisponibilidadUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final ReservaPersistencePort reservaPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioDisponibilidadResponse> consultarDisponibilidad(DisponibilidadRequest request) {
        // Get all escenarios first
        List<Escenario> allEscenarios = escenarioRepositoryPort.findAll();
        
        // Filter escenarios based on request parameters
        List<Escenario> escenarios = allEscenarios.stream()
                .filter(escenario -> {
                    boolean matches = true;
                    if (request.getTipo() != null) {
                        matches = escenario.getTipo() != null && 
                                request.getTipo().equalsIgnoreCase(escenario.getTipo().getNombre());
                    }
                    if (request.getNombre() != null && matches) {
                        matches = escenario.getNombre() != null && 
                                escenario.getNombre().toLowerCase().contains(request.getNombre().toLowerCase());
                    }
                    if (request.getUbicacion() != null && matches) {
                        matches = escenario.getUbicacion() != null && 
                                escenario.getUbicacion().getNombre() != null &&
                                escenario.getUbicacion().getNombre().toLowerCase().contains(request.getUbicacion().toLowerCase());
                    }
                    return matches;
                })
                .toList();

        if (escenarios.isEmpty()) {
            return List.of();
        }

        // Verificar disponibilidad de cada escenario
        return escenarios.stream()
                .map(escenario -> {
                    boolean disponible = !reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetween(
                            escenario.getId(),
                            request.getFechaInicio(),
                            request.getFechaFin()
                    );

                    return EscenarioDisponibilidadResponse.builder()
                            .id(escenario.getId())
                            .nombre(escenario.getNombre())
                            .tipo(escenario.getTipo() != null ? escenario.getTipo().getNombre() : null)
                            .ubicacion(escenario.getUbicacion() != null ? escenario.getUbicacion().getNombre() : null)
                            .capacidad(escenario.getCapacidad())
                            .descripcion(escenario.getDescripcion())
                            .imagenUrl(escenario.getImagenUrl())
                            .disponible(disponible)
                            .build();
                })
                .toList();
    }
}
