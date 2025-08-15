package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ReservaRecurrenteRequest;
import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResponse;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResumeResponse;
import edu.usco.campusbookings.application.exception.InvalidReservaException;
import edu.usco.campusbookings.application.exception.ReservaNotFoundException;
import edu.usco.campusbookings.application.mapper.ReservaRecurrenteMapper;
import edu.usco.campusbookings.application.port.input.ReservaRecurrenteUseCase;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import edu.usco.campusbookings.application.port.output.ReservaRecurrentePersistencePort;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaRecurrenteService implements ReservaRecurrenteUseCase {

    private final ReservaRecurrentePersistencePort reservaRecurrentePersistencePort;
    private final ReservaPersistencePort reservaPersistencePort;
    private final ReservaRecurrenteMapper mapper;
    private final ReservaUseCase reservaUseCase;
    private final UsuarioService usuarioService;

    @Override
    @Transactional(readOnly = true)
    public ReservaRecurrenteResumeResponse previsualizarReservasRecurrentes(ReservaRecurrenteRequest request) {
        log.info("Previsualizando reservas recurrentes para escenario: {}", request.getEscenarioId());
        
        validarRequest(request);
        
        // Validar que el escenario exista
        Escenario escenario = validarYObtenerEscenario(request.getEscenarioId());
        
        // Calcular todas las fechas que se generarían
        List<LocalDate> fechasCalculadas = calcularFechasParaPatron(request);
        
        // Verificar conflictos para cada fecha
        List<ReservaRecurrenteResumeResponse.FechaReservaResumen> fechasResumen = new ArrayList<>();
        List<String> conflictos = new ArrayList<>();
        
        for (LocalDate fecha : fechasCalculadas) {
            var fechaHoraInicio = fecha.atTime(request.getHoraInicio());
            var fechaHoraFin = fecha.atTime(request.getHoraFin());
            
            boolean tieneConflicto = reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
                request.getEscenarioId(), fechaHoraInicio, fechaHoraFin, "APROBADA");
            
            String detalleConflicto = null;
            if (tieneConflicto) {
                detalleConflicto = "Reserva aprobada existente";
                conflictos.add(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                              " - " + detalleConflicto);
            }
            
            fechasResumen.add(ReservaRecurrenteResumeResponse.FechaReservaResumen.builder()
                .fecha(fecha)
                .diaSemana(obtenerNombreDiaSemana(fecha))
                .tieneConflicto(tieneConflicto)
                .detalleConflicto(detalleConflicto)
                .build());
        }
        
        // Generar advertencias
        List<String> advertencias = generarAdvertencias(request, fechasCalculadas.size(), conflictos.size());
        
        return ReservaRecurrenteResumeResponse.builder()
            .patron(request.getPatron())
            .descripcionPatron(generarDescripcionPatron(request))
            .totalReservasAGenerar(fechasCalculadas.size())
            .fechasReservas(fechasResumen)
            .conflictos(conflictos)
            .advertencias(advertencias)
            .build();
    }

    @Override
    @Transactional
    public ReservaRecurrenteResponse crearReservaRecurrente(ReservaRecurrenteRequest request) {
        log.info("Creando reserva recurrente para usuario autenticado");
        
        validarRequest(request);
        
        // Obtener usuario autenticado
        Usuario usuarioActual = getCurrentUser();
        
        // Validar que el escenario exista y esté disponible
        Escenario escenario = validarYObtenerEscenario(request.getEscenarioId());
        
        // Mapear y crear la entidad
        ReservaRecurrente reservaRecurrente = mapper.toEntity(request);
        reservaRecurrente.setUsuario(usuarioActual);
        reservaRecurrente.setEscenario(escenario);
        
        // Guardar la configuración recurrente
        ReservaRecurrente savedReservaRecurrente = reservaRecurrentePersistencePort.save(reservaRecurrente);
        log.info("Reserva recurrente creada con ID: {}", savedReservaRecurrente.getId());
        
        // Generar reservas iniciales (primeras 30 días o hasta 10 reservas)
        LocalDate fechaLimite = LocalDate.now().plusDays(30);
        if (fechaLimite.isAfter(savedReservaRecurrente.getFechaFin())) {
            fechaLimite = savedReservaRecurrente.getFechaFin();
        }
        
        List<Long> reservasGeneradas = generarReservasHastaFecha(savedReservaRecurrente.getId(), fechaLimite);
        log.info("Generadas {} reservas iniciales para configuración recurrente ID: {}", 
                reservasGeneradas.size(), savedReservaRecurrente.getId());
        
        return mapper.toResponse(savedReservaRecurrente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaRecurrenteResponse> obtenerReservasRecurrentesPorUsuario(Long usuarioId) {
        Usuario usuarioActual = getCurrentUser();
        
        // Solo admin o el propio usuario pueden ver sus reservas recurrentes
        if (!esAdmin() && !usuarioActual.getId().equals(usuarioId)) {
            throw new AccessDeniedException("No tiene permisos para ver las reservas recurrentes de este usuario");
        }
        
        List<ReservaRecurrente> reservas = reservaRecurrentePersistencePort.findByUsuarioId(usuarioId);
        return mapper.toResponseList(reservas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaRecurrenteResponse> obtenerTodasLasReservasRecurrentes() {
        validarAccesoAdmin();
        
        List<ReservaRecurrente> reservas = reservaRecurrentePersistencePort.findByActiva(true);
        return mapper.toResponseList(reservas);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaRecurrenteResponse obtenerReservaRecurrentePorId(Long id) {
        ReservaRecurrente reserva = reservaRecurrentePersistencePort.findById(id)
            .orElseThrow(() -> new ReservaNotFoundException("Reserva recurrente no encontrada con ID: " + id));
        
        Usuario usuarioActual = getCurrentUser();
        
        // Solo admin o el propietario pueden ver la reserva recurrente
        if (!esAdmin() && !reserva.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No tiene permisos para ver esta reserva recurrente");
        }
        
        return mapper.toResponse(reserva);
    }

    @Override
    @Transactional
    public ReservaRecurrenteResponse actualizarReservaRecurrente(Long id, ReservaRecurrenteRequest request) {
        ReservaRecurrente reservaExistente = reservaRecurrentePersistencePort.findById(id)
            .orElseThrow(() -> new ReservaNotFoundException("Reserva recurrente no encontrada con ID: " + id));
        
        Usuario usuarioActual = getCurrentUser();
        
        // Solo admin o el propietario pueden actualizar la reserva recurrente
        if (!esAdmin() && !reservaExistente.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No tiene permisos para actualizar esta reserva recurrente");
        }
        
        validarRequest(request);
        
        // Actualizar campos
        reservaExistente.setPatron(request.getPatron());
        reservaExistente.setFechaInicio(request.getFechaInicio());
        reservaExistente.setFechaFin(request.getFechaFin());
        reservaExistente.setHoraInicio(request.getHoraInicio());
        reservaExistente.setHoraFin(request.getHoraFin());
        reservaExistente.setObservaciones(request.getObservaciones());
        reservaExistente.setDiaMes(request.getDiaMes());
        reservaExistente.setIntervaloRepeticion(request.getIntervaloRepeticion());
        reservaExistente.setMaxReservas(request.getMaxReservas());
        
        if (request.getDiasSemana() != null) {
            reservaExistente.setDiasSemana(
                request.getDiasSemana().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",", "[", "]"))
            );
        }
        
        ReservaRecurrente updated = reservaRecurrentePersistencePort.save(reservaExistente);
        log.info("Reserva recurrente actualizada: ID {}", id);
        
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ReservaRecurrenteResponse desactivarReservaRecurrente(Long id) {
        ReservaRecurrente reserva = reservaRecurrentePersistencePort.findById(id)
            .orElseThrow(() -> new ReservaNotFoundException("Reserva recurrente no encontrada con ID: " + id));
        
        validarAccesoReservaRecurrente(reserva);
        
        reserva.setActiva(false);
        ReservaRecurrente updated = reservaRecurrentePersistencePort.save(reserva);
        log.info("Reserva recurrente desactivada: ID {}", id);
        
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ReservaRecurrenteResponse activarReservaRecurrente(Long id) {
        ReservaRecurrente reserva = reservaRecurrentePersistencePort.findById(id)
            .orElseThrow(() -> new ReservaNotFoundException("Reserva recurrente no encontrada con ID: " + id));
        
        validarAccesoReservaRecurrente(reserva);
        
        reserva.setActiva(true);
        ReservaRecurrente updated = reservaRecurrentePersistencePort.save(reserva);
        log.info("Reserva recurrente activada: ID {}", id);
        
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void eliminarReservaRecurrente(Long id, boolean eliminarReservasFuturas) {
        ReservaRecurrente reserva = reservaRecurrentePersistencePort.findById(id)
            .orElseThrow(() -> new ReservaNotFoundException("Reserva recurrente no encontrada con ID: " + id));
        
        validarAccesoReservaRecurrente(reserva);
        
        // TODO: Implementar eliminación de reservas futuras si se solicita
        if (eliminarReservasFuturas) {
            log.info("Eliminando reservas futuras para configuración recurrente ID: {}", id);
            // Implementar lógica para eliminar reservas futuras
        }
        
        reservaRecurrentePersistencePort.deleteById(id);
        log.info("Reserva recurrente eliminada: ID {}", id);
    }

    @Override
    @Transactional
    public void generarReservasPendientes() {
        log.info("Iniciando generación de reservas pendientes");
        
        List<ReservaRecurrente> configuracionesActivas = reservaRecurrentePersistencePort.findActivasParaGeneracion();
        log.info("Encontradas {} configuraciones activas para generación", configuracionesActivas.size());
        
        for (ReservaRecurrente config : configuracionesActivas) {
            try {
                LocalDate fechaLimite = LocalDate.now().plusDays(7); // Generar para próximos 7 días
                if (fechaLimite.isAfter(config.getFechaFin())) {
                    fechaLimite = config.getFechaFin();
                }
                
                List<Long> nuevasReservas = generarReservasHastaFecha(config.getId(), fechaLimite);
                log.info("Generadas {} nuevas reservas para configuración ID: {}", 
                        nuevasReservas.size(), config.getId());
                
            } catch (Exception e) {
                log.error("Error generando reservas para configuración ID: {} - {}", 
                         config.getId(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    public List<Long> generarReservasHastaFecha(Long reservaRecurrenteId, LocalDate fechaLimite) {
        ReservaRecurrente config = reservaRecurrentePersistencePort.findById(reservaRecurrenteId)
            .orElseThrow(() -> new ReservaNotFoundException("Configuración recurrente no encontrada"));
        
        if (!config.getActiva()) {
            log.warn("Intentando generar reservas para configuración inactiva ID: {}", reservaRecurrenteId);
            return new ArrayList<>();
        }
        
        List<Long> reservasGeneradas = new ArrayList<>();
        List<LocalDate> fechasAGenerar = calcularFechasParaGeneracion(config, fechaLimite);
        
        for (LocalDate fecha : fechasAGenerar) {
            try {
                // Verificar si ya existe una reserva para esta fecha
                var fechaHoraInicio = fecha.atTime(config.getHoraInicio());
                var fechaHoraFin = fecha.atTime(config.getHoraFin());
                
                boolean yaExiste = reservaPersistencePort.existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
                    config.getEscenario().getId(), fechaHoraInicio, fechaHoraFin, "APROBADA");
                
                if (!yaExiste) {
                    // Crear la reserva
                    ReservaRequest reservaRequest = ReservaRequest.builder()
                        .escenarioId(config.getEscenario().getId())
                        .fechaInicio(fechaHoraInicio)
                        .fechaFin(fechaHoraFin)
                        .observaciones("Reserva generada automáticamente - " + 
                                     (config.getObservaciones() != null ? config.getObservaciones() : ""))
                        .build();
                    
                    // Crear la reserva en el contexto del usuario propietario de la configuración
                    var reservaResponse = reservaUseCase.crearReserva(reservaRequest);
                    reservasGeneradas.add(reservaResponse.getId());
                    
                    log.debug("Reserva generada automáticamente: ID {} para fecha {}", 
                             reservaResponse.getId(), fecha);
                }
                
            } catch (Exception e) {
                log.error("Error generando reserva para fecha {} de configuración ID: {} - {}", 
                         fecha, reservaRecurrenteId, e.getMessage());
            }
        }
        
        log.info("Proceso completado. Generadas {} reservas para configuración ID: {}", 
                reservasGeneradas.size(), reservaRecurrenteId);
        
        return reservasGeneradas;
    }

    // Métodos privados de validación y utilidad
    
    private void validarRequest(ReservaRecurrenteRequest request) {
        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new InvalidReservaException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        
        if (request.getHoraFin().isBefore(request.getHoraInicio()) || 
            request.getHoraFin().equals(request.getHoraInicio())) {
            throw new InvalidReservaException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        if (request.getPatron() == ReservaRecurrente.PatronRecurrencia.SEMANAL && 
            (request.getDiasSemana() == null || request.getDiasSemana().isEmpty())) {
            throw new InvalidReservaException("Para patrón semanal debe especificar al menos un día de la semana");
        }
        
        if (request.getPatron() == ReservaRecurrente.PatronRecurrencia.MENSUAL && 
            request.getDiaMes() == null) {
            throw new InvalidReservaException("Para patrón mensual debe especificar el día del mes");
        }
    }
    
    private Escenario validarYObtenerEscenario(Long escenarioId) {
        return reservaPersistencePort.findEscenarioById(escenarioId)
            .orElseThrow(() -> new InvalidReservaException("Escenario no encontrado con ID: " + escenarioId));
    }
    
    private Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioService.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));
    }
    
    private boolean esAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
    
    private void validarAccesoAdmin() {
        if (!esAdmin()) {
            throw new AccessDeniedException("Se requieren permisos de administrador");
        }
    }
    
    private void validarAccesoReservaRecurrente(ReservaRecurrente reserva) {
        Usuario usuarioActual = getCurrentUser();
        if (!esAdmin() && !reserva.getUsuario().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("No tiene permisos para modificar esta reserva recurrente");
        }
    }
    
    private List<LocalDate> calcularFechasParaPatron(ReservaRecurrenteRequest request) {
        List<LocalDate> fechas = new ArrayList<>();
        LocalDate fechaActual = request.getFechaInicio();
        int maxReservas = request.getMaxReservas() != null ? request.getMaxReservas() : 365;
        
        while (!fechaActual.isAfter(request.getFechaFin()) && fechas.size() < maxReservas) {
            boolean coincide = switch (request.getPatron()) {
                case DIARIO -> true;
                case SEMANAL -> request.getDiasSemana() != null && 
                              request.getDiasSemana().contains(fechaActual.getDayOfWeek().getValue());
                case MENSUAL -> request.getDiaMes() != null && 
                              fechaActual.getDayOfMonth() == request.getDiaMes();
                case PERSONALIZADO -> true; // Implementar lógica personalizada si se necesita
            };
            
            if (coincide) {
                fechas.add(fechaActual);
            }
            
            fechaActual = fechaActual.plusDays(1);
        }
        
        return fechas;
    }
    
    private List<LocalDate> calcularFechasParaGeneracion(ReservaRecurrente config, LocalDate fechaLimite) {
        List<LocalDate> fechas = new ArrayList<>();
        LocalDate fechaActual = LocalDate.now();
        
        // Si la fecha actual es anterior al inicio de la configuración, comenzar desde fechaInicio
        if (fechaActual.isBefore(config.getFechaInicio())) {
            fechaActual = config.getFechaInicio();
        }
        
        while (!fechaActual.isAfter(fechaLimite) && !fechaActual.isAfter(config.getFechaFin())) {
            if (config.coincideConPatron(fechaActual)) {
                fechas.add(fechaActual);
            }
            fechaActual = fechaActual.plusDays(1);
        }
        
        return fechas;
    }
    
    private String generarDescripcionPatron(ReservaRecurrenteRequest request) {
        return switch (request.getPatron()) {
            case DIARIO -> "Todos los días";
            case SEMANAL -> "Patrón semanal";
            case MENSUAL -> "Patrón mensual";
            case PERSONALIZADO -> "Patrón personalizado";
        };
    }
    
    private String obtenerNombreDiaSemana(LocalDate fecha) {
        return switch (fecha.getDayOfWeek()) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
    
    private List<String> generarAdvertencias(ReservaRecurrenteRequest request, int totalReservas, int conflictos) {
        List<String> advertencias = new ArrayList<>();
        
        if (totalReservas > 100) {
            advertencias.add("Se generarán más de 100 reservas. Considere ajustar el rango de fechas.");
        }
        
        if (conflictos > 0) {
            advertencias.add("Se encontraron " + conflictos + " conflictos. Estas fechas se omitirán.");
        }
        
        if (request.getFechaFin().isAfter(LocalDate.now().plusYears(1))) {
            advertencias.add("La fecha de fin es más de 1 año en el futuro.");
        }
        
        return advertencias;
    }
}
