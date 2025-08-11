package edu.usco.campusbookings.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.OcupacionesDiaRequest;
import edu.usco.campusbookings.application.dto.request.OcupacionesMesRequest;
import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.request.VerificarDisponibilidadRequest;
import edu.usco.campusbookings.application.dto.response.DisponibilidadResponse;
import edu.usco.campusbookings.application.dto.response.OcupacionesDiaResponse;
import edu.usco.campusbookings.application.dto.response.OcupacionesMesResponse;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.application.exception.InvalidReservaException;
import edu.usco.campusbookings.application.exception.ReservaNotFoundException;
import edu.usco.campusbookings.application.mapper.ReservaMapper;
import edu.usco.campusbookings.application.port.input.AlertaReservaUseCase;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import edu.usco.campusbookings.application.port.output.EmailServicePort;
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
    private final EmailServicePort emailService;
    private final NotificationService notificationService;
    private final AlertaReservaUseCase alertaReservaUseCase;
    private final GoogleCalendarService googleCalendarService;

    @Override
    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request) {
        log.info("Creating new reservation request for escenario: {}", request.getEscenarioId());
        
        // Validate required fields
        if (request.getEscenarioId() == null) {
            throw new InvalidReservaException("El ID del escenario es obligatorio");
        }
        
        // Validate reservation times
        validarTiempoReserva(request.getFechaInicio(), request.getFechaFin());
        
        // Get current authenticated user
        Usuario usuarioActual = getCurrentUser();
        if (usuarioActual == null) {
            throw new InvalidReservaException("No se pudo obtener el usuario autenticado");
        }
        log.debug("Creating reservation for user: {} (ID: {})", usuarioActual.getEmail(), usuarioActual.getId());
        
        // Get and validate escenario
        var escenarioOpt = reservaPersistencePort.findEscenarioById(request.getEscenarioId());
        if (escenarioOpt.isEmpty()) {
            throw new InvalidReservaException("El escenario con ID " + request.getEscenarioId() + " no existe");
        }
        var escenario = escenarioOpt.get();
        
        if (!escenario.getDisponible()) {
            throw new InvalidReservaException("El escenario no está disponible para reservas");
        }
        log.debug("Validated escenario: {} (ID: {})", escenario.getNombre(), escenario.getId());
        
        // Check escenario availability for the requested time (only check APPROVED reservations)
        if (reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
                request.getEscenarioId(), request.getFechaInicio(), request.getFechaFin(), "APROBADA")) {
            throw new InvalidReservaException("El escenario ya tiene una reserva aprobada para el horario solicitado");
        }
        
        log.debug("Time slot available for new reservation (no approved conflicts found)");
        
        // Map the request to entity
        Reserva reserva = reservaMapper.toEntity(request);
        
        // Set required relationships manually (mapper ignores them)
        reserva.setUsuario(usuarioActual);
        reserva.setEscenario(escenario);
        
        // Set initial state to PENDING
        EstadoReserva estadoPendiente = reservaPersistencePort.findEstadoByNombre("PENDIENTE")
                .orElseThrow(() -> new IllegalStateException("No se encontró el estado PENDIENTE en la base de datos"));
        reserva.setEstado(estadoPendiente);
        
        // Final validation before saving
        if (reserva.getUsuario() == null || reserva.getEscenario() == null) {
            throw new IllegalStateException("Error interno: La reserva no puede guardarse sin usuario o escenario");
        }
        
        // Save the reservation
        Reserva savedReserva = reservaPersistencePort.save(reserva);
        log.info("Reservation created successfully with ID: {} for user: {} and escenario: {}", 
                savedReserva.getId(), usuarioActual.getEmail(), escenario.getNombre());
        
                    // Send notification emails and real-time notifications asynchronously
            try {
                // Email de confirmación al usuario
                emailService.enviarCorreoConfirmacionReserva(savedReserva);
                log.info("Confirmation email sent to user: {}", savedReserva.getUsuario().getEmail());
                
                // Email de notificación al administrador
                emailService.enviarCorreoNuevaReservaAdmin(savedReserva);
                log.info("Admin notification email sent for reservation ID: {}", savedReserva.getId());
                
                // Notificación en tiempo real al administrador
                notificationService.notificarNuevaReservaAdmin(savedReserva);
                log.info("Real-time admin notification sent for reservation ID: {}", savedReserva.getId());
                
            } catch (Exception e) {
                log.error("Error sending notifications for reservation ID: {}", savedReserva.getId(), e);
                // No lanzamos excepción para no afectar la creación de la reserva
            }

            // Crear alertas automáticas para la nueva reserva
            try {
                alertaReservaUseCase.crearAlertasParaReserva(savedReserva);
                log.info("Automatic alerts created for reservation ID: {}", savedReserva.getId());
            } catch (Exception e) {
                log.error("Error creating alerts for reservation ID: {}", savedReserva.getId(), e);
                // No lanzamos excepción para no afectar la creación de la reserva
            }

            // Sincronizar con Google Calendar si el usuario está conectado
            try {
                String eventId = googleCalendarService.syncReservationWithCalendar(savedReserva);
                if (eventId != null) {
                    savedReserva.setGoogleCalendarEventId(eventId);
                    reservaPersistencePort.save(savedReserva);
                    log.info("Reservation synced with Google Calendar, event ID: {}", eventId);
                }
            } catch (Exception e) {
                log.error("Error syncing reservation with Google Calendar for reservation ID: {}", savedReserva.getId(), e);
                // No lanzamos excepción para no afectar la creación de la reserva
            }
        
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
                    
                    // Auto-reject competing pending reservations for the same time slot
                    autoRejectCompetingReservations(updatedReserva);
                    
                    // Send approval notifications (email + real-time)
                    try {
                        emailService.enviarCorreoAprobacionReserva(updatedReserva);
                        log.info("Approval email sent to user: {} for reservation ID: {}", 
                                updatedReserva.getUsuario().getEmail(), updatedReserva.getId());
                        
                        // Notificación en tiempo real al usuario
                        notificationService.notificarReservaAprobada(updatedReserva);
                        log.info("Real-time approval notification sent to user: {} for reservation ID: {}", 
                                updatedReserva.getUsuario().getEmail(), updatedReserva.getId());
                        
                    } catch (Exception e) {
                        log.error("Error sending approval notifications for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar la aprobación
                    }

                    // Sincronizar con Google Calendar al aprobar
                    try {
                        String eventId = googleCalendarService.syncReservationWithCalendar(updatedReserva);
                        if (eventId != null) {
                            updatedReserva.setGoogleCalendarEventId(eventId);
                            reservaPersistencePort.save(updatedReserva);
                            log.info("Approved reservation synced with Google Calendar, event ID: {}", eventId);
                        }
                    } catch (Exception e) {
                        log.error("Error syncing approved reservation with Google Calendar for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar la aprobación
                    }
                    
                    return reservaMapper.toDto(updatedReserva);
                })
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public ReservaResponse rechazarReserva(Long id, String motivo) {
        Usuario currentUser = getCurrentUser();
        validarAdmin(currentUser);
        log.info("Admin {} rejecting reservation ID: {} with reason: {}", currentUser.getEmail(), id, motivo);
        
        return reservaPersistencePort.findById(id)
                .map(reserva -> {
                    validarTransicionEstado(reserva, "RECHAZADA");
                    EstadoReserva estadoRechazado = reservaPersistencePort.findEstadoByNombre("RECHAZADA")
                            .orElseThrow(() -> new IllegalStateException("No se encontró el estado RECHAZADA"));
                    
                    reserva.setEstado(estadoRechazado);
                    reserva.setMotivoRechazo(motivo);
                    
                    Reserva updatedReserva = reservaPersistencePort.save(reserva);
                    log.info("Reservation ID: {} rejected by admin: {} with reason: {}", id, currentUser.getEmail(), motivo);
                    
                    // Send rejection notifications (email + real-time)
                    try {
                        emailService.enviarCorreoRechazoReserva(updatedReserva);
                        log.info("Rejection email sent to user: {} for reservation ID: {}", 
                                updatedReserva.getUsuario().getEmail(), updatedReserva.getId());
                        
                        // Notificación en tiempo real al usuario
                        notificationService.notificarReservaRechazada(updatedReserva);
                        log.info("Real-time rejection notification sent to user: {} for reservation ID: {}", 
                                updatedReserva.getUsuario().getEmail(), updatedReserva.getId());
                        
                    } catch (Exception e) {
                        log.error("Error sending rejection notifications for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar el rechazo
                    }
                    
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
                    
                    // Send cancellation notification email to user
                    try {
                        emailService.enviarCorreoCancelacionReserva(updatedReserva);
                        log.info("Cancellation email sent to user: {} for reservation ID: {}", 
                                updatedReserva.getUsuario().getEmail(), updatedReserva.getId());
                    } catch (Exception e) {
                        log.error("Error sending cancellation email for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar la cancelación
                    }

                    // Eliminar alertas asociadas a la reserva cancelada
                    try {
                        alertaReservaUseCase.eliminarAlertasDeReservaCancelada(updatedReserva.getId());
                        log.info("Alerts canceled for reservation ID: {}", updatedReserva.getId());
                    } catch (Exception e) {
                        log.error("Error canceling alerts for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar la cancelación
                    }

                    // Eliminar evento de Google Calendar si existe
                    try {
                        if (updatedReserva.getGoogleCalendarEventId() != null) {
                            googleCalendarService.deleteEventFromCalendar(updatedReserva.getGoogleCalendarEventId());
                            updatedReserva.setGoogleCalendarEventId(null);
                            reservaPersistencePort.save(updatedReserva);
                            log.info("Google Calendar event deleted for canceled reservation ID: {}", updatedReserva.getId());
                        }
                    } catch (Exception e) {
                        log.error("Error deleting Google Calendar event for reservation ID: {}", updatedReserva.getId(), e);
                        // No lanzamos excepción para no afectar la cancelación
                    }
                    
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
    
    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerTodasLasReservas() {
        Usuario currentUser = getCurrentUser();
        log.debug("Admin {} fetching all reservations", currentUser.getEmail());
        
        // Solo los administradores pueden ver todas las reservas
        if (!currentUser.getRol().getNombre().equals("ADMIN")) {
            throw new AccessDeniedException("Solo los administradores pueden ver todas las reservas");
        }
        
        // Logging temporal para diagnóstico de fechas
        log.info("TIMEZONE DIAGNOSTIC - Sistema: {}", java.util.TimeZone.getDefault().getID());
        log.info("TIMEZONE DIAGNOSTIC - ZoneId: {}", java.time.ZoneId.systemDefault().getId());
        log.info("TIMEZONE DIAGNOSTIC - Hora actual: {}", LocalDateTime.now());
        
        List<Reserva> reservas = reservaPersistencePort.findAll();
        log.info("TIMEZONE DIAGNOSTIC - Encontradas {} reservas", reservas.size());
        
        // Log de la primera reserva para diagnóstico
        if (!reservas.isEmpty()) {
            Reserva primeraReserva = reservas.get(0);
            log.info("TIMEZONE DIAGNOSTIC - Primera reserva ID: {}", primeraReserva.getId());
            log.info("TIMEZONE DIAGNOSTIC - CreatedDate BD: {}", primeraReserva.getCreatedDate());
            log.info("TIMEZONE DIAGNOSTIC - ModifiedDate BD: {}", primeraReserva.getModifiedDate());
            log.info("TIMEZONE DIAGNOSTIC - FechaInicio BD: {}", primeraReserva.getFechaInicio());
            
            // Mapear y ver qué sale
            ReservaResponse response = reservaMapper.toDto(primeraReserva);
            log.info("TIMEZONE DIAGNOSTIC - FechaCreacion DTO: {}", response.getFechaCreacion());
            log.info("TIMEZONE DIAGNOSTIC - FechaActualizacion DTO: {}", response.getFechaActualizacion());
        }
        
        return reservas.stream()
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
    
    /**
     * Rechaza automáticamente todas las reservas PENDIENTES que compiten con una reserva recién aprobada.
     * Este método implementa la lógica de "competencia justa" donde múltiples usuarios pueden solicitar
     * el mismo horario, pero solo una reserva puede ser aprobada.
     */
    private void autoRejectCompetingReservations(Reserva approvedReserva) {
        log.info("Auto-rejecting competing reservations for escenario: {} at time: {} - {}", 
                approvedReserva.getEscenario().getId(), 
                approvedReserva.getFechaInicio(), 
                approvedReserva.getFechaFin());
        
        try {
            // Find all conflicting reservations that are still PENDING
            List<Reserva> conflictingReservations = reservaPersistencePort.findConflictingReservations(
                    approvedReserva.getEscenario().getId(),
                    approvedReserva.getFechaInicio(),
                    approvedReserva.getFechaFin()
            );
            
            // Filter to only PENDING reservations (excluding the one we just approved)
            List<Reserva> pendingConflicts = conflictingReservations.stream()
                    .filter(r -> !r.getId().equals(approvedReserva.getId()))
                    .filter(r -> "PENDIENTE".equals(r.getEstado().getNombre()))
                    .toList();
            
            if (pendingConflicts.isEmpty()) {
                log.info("No competing pending reservations found for escenario: {} at the approved time slot", 
                        approvedReserva.getEscenario().getId());
                return;
            }
            
            // Get REJECTED state
            EstadoReserva estadoRechazado = reservaPersistencePort.findEstadoByNombre("RECHAZADA")
                    .orElseThrow(() -> new IllegalStateException("No se encontró el estado RECHAZADA"));
            
            // Auto-reject each competing reservation
            for (Reserva competingReserva : pendingConflicts) {
                competingReserva.setEstado(estadoRechazado);
                competingReserva.setMotivoRechazo(String.format(
                    "Reserva rechazada automáticamente. El horario fue asignado a otra solicitud (ID: %d) que fue aprobada primero.",
                    approvedReserva.getId()
                ));
                
                Reserva rejectedReserva = reservaPersistencePort.save(competingReserva);
                
                log.info("Auto-rejected competing reservation ID: {} for user: {}", 
                        rejectedReserva.getId(), rejectedReserva.getUsuario().getEmail());
                
                // Send rejection notifications (email + real-time)
                try {
                    emailService.enviarCorreoRechazoReserva(rejectedReserva);
                    log.info("Auto-rejection email sent to user: {} for reservation ID: {}", 
                            rejectedReserva.getUsuario().getEmail(), rejectedReserva.getId());
                    
                    // Notificación en tiempo real al usuario sobre auto-rechazo
                    notificationService.notificarReservaAutoRechazada(rejectedReserva, approvedReserva.getId());
                    log.info("Real-time auto-rejection notification sent to user: {} for reservation ID: {}", 
                            rejectedReserva.getUsuario().getEmail(), rejectedReserva.getId());
                    
                } catch (Exception e) {
                    log.error("Error sending auto-rejection notifications for reservation ID: {}", rejectedReserva.getId(), e);
                    // No lanzar excepción para no afectar el proceso principal
                }
            }
            
            log.info("Auto-rejected {} competing reservations for approved reservation ID: {}", 
                    pendingConflicts.size(), approvedReserva.getId());
            
        } catch (Exception e) {
            log.error("Error during auto-rejection of competing reservations for approved reservation ID: {}", 
                    approvedReserva.getId(), e);
            // No lanzar excepción para no afectar la aprobación principal
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OcupacionesDiaResponse obtenerOcupacionesDia(OcupacionesDiaRequest request) {
        log.debug("Obteniendo ocupaciones para escenario: {} en fecha: {}", 
                 request.getEscenarioId(), request.getFecha());

        // Verificar que el escenario existe
        var escenario = reservaPersistencePort.findEscenarioById(request.getEscenarioId());
        if (escenario.isEmpty()) {
            throw new IllegalArgumentException("Escenario no encontrado con ID: " + request.getEscenarioId());
        }

        // Calcular el rango de tiempo para todo el día
        LocalDateTime inicioDelDia = request.getFecha().atTime(LocalTime.MIN);
        LocalDateTime finDelDia = request.getFecha().atTime(LocalTime.MAX);

        // Obtener todas las reservas activas (aprobadas y pendientes) para el día
        List<Reserva> reservasDelDia = reservaPersistencePort.findByEscenarioIdAndFechaRange(
                request.getEscenarioId(), inicioDelDia, finDelDia);

        // Filtrar solo las reservas que están aprobadas o pendientes de aprobación
        List<Reserva> reservasActivas = reservasDelDia.stream()
                .filter(reserva -> {
                    String estadoNombre = reserva.getEstado().getNombre();
                    return "APROBADA".equals(estadoNombre) || "PENDIENTE".equals(estadoNombre);
                })
                .collect(Collectors.toList());

        // Convertir a bloques ocupados
        List<OcupacionesDiaResponse.BloqueOcupado> bloquesOcupados = reservasActivas.stream()
                .map(reserva -> OcupacionesDiaResponse.BloqueOcupado.builder()
                        .horaInicio(reserva.getFechaInicio())
                        .horaFin(reserva.getFechaFin())
                        .motivo(String.format("Reserva de %s %s", 
                                reserva.getUsuario().getNombre(), 
                                reserva.getUsuario().getApellido()))
                        .estado(reserva.getEstado().getNombre())
                        .reservaId(reserva.getId())
                        .build())
                .collect(Collectors.toList());

        log.debug("Encontrados {} bloques ocupados para escenario {} en fecha {}", 
                 bloquesOcupados.size(), request.getEscenarioId(), request.getFecha());

        return OcupacionesDiaResponse.builder()
                .escenarioId(request.getEscenarioId())
                .escenarioNombre(escenario.get().getNombre())
                .fecha(request.getFecha())
                .bloquesOcupados(bloquesOcupados)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OcupacionesMesResponse obtenerOcupacionesMes(OcupacionesMesRequest request) {
        log.debug("Obteniendo ocupaciones para escenario: {} en mes: {}/{}", 
                 request.getEscenarioId(), request.getMes(), request.getAño());

        // Verificar que el escenario existe
        var escenario = reservaPersistencePort.findEscenarioById(request.getEscenarioId());
        if (escenario.isEmpty()) {
            throw new IllegalArgumentException("Escenario no encontrado con ID: " + request.getEscenarioId());
        }

        // Calcular el rango de tiempo para todo el mes
        LocalDateTime inicioDelMes = LocalDateTime.of(request.getAño(), request.getMes(), 1, 0, 0, 0);
        LocalDateTime finDelMes = inicioDelMes.plusMonths(1).minusSeconds(1);

        // Obtener todas las reservas activas (aprobadas y pendientes) para el mes
        List<Reserva> reservasDelMes = reservaPersistencePort.findByEscenarioIdAndFechaRange(
                request.getEscenarioId(), inicioDelMes, finDelMes);

        // Filtrar solo las reservas que están aprobadas o pendientes de aprobación
        List<Reserva> reservasActivas = reservasDelMes.stream()
                .filter(reserva -> {
                    String estadoNombre = reserva.getEstado().getNombre();
                    return "APROBADA".equals(estadoNombre) || "PENDIENTE".equals(estadoNombre);
                })
                .collect(Collectors.toList());

        // Convertir a bloques ocupados
        List<OcupacionesMesResponse.BloqueOcupado> bloquesOcupados = reservasActivas.stream()
                .map(reserva -> {
                    int diaDelMes = reserva.getFechaInicio().getDayOfMonth();
                    return OcupacionesMesResponse.BloqueOcupado.builder()
                            .horaInicio(reserva.getFechaInicio())
                            .horaFin(reserva.getFechaFin())
                            .motivo(String.format("Reserva de %s %s", 
                                    reserva.getUsuario().getNombre(), 
                                    reserva.getUsuario().getApellido()))
                            .estado(reserva.getEstado().getNombre())
                            .reservaId(reserva.getId())
                            .diaDelMes(diaDelMes)
                            .build();
                })
                .collect(Collectors.toList());

        // Agrupar por día del mes para facilitar el acceso desde el frontend
        Map<Integer, List<OcupacionesMesResponse.BloqueOcupado>> ocupacionesPorDia = bloquesOcupados.stream()
                .collect(Collectors.groupingBy(OcupacionesMesResponse.BloqueOcupado::getDiaDelMes));

        log.debug("Encontrados {} bloques ocupados distribuidos en {} días para escenario {} en {}/{}", 
                 bloquesOcupados.size(), ocupacionesPorDia.size(), request.getEscenarioId(), request.getMes(), request.getAño());

        return OcupacionesMesResponse.builder()
                .escenarioId(request.getEscenarioId())
                .escenarioNombre(escenario.get().getNombre())
                .año(request.getAño())
                .mes(request.getMes())
                .ocupacionesPorDia(ocupacionesPorDia)
                .todasLasOcupaciones(bloquesOcupados)
                .build();
    }
}
