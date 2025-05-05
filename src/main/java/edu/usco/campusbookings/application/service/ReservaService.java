package edu.usco.campusbookings.application.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.dto.request.ReservaRequest;
import edu.usco.campusbookings.application.dto.response.ReservaResponse;
import edu.usco.campusbookings.application.exception.HorarioOcupadoException;
import edu.usco.campusbookings.application.exception.ReservaNotFoundException;
import edu.usco.campusbookings.application.mapper.ReservaMapper;
import edu.usco.campusbookings.application.port.input.ReservaUseCase;
import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.EstadoReservaRepositoryPort;
import edu.usco.campusbookings.application.port.output.ReservaRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService implements ReservaUseCase {

    private final ReservaRepositoryPort reservaRepositoryPort;
    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final EstadoReservaRepositoryPort estadoReservaRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final EmailServicePort emailServicePort;
    private final ReservaMapper reservaMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request) {
        // Obtener el usuario actual
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = new Usuario();
        usuario.setEmail(userDetails.getUsername());

        // Verificar disponibilidad del escenario
        Escenario escenario = escenarioRepositoryPort.findById(request.getEscenarioId())
                .orElseThrow(() -> new ReservaNotFoundException("Escenario no encontrado"));

        // Verificar si hay reservas que se superpongan con el horario solicitado
        if (reservaRepositoryPort.existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                request.getEscenarioId(),
                request.getFechaFin(),
                request.getFechaInicio()
        )) {
            throw new HorarioOcupadoException("El horario solicitado ya está ocupado para este escenario");
        }

        // Crear la reserva con estado "pendiente"
        EstadoReserva estado = estadoReservaRepositoryPort.findByNombre("pendiente")
                .orElseThrow(() -> new ReservaNotFoundException("Estado no encontrado"));

        Reserva reserva = reservaMapper.toEntity(request);
        reserva.setUsuario(usuario);
        reserva.setEscenario(escenario);
        reserva.setEstado(estado);

        Reserva saved = reservaRepositoryPort.save(reserva);
        return reservaMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ReservaResponse aprobarReserva(Long id) {
        Reserva reserva = reservaRepositoryPort.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada"));

        // Verificar que el usuario tenga permisos de administrador
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ReservaNotFoundException("No tiene permisos para aprobar reservas");
        }

        // Cambiar estado a "aprobado"
        EstadoReserva estado = estadoReservaRepositoryPort.findByNombre("aprobado")
                .orElseThrow(() -> new ReservaNotFoundException("Estado no encontrado"));
        reserva.setEstado(estado);

        Reserva saved = reservaRepositoryPort.save(reserva);
        return reservaMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ReservaResponse rechazarReserva(Long id) {
        Reserva reserva = reservaRepositoryPort.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada"));

        // Verificar que el usuario tenga permisos de administrador
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ReservaNotFoundException("No tiene permisos para rechazar reservas");
        }

        // Cambiar estado a "rechazado"
        EstadoReserva estado = estadoReservaRepositoryPort.findByNombre("rechazado")
                .orElseThrow(() -> new ReservaNotFoundException("Estado no encontrado"));
        reserva.setEstado(estado);

        Reserva saved = reservaRepositoryPort.save(reserva);
        return reservaMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ReservaResponse cancelarReserva(Long id) {
        Reserva reserva = reservaRepositoryPort.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva no encontrada"));

        // Verificar que el usuario sea el propietario de la reserva o administrador
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!reserva.getUsuario().getEmail().equals(userDetails.getUsername())
                && !SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ReservaNotFoundException("No tiene permisos para cancelar esta reserva");
        }

        // Cambiar estado a "cancelado"
        EstadoReserva estado = estadoReservaRepositoryPort.findByNombre("cancelado")
                .orElseThrow(() -> new ReservaNotFoundException("Estado no encontrado"));
        reserva.setEstado(estado);

        // Guardar la reserva antes de enviar la notificación
        Reserva saved = reservaRepositoryPort.save(reserva);

        // Enviar notificación a los administradores
        List<Usuario> administradores = usuarioRepositoryPort.findByRolesContaining("ROLE_ADMIN");
        for (Usuario admin : administradores) {
            String asunto = "Cancelación de Reserva - " + reserva.getEscenario().getNombre();
            String cuerpo = construirMensajeNotificacionAdmin(saved, admin);
            emailServicePort.sendConfirmationEmail(admin.getEmail(), asunto, cuerpo);
        }

        return reservaMapper.toDto(saved);
    }

    private String construirMensajeNotificacionAdmin(Reserva reserva, Usuario admin) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a ").append(admin.getNombre()).append("\n\n");
        mensaje.append("Se ha cancelado una reserva en el sistema:\n\n");
        mensaje.append("Detalles de la reserva:\n");
        mensaje.append("- Usuario: ").append(reserva.getUsuario().getNombre()).append("\n");
        mensaje.append("- Escenario: ").append(reserva.getEscenario().getNombre()).append("\n");
        mensaje.append("- Fecha de inicio: ").append(reserva.getFechaInicio()).append("\n");
        mensaje.append("- Fecha de fin: ").append(reserva.getFechaFin()).append("\n");
        mensaje.append("- Tipo de escenario: ").append(reserva.getEscenario().getTipo()).append("\n\n");
        
        mensaje.append("Si necesita más información, puede contactar al usuario directamente.\n\n");
        mensaje.append("Atentamente,\n");
        mensaje.append("Sistema de Campus Bookings");
        
        return mensaje.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorUsuario(Long usuarioId) {
        List<Reserva> reservas = reservaRepositoryPort.findByUsuarioId(usuarioId);
        return reservas.stream()
                .map(reservaMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorEscenario(Long escenarioId) {
        List<Reserva> reservas = reservaRepositoryPort.findByEscenarioId(escenarioId);
        return reservas.stream()
                .map(reservaMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> obtenerReservasPorEstado(String estadoNombre) {
        List<Reserva> reservas = reservaRepositoryPort.findByEstadoNombre(estadoNombre);
        return reservas.stream()
                .map(reservaMapper::toDto)
                .toList();
    }
}
