package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.port.input.DisponibilidadUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadService implements DisponibilidadUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final ReservaRepositoryPort reservaRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> consultarDisponibilidad(DisponibilidadRequest request) {
        // Obtener todos los escenarios que coincidan con los filtros
        List<Escenario> escenarios = escenarioRepositoryPort.findAll();
        
        if (request.getTipo() != null) {
            escenarios = escenarios.stream()
                    .filter(e -> e.getTipo().equalsIgnoreCase(request.getTipo()))
                    .collect(Collectors.toList());
        }
        
        if (request.getUbicacion() != null) {
            escenarios = escenarios.stream()
                    .filter(e -> e.getUbicacion().equalsIgnoreCase(request.getUbicacion()))
                    .collect(Collectors.toList());
        }

        // Verificar disponibilidad de cada escenario
        return escenarios.stream()
                .map(escenario -> {
                    boolean disponible = !reservaRepositoryPort.existsByEscenarioIdAndFechaInicioBetween(
                            escenario.getId(),
                            request.getFechaInicio(),
                            request.getFechaFin()
                    );

                    return DisponibilidadResponse.builder()
                            .id(escenario.getId())
                            .nombre(escenario.getNombre())
                            .tipo(escenario.getTipo())
                            .ubicacion(escenario.getUbicacion())
                            .disponible(disponible)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
