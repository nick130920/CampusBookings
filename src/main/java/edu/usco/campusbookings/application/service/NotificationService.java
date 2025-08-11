package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.notification.ReservaNotificationDto;
import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.infrastructure.adapter.input.handler.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para el manejo de notificaciones en tiempo real via WebSocket nativo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationWebSocketHandler webSocketHandler;
    
    /**
     * Envía notificación cuando una reserva es aprobada
     */
    public void notificarReservaAprobada(Reserva reserva) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .escenarioNombre(reserva.getEscenario().getNombre())
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("APROBADA")
                .mensaje(String.format("Tu reserva para %s ha sido aprobada", reserva.getEscenario().getNombre()))
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.RESERVA_APROBADA)
                .build();
        
        enviarNotificacionPrivada(reserva.getUsuario().getId(), notification);
        log.info("Notification sent for approved reservation ID: {} to user: {}", 
                reserva.getId(), reserva.getUsuario().getEmail());
    }
    
    /**
     * Envía notificación cuando una reserva es rechazada
     */
    public void notificarReservaRechazada(Reserva reserva) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .escenarioNombre(reserva.getEscenario().getNombre())
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("RECHAZADA")
                .mensaje(String.format("Tu reserva para %s ha sido rechazada", reserva.getEscenario().getNombre()))
                .motivo(reserva.getMotivoRechazo())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.RESERVA_RECHAZADA)
                .build();
        
        enviarNotificacionPrivada(reserva.getUsuario().getId(), notification);
        log.info("Notification sent for rejected reservation ID: {} to user: {}", 
                reserva.getId(), reserva.getUsuario().getEmail());
    }
    
    /**
     * Envía notificación cuando una reserva es cancelada
     */
    public void notificarReservaCancelada(Reserva reserva) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .escenarioNombre(reserva.getEscenario().getNombre())
                .estadoAnterior(reserva.getEstado().getNombre())
                .estadoNuevo("CANCELADA")
                .mensaje(String.format("Tu reserva para %s ha sido cancelada", reserva.getEscenario().getNombre()))
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.RESERVA_CANCELADA)
                .build();
        
        enviarNotificacionPrivada(reserva.getUsuario().getId(), notification);
        log.info("Notification sent for cancelled reservation ID: {} to user: {}", 
                reserva.getId(), reserva.getUsuario().getEmail());
    }
    
    /**
     * Envía notificación cuando una reserva es auto-rechazada por competencia
     */
    public void notificarReservaAutoRechazada(Reserva reserva, Long reservaAprobadaId) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .escenarioNombre(reserva.getEscenario().getNombre())
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("RECHAZADA")
                .mensaje(String.format("Tu reserva para %s fue rechazada automáticamente", reserva.getEscenario().getNombre()))
                .motivo(String.format("El horario fue asignado a otra solicitud (ID: %d) que fue aprobada primero", reservaAprobadaId))
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.RESERVA_AUTO_RECHAZADA)
                .build();
        
        enviarNotificacionPrivada(reserva.getUsuario().getId(), notification);
        log.info("Auto-rejection notification sent for reservation ID: {} to user: {}", 
                reserva.getId(), reserva.getUsuario().getEmail());
    }
    
    /**
     * Envía notificación a administradores sobre nueva reserva
     */
    public void notificarNuevaReservaAdmin(Reserva reserva) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(reserva.getId())
                .usuarioId(reserva.getUsuario().getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .escenarioNombre(reserva.getEscenario().getNombre())
                .estadoAnterior(null)
                .estadoNuevo("PENDIENTE")
                .mensaje(String.format("Nueva reserva pendiente de %s para %s", 
                        reserva.getUsuario().getNombre() + " " + reserva.getUsuario().getApellido(),
                        reserva.getEscenario().getNombre()))
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.NUEVA_RESERVA_ADMIN)
                .build();
        
        // Enviar a canal de administradores
        enviarNotificacionAdmin(notification);
        log.info("Admin notification sent for new reservation ID: {} from user: {}", 
                reserva.getId(), reserva.getUsuario().getEmail());
    }
    
    /**
     * Envía notificación privada a un usuario específico
     */
    private void enviarNotificacionPrivada(Long usuarioId, ReservaNotificationDto notification) {
        try {
            webSocketHandler.sendNotificationToUser(usuarioId, notification);
            log.info("✅ Native WebSocket notification sent to user {}", usuarioId);
        } catch (Exception e) {
            log.error("❌ Error sending private notification to user {}: {}", usuarioId, e.getMessage());
        }
    }
    
    /**
     * Envía notificación a canal de administradores
     */
    private void enviarNotificacionAdmin(ReservaNotificationDto notification) {
        try {
            webSocketHandler.sendNotificationToAllAdmins(notification);
            log.info("✅ Native WebSocket notification sent to all admins");
        } catch (Exception e) {
            log.error("❌ Error sending admin notification: {}", e.getMessage());
        }
    }

    /**
     * Envía alerta por WebSocket
     */
    public void enviarAlertaWebSocket(AlertaReserva alerta) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(alerta.getReserva().getId())
                .usuarioId(alerta.getReserva().getUsuario().getId())
                .usuarioEmail(alerta.getReserva().getUsuario().getEmail())
                .escenarioNombre(alerta.getReserva().getEscenario().getNombre())
                .estadoAnterior(null)
                .estadoNuevo("ALERTA")
                .mensaje(alerta.getMensaje())
                .fechaInicio(alerta.getReserva().getFechaInicio())
                .fechaFin(alerta.getReserva().getFechaFin())
                .timestamp(LocalDateTime.now())
                .tipo(mapearTipoAlerta(alerta.getTipo()))
                .build();

        enviarNotificacionPrivada(alerta.getReserva().getUsuario().getId(), notification);
        log.info("Alert WebSocket notification sent for alerta ID: {} to user: {}", 
                alerta.getId(), alerta.getReserva().getUsuario().getEmail());
    }

    /**
     * Envía notificación cuando se actualiza el rol de un usuario
     */
    public void notificarCambioRolUsuario(Long usuarioId, String usuarioEmail, String rolAnterior, String rolNuevo) {
        ReservaNotificationDto notification = ReservaNotificationDto.builder()
                .reservaId(null) // No aplica para cambios de rol
                .usuarioId(usuarioId)
                .usuarioEmail(usuarioEmail)
                .escenarioNombre(null) // No aplica para cambios de rol
                .estadoAnterior(rolAnterior)
                .estadoNuevo(rolNuevo)
                .mensaje(String.format("Tu rol ha sido actualizado de %s a %s", rolAnterior, rolNuevo))
                .fechaInicio(null) // No aplica para cambios de rol
                .fechaFin(null) // No aplica para cambios de rol
                .timestamp(LocalDateTime.now())
                .tipo(ReservaNotificationDto.NotificationType.USER_ROLE_UPDATED)
                .build();
        
        enviarNotificacionPrivada(usuarioId, notification);
        log.info("Role change notification sent to user: {} - Role changed from {} to {}", 
                usuarioEmail, rolAnterior, rolNuevo);
    }

    /**
     * Mapea tipos de alerta a tipos de notificación
     */
    private ReservaNotificationDto.NotificationType mapearTipoAlerta(AlertaReserva.TipoAlerta tipoAlerta) {
        return switch (tipoAlerta) {
            case RECORDATORIO_24H, RECORDATORIO_2H, RECORDATORIO_30MIN -> 
                ReservaNotificationDto.NotificationType.RESERVA_APROBADA; // Reutilizamos este tipo
            case CONFIRMACION_LLEGADA -> 
                ReservaNotificationDto.NotificationType.RESERVA_APROBADA;
            case EXPIRACION_RESERVA, CANCELACION_AUTOMATICA -> 
                ReservaNotificationDto.NotificationType.RESERVA_CANCELADA;
            case CAMBIO_ESTADO -> 
                ReservaNotificationDto.NotificationType.NUEVA_RESERVA_ADMIN;
        };
    }
}