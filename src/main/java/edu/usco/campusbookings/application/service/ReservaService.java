package edu.usco.campusbookings.application.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.request.VerificarDisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.application.exception.InvalidReservaException;
import edu.usco.campusbookings.application.exception.ReservaNotFoundException;
import edu.usco.campusbookings.application.mapper.ReservaMapper;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ReservaUseCase for managing reservations.
 * Handles business logic for reservation operations including creation, approval, rejection, and cancellation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaUseCase {

    private final ReservaPersistencePort reservaPersistencePort;
    private final ReservaMapper reservaMapper;
    private final UsuarioService usuarioService;

    @Override
    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request) {
        log.info("Creating new reservation request for escenario: {}", request.getEscenarioId());
        
        // Validate reservation times
        validarTiempoReserva(request.getFechaInicio(), request.getFechaFin());
        
        // Check escenario availability
        if (reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetween(
                request.getEscenarioId(), request.getFechaInicio(), request.getFechaFin())) {
            throw new InvalidReservaException("El escenario ya está reservado para el horario solicitado");
        }
        
        // Map the request to entity
        Reserva reserva = reservaMapper.toEntity(request);
        
        // Set initial state to PENDING_APPROVAL
        EstadoReserva estadoPendiente = reservaPersistencePort.findEstadoByNombre("PENDIENTE")
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado PENDIENTE en la base de datos"));
        reserva.setEstado(estadoPendiente);
        
        // Save the reservation
        Reserva savedReserva = reservaPersistencePort.save(reserva);
        log.info("Reservation created successfully with ID: {}", savedReserva.getId());
        
        return reservaMapper.toDto(savedReserva);
    }

    @Override
    @Transactional
    public ReservaResponse aprobarReserva(Long id) {
        Usuario currentUser = getCurrentUser();
        validarAdmin(currentUser);
        log.info("Admin {} approving reservation ID: {}", currentUser.getEmail(), id);
        
        return reservaPersistencePort.findById(id)
                .map(reserva -> {
                    validarTransicionEstado(reserva, "APROBADA");
                    EstadoReserva estadoAprobado = reservaPersistencePort.findEstadoByNombre("APROBADA")
                            .orElseThrow(() -> new IllegalStateException("No se encontró el estado APROBADA"));
                    
                    // Check for time conflicts with approved reservations
                    if (reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
                            reserva.getEscenario().getId(), 
                            reserva.getFechaInicio(), 
                            reserva.getFechaFin(),
                            "APROBADA")) {
                        throw new InvalidReservaException("No se puede aprobar la reserva. Existe un conflicto de horario con otra reserva aprobada.");
                    }
                    
                    reserva.setEstado(estadoAprobado);
                    Reserva updatedReserva = reservaPersistencePort.save(reserva);
                    log.info("Reservation ID: {} approved by admin: {}", id, currentUser.getEmail());
                    return reservaMapper.toDto(updatedReserva);
                })
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public ReservaResponse rechazarReserva(Long id) {
        Usuario currentUser = getCurrentUser();
        validarAdmin(currentUser);
        log.info("Admin {} rejecting reservation ID: {}", currentUser.getEmail(), id);
        
        return reservaPersistencePort.findById(id)
                .map(reserva -> {
                    validarTransicionEstado(reserva, "RECHAZADA");
                    EstadoReserva estadoRechazado = reservaPersistencePort.findEstadoByNombre("RECHAZADA")
                            .orElseThrow(() -> new IllegalStateException("No se encontró el estado RECHAZADA"));
                    
                    reserva.setEstado(estadoRechazado);
                    reserva.setMotivoRechazo("Rechazada por el administrador");
                    
                    Reserva updatedReserva = reservaPersistencePort.save(reserva);
                    log.info("Reservation ID: {} rejected by admin: {}", id, currentUser.getEmail());
                    return reservaMapper.toDto(updatedReserva);
                })
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public ReservaResponse cancelarReserva(Long id) {
        Usuario currentUser = getCurrentUser();
        log.info("User {} canceling reservation ID: {}", currentUser.getEmail(), id);
        
        return reservaPersistencePort.findById(id)
                .map(reserva -> {
                    // Solo el dueño de la reserva o un admin puede cancelarla
                    if (!reserva.getUsuario().getId().equals(currentUser.getId()) && 
                        !currentUser.getRol().getNombre().equals("ADMIN")) {
                        throw new AccessDeniedException("No tiene permisos para cancelar esta reserva");
                    }
                    
                    validarTransicionEstado(reserva, "CANCELADA");
                    EstadoReserva estadoCancelado = reservaPersistencePort.findEstadoByNombre("CANCELADA")
                            .orElseThrow(() -> new IllegalStateException("No se encontró el estado CANCELADA"));
                    
                    reserva.setEstado(estadoCancelado);
                    
                    Reserva updatedReserva = reservaPersistencePort.save(reserva);
                    log.info("Reservation ID: {} canceled by user: {}", id, currentUser.getEmail());
                    return reservaMapper.toDto(updatedReserva);
                })
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorUsuario(Long usuarioId) {
        Usuario currentUser = getCurrentUser();
        log.debug("Fetching reservations for user ID: {}", usuarioId);
        
        // Solo el propio usuario o un admin puede ver sus reservas
        if (!currentUser.getId().equals(usuarioId) && 
            !currentUser.getRol().getNombre().equals("ADMIN")) {
            throw new AccessDeniedException("No tiene permisos para ver estas reservas");
        }
        
        return reservaPersistencePort.findByUsuarioId(usuarioId).stream()
                .filter(reserva -> !reserva.getEstado().getNombre().equals("ELIMINADA"))
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorEscenario(Long escenarioId) {
        Usuario currentUser = getCurrentUser();
        log.debug("Fetching reservations for escenario ID: {}", escenarioId);
        
        // Solo los administradores pueden ver todas las reservas de un escenario
        if (!currentUser.getRol().getNombre().equals("ADMIN")) {
            throw new AccessDeniedException("Solo los administradores pueden ver las reservas de los escenarios");
        }
        
        return reservaPersistencePort.findByEscenarioId(escenarioId).stream()
                .filter(reserva -> !reserva.getEstado().getNombre().equals("ELIMINADA"))
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorEstado(String estadoNombre) {
        Usuario currentUser = getCurrentUser();
        log.debug("Fetching reservations with status: {}", estadoNombre);
        
        // Solo los administradores pueden filtrar por estado
        if (!currentUser.getRol().getNombre().equals("ADMIN")) {
            throw new AccessDeniedException("Solo los administradores pueden filtrar por estado");
        }
        
        return reservaPersistencePort.findByEstadoNombre(estadoNombre).stream()
                .filter(reserva -> !reserva.getEstado().getNombre().equals("ELIMINADA"))
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to change the status of a reservation.
     *
     * @param id           The ID of the reservation to update
     * @param estadoNombre The new status name
     * @return The updated reservation response
     * @throws ReservaNotFoundException if no reservation is found with the given ID
     */
    /**
     * Obtiene el usuario actual autenticado
     * @return Usuario autenticado
     * @throws AccessDeniedException si no hay usuario autenticado
     */
    private Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }
        
        // El principal es un UserDetails de Spring Security, obtenemos el email (username)
        String email = authentication.getName();
        if (email == null || email.isEmpty()) {
            throw new AccessDeniedException("No se pudo obtener el email del usuario autenticado");
        }
        
        // Buscar el usuario en la base de datos por email
        return usuarioService.findByEmail(email);
    }
    
    /**
     * Valida que el usuario sea administrador
     * @param usuario Usuario a validar
     * @throws AccessDeniedException si el usuario no es administrador
     */
    private void validarAdmin(Usuario usuario) {
        if (usuario == null || usuario.getRol() == null || 
            !usuario.getRol().getNombre().equals("ADMIN")) {
            log.warn("Intento de operación administrativa por usuario no autorizado: {}", 
                    usuario != null ? usuario.getEmail() : "null");
            throw new AccessDeniedException("Solo los administradores pueden realizar esta acción");
        }
    }
    
    /**
     * Valida que la transición de estado sea válida
     * @param reserva Reserva a validar
     * @param nuevoEstado Nombre del nuevo estado
     * @throws InvalidReservaException si la transición no es válida
     */
    private void validarTransicionEstado(Reserva reserva, String nuevoEstado) {
        if (reserva == null || reserva.getEstado() == null) {
            throw new InvalidReservaException("La reserva o su estado no pueden ser nulos");
        }
        
        String estadoActual = reserva.getEstado().getNombre();
        
        // Validar transiciones de estado permitidas
        switch (nuevoEstado) {
            case "APROBADA":
                if (!estadoActual.equals("PENDIENTE")) {
                    throw new InvalidReservaException("Solo se pueden aprobar reservas en estado PENDIENTE");
                }
                break;
            case "RECHAZADA":
                if (!estadoActual.equals("PENDIENTE")) {
                    throw new InvalidReservaException("Solo se pueden rechazar reservas en estado PENDIENTE");
                }
                break;
            case "CANCELADA":
                if (estadoActual.equals("CANCELADA") || estadoActual.equals("FINALIZADA")) {
                    throw new InvalidReservaException("No se puede cancelar una reserva " + estadoActual);
                }
                break;
            default:
                throw new InvalidReservaException("Transición de estado no soportada: " + estadoActual + " -> " + nuevoEstado);
        }
        
        // Validar que la reserva no esté vencida
        if (reserva.getFechaFin().isBefore(LocalDateTime.now())) {
            throw new InvalidReservaException("No se puede modificar una reserva pasada");
        }
    }
    
    /**
     * Valida que los tiempos de la reserva sean válidos
     * @param fechaInicio Fecha y hora de inicio
     * @param fechaFin Fecha y hora de fin
     * @throws InvalidReservaException si los tiempos no son válidos
     */
    private void validarTiempoReserva(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new InvalidReservaException("Las fechas de inicio y fin son requeridas");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (fechaInicio.isBefore(now)) {
            throw new InvalidReservaException("La fecha de inicio no puede ser en el pasado");
        }
        
        if (fechaFin.isBefore(fechaInicio)) {
            throw new InvalidReservaException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        // Validar que la reserva no exceda el tiempo máximo (4 horas)
        long horasReserva = java.time.Duration.between(fechaInicio, fechaFin).toHours();
        if (horasReserva > 4) {
            throw new InvalidReservaException("La duración máxima de la reserva es de 4 horas");
        }
        
        // Validar horario laboral (8am a 8pm)
        int horaInicio = fechaInicio.getHour();
        int horaFin = fechaFin.getHour();
        
        if (horaInicio < 8 || horaFin > 20 || (horaFin == 20 && fechaFin.getMinute() > 0)) {
            throw new InvalidReservaException("Las reservas solo están permitidas entre las 8:00 AM y 8:00 PM");
        }
        
        // Validar que la reserva no sea con más de 30 días de anticipación
        if (fechaInicio.isAfter(now.plusDays(30))) {
            throw new InvalidReservaException("Las reservas solo se pueden hacer con máximo 30 días de anticipación");
        }
     }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadResponse verificarDisponibilidad(VerificarDisponibilidadRequest request) {
        log.debug("Verificando disponibilidad para escenario: {} desde {} hasta {}", 
                 request.getEscenarioId(), request.getFechaInicio(), request.getFechaFin());

        // Primero validar que los datos básicos sean correctos
        try {
            validarTiempoReserva(request.getFechaInicio(), request.getFechaFin());
        } catch (InvalidReservaException e) {
            return DisponibilidadResponse.builder()
                    .disponible(false)
                    .escenarioId(request.getEscenarioId())
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .mensaje("Horario no válido: " + e.getMessage())
                    .build();
        }

        // Obtener información del escenario
        var escenario = reservaPersistencePort.findEscenarioById(request.getEscenarioId());
        if (escenario.isEmpty()) {
            return DisponibilidadResponse.builder()
                    .disponible(false)
                    .escenarioId(request.getEscenarioId())
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .mensaje("Escenario no encontrado")
                    .build();
        }

        String escenarioNombre = escenario.get().getNombre();

        // Verificar si el escenario está disponible (no deshabilitado)
        if (!escenario.get().getDisponible()) {
            return DisponibilidadResponse.builder()
                    .disponible(false)
                    .escenarioId(request.getEscenarioId())
                    .escenarioNombre(escenarioNombre)
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .mensaje("El escenario no está disponible para reservas en este momento")
                    .build();
        }

        // Buscar conflictos con reservas existentes (solo aprobadas)
        List<Reserva> reservasConflicto = reservaPersistencePort.findConflictingReservations(
                request.getEscenarioId(), 
                request.getFechaInicio(), 
                request.getFechaFin());

        if (reservasConflicto.isEmpty()) {
            // No hay conflictos, el horario está disponible
            return DisponibilidadResponse.builder()
                    .disponible(true)
                    .escenarioId(request.getEscenarioId())
                    .escenarioNombre(escenarioNombre)
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .mensaje("El escenario está disponible para el horario solicitado")
                    .build();
        } else {
            // Hay conflictos, construir información detallada
            List<DisponibilidadResponse.ConflictoReserva> conflictos = reservasConflicto.stream()
                    .map(reserva -> DisponibilidadResponse.ConflictoReserva.builder()
                            .reservaId(reserva.getId())
                            .fechaInicio(reserva.getFechaInicio())
                            .fechaFin(reserva.getFechaFin())
                            .usuarioNombre(reserva.getUsuario().getNombre())
                            .estado(reserva.getEstado().getNombre())
                            .build())
                    .collect(Collectors.toList());

            // Generar sugerencias de horarios alternativos
            List<DisponibilidadResponse.SugerenciaHorario> alternativas = 
                    generarHorariosAlternativos(request, reservasConflicto);

            return DisponibilidadResponse.builder()
                    .disponible(false)
                    .escenarioId(request.getEscenarioId())
                    .escenarioNombre(escenarioNombre)
                    .fechaInicio(request.getFechaInicio())
                    .fechaFin(request.getFechaFin())
                    .mensaje("El escenario no está disponible para el horario solicitado debido a conflictos existentes")
                    .conflictos(conflictos)
                    .alternativas(alternativas)
                    .build();
        }
    }

    /**
     * Genera sugerencias de horarios alternativos cuando el horario solicitado no está disponible.
     * Implementa lógica similar a Cal.com para proponer slots alternativos.
     */
    private List<DisponibilidadResponse.SugerenciaHorario> generarHorariosAlternativos(
            VerificarDisponibilidadRequest request, List<Reserva> reservasConflicto) {
        
        List<DisponibilidadResponse.SugerenciaHorario> alternativas = new ArrayList<>();
        LocalDateTime fechaBase = request.getFechaInicio().toLocalDate().atTime(8, 0); // Inicio del día laboral
        Duration duracionSolicitada = Duration.between(request.getFechaInicio(), request.getFechaFin());
        
        // Buscar slots disponibles el mismo día
        for (int hour = 8; hour <= 18; hour++) { // De 8 AM a 6 PM para permitir reservas hasta 8 PM
            LocalDateTime inicioSlot = fechaBase.withHour(hour);
            LocalDateTime finSlot = inicioSlot.plus(duracionSolicitada);
            
            // Verificar que el slot no exceda las 8 PM
            if (finSlot.getHour() > 20) {
                break;
            }
            
            // Verificar que no haya conflictos con este slot
            boolean hayConflicto = reservasConflicto.stream()
                    .anyMatch(reserva -> 
                        !(finSlot.isBefore(reserva.getFechaInicio()) || 
                          inicioSlot.isAfter(reserva.getFechaFin())));
            
            if (!hayConflicto) {
                alternativas.add(DisponibilidadResponse.SugerenciaHorario.builder()
                        .fechaInicio(inicioSlot)
                        .fechaFin(finSlot)
                        .descripcion("Disponible el mismo día de " + 
                                   inicioSlot.toLocalTime() + " a " + finSlot.toLocalTime())
                        .build());
                
                // Limitar a 3 sugerencias del mismo día
                if (alternativas.size() >= 3) {
                    break;
                }
            }
        }
        
        return alternativas;
    }
}
