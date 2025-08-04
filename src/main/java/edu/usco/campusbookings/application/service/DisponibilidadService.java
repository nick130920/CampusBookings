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
import edu.usco.campusbookings.application.exception.DisponibilidadValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisponibilidadService implements DisponibilidadUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final ReservaPersistencePort reservaPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public List<EscenarioDisponibilidadResponse> consultarDisponibilidad(DisponibilidadRequest request) {
        // Validar fechas
        validarFechas(request);
        
        // Obtener escenarios con filtros aplicados
        List<Escenario> escenarios = obtenerEscenariosFiltrados(request);
        
        if (escenarios.isEmpty()) {
            return List.of();
        }

        // Verificar disponibilidad de cada escenario (solo considerar reservas APROBADAS)
        return escenarios.stream()
                .map(escenario -> {
                    boolean disponible = verificarDisponibilidadEscenario(escenario.getId(), request);
                    
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

    private void validarFechas(DisponibilidadRequest request) {
        if (request.getFechaInicio().isBefore(java.time.LocalDateTime.now())) {
            throw new DisponibilidadValidationException("La fecha de inicio no puede ser anterior al momento actual");
        }
        
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new DisponibilidadValidationException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        // Validar que no exceda 6 meses
        java.time.LocalDateTime maxDate = java.time.LocalDateTime.now().plusMonths(6);
        if (request.getFechaFin().isAfter(maxDate)) {
            throw new DisponibilidadValidationException("La fecha de fin no puede exceder 6 meses desde la fecha actual");
        }
    }

    private List<Escenario> obtenerEscenariosFiltrados(DisponibilidadRequest request) {
        List<Escenario> allEscenarios = escenarioRepositoryPort.findAll();
        
        return allEscenarios.stream()
                .filter(escenario -> aplicarFiltros(escenario, request))
                .toList();
    }

    private boolean aplicarFiltros(Escenario escenario, DisponibilidadRequest request) {
        // Filtro por tipo
        if (request.getTipo() != null && !request.getTipo().trim().isEmpty()) {
            if (escenario.getTipo() == null || 
                !escenario.getTipo().getNombre().toLowerCase().contains(request.getTipo().toLowerCase())) {
                return false;
            }
        }
        
        // Filtro por nombre
        if (request.getNombre() != null && !request.getNombre().trim().isEmpty()) {
            if (escenario.getNombre() == null || 
                !escenario.getNombre().toLowerCase().contains(request.getNombre().toLowerCase())) {
                return false;
            }
        }
        
        // Filtro por ubicación
        if (request.getUbicacion() != null && !request.getUbicacion().trim().isEmpty()) {
            if (escenario.getUbicacion() == null || 
                escenario.getUbicacion().getNombre() == null ||
                !escenario.getUbicacion().getNombre().toLowerCase().contains(request.getUbicacion().toLowerCase())) {
                return false;
            }
        }
        
        return true;
    }

    private boolean verificarDisponibilidadEscenario(Long escenarioId, DisponibilidadRequest request) {
        // Verificar si hay reservas aprobadas en el rango de fechas
        return !reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
                escenarioId,
                request.getFechaInicio(),
                request.getFechaFin(),
                "APROBADA"
        );
    }
}
