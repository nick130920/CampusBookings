package edu.usco.campusbookings.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.DisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.CalendarioDisponibilidadResponse;
import edu.usco.campusbookings.application.port.input.CalendarioDisponibilidadUseCase;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarioDisponibilidadService implements CalendarioDisponibilidadUseCase {

    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final ReservaPersistencePort reservaPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public CalendarioDisponibilidadResponse consultarDisponibilidadCalendario(Long escenarioId, DisponibilidadRequest request) {
        log.info("Consultando disponibilidad de calendario para escenario ID: {}", escenarioId);
        
        // Buscar el escenario
        Escenario escenario = escenarioRepositoryPort.findById(escenarioId)
                .orElseThrow(() -> new RuntimeException("Escenario no encontrado con ID: " + escenarioId));
        
        // Generar lista de días en el rango
        List<LocalDate> diasEnRango = generarDiasEnRango(request.getFechaInicio().toLocalDate(), request.getFechaFin().toLocalDate());
        
        // Obtener disponibilidad por día
        List<CalendarioDisponibilidadResponse.DiaDisponibilidad> diasDisponibilidad = diasEnRango.stream()
                .map(dia -> generarDisponibilidadDia(escenarioId, dia, request.getFechaInicio().toLocalTime(), request.getFechaFin().toLocalTime()))
                .collect(Collectors.toList());
        
        return CalendarioDisponibilidadResponse.builder()
                .escenarioId(escenario.getId())
                .escenarioNombre(escenario.getNombre())
                .tipo(escenario.getTipo() != null ? escenario.getTipo().getNombre() : null)
                .ubicacion(escenario.getUbicacion() != null ? escenario.getUbicacion().getNombre() : null)
                .capacidad(escenario.getCapacidad())
                .descripcion(escenario.getDescripcion())
                .imagenUrl(escenario.getImagenUrl())
                .diasDisponibilidad(diasDisponibilidad)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarioDisponibilidadResponse> consultarDisponibilidadCalendarioMultiple(DisponibilidadRequest request) {
        log.info("Consultando disponibilidad de calendario para múltiples escenarios");
        
        // Obtener todos los escenarios que coincidan con los filtros
        List<Escenario> escenarios = escenarioRepositoryPort.findAll().stream()
                .filter(escenario -> filtrarEscenario(escenario, request))
                .collect(Collectors.toList());
        
        if (escenarios.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Generar disponibilidad para cada escenario
        return escenarios.stream()
                .map(escenario -> {
                    try {
                        return consultarDisponibilidadCalendario(escenario.getId(), request);
                    } catch (Exception e) {
                        log.error("Error consultando disponibilidad para escenario {}: {}", escenario.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    private boolean filtrarEscenario(Escenario escenario, DisponibilidadRequest request) {
        boolean matches = true;
        
        if (request.getTipo() != null && !request.getTipo().trim().isEmpty()) {
            matches = escenario.getTipo() != null && 
                    escenario.getTipo().getNombre().toLowerCase().contains(request.getTipo().toLowerCase());
        }
        
        if (request.getNombre() != null && !request.getNombre().trim().isEmpty() && matches) {
            matches = escenario.getNombre() != null && 
                    escenario.getNombre().toLowerCase().contains(request.getNombre().toLowerCase());
        }
        
        if (request.getUbicacion() != null && !request.getUbicacion().trim().isEmpty() && matches) {
            matches = escenario.getUbicacion() != null && 
                    escenario.getUbicacion().getNombre() != null &&
                    escenario.getUbicacion().getNombre().toLowerCase().contains(request.getUbicacion().toLowerCase());
        }
        
        return matches;
    }

    private List<LocalDate> generarDiasEnRango(LocalDate fechaInicio, LocalDate fechaFin) {
        List<LocalDate> dias = new ArrayList<>();
        LocalDate fecha = fechaInicio;
        
        while (!fecha.isAfter(fechaFin)) {
            dias.add(fecha);
            fecha = fecha.plusDays(1);
        }
        
        return dias;
    }

    private CalendarioDisponibilidadResponse.DiaDisponibilidad generarDisponibilidadDia(
            Long escenarioId, 
            LocalDate fecha, 
            LocalTime horaInicio, 
            LocalTime horaFin) {
        
        LocalDateTime fechaInicio = fecha.atTime(horaInicio);
        LocalDateTime fechaFin = fecha.atTime(horaFin);
        
        // Verificar si el día está en el pasado
        if (fecha.isBefore(LocalDate.now())) {
            return CalendarioDisponibilidadResponse.DiaDisponibilidad.builder()
                    .fecha(fecha)
                    .disponible(false)
                    .estado("NO_DISPONIBLE")
                    .reservas(new ArrayList<>())
                    .horariosDisponibles(new ArrayList<>())
                    .build();
        }
        
        // Buscar reservas existentes para este día
        List<Reserva> reservasDelDia = reservaPersistencePort.findConflictingReservations(escenarioId, fechaInicio, fechaFin);
        
        // Determinar disponibilidad
        boolean disponible = reservasDelDia.isEmpty();
        String estado = disponible ? "DISPONIBLE" : "RESERVADO";
        
        // Convertir reservas a DTO
        List<CalendarioDisponibilidadResponse.ReservaInfo> reservasInfo = reservasDelDia.stream()
                .map(this::convertirReservaAInfo)
                .collect(Collectors.toList());
        
        // Generar horarios disponibles (simplificado)
        List<String> horariosDisponibles = disponible ? generarHorariosDisponibles() : new ArrayList<>();
        
        return CalendarioDisponibilidadResponse.DiaDisponibilidad.builder()
                .fecha(fecha)
                .disponible(disponible)
                .estado(estado)
                .reservas(reservasInfo)
                .horariosDisponibles(horariosDisponibles)
                .build();
    }

    private CalendarioDisponibilidadResponse.ReservaInfo convertirReservaAInfo(Reserva reserva) {
        return CalendarioDisponibilidadResponse.ReservaInfo.builder()
                .reservaId(reserva.getId())
                .fechaInicio(reserva.getFechaInicio().toString())
                .fechaFin(reserva.getFechaFin().toString())
                .estado(reserva.getEstado().getNombre())
                .usuarioNombre(reserva.getUsuario().getNombre())
                .build();
    }

    private List<String> generarHorariosDisponibles() {
        // Horarios típicos de la universidad (simplificado)
        List<String> horarios = new ArrayList<>();
        String[] horas = {"08:00-10:00", "10:00-12:00", "14:00-16:00", "16:00-18:00", "18:00-20:00"};
        
        for (String hora : horas) {
            horarios.add(hora);
        }
        
        return horarios;
    }
} 