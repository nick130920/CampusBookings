package edu.usco.campusbookings.application.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaNotificationDto {
    
    private Long reservaId;
    private Long usuarioId;
    private String usuarioEmail;
    private String escenarioNombre;
    private String estadoAnterior;
    private String estadoNuevo;
    private String mensaje;
    private String motivo; // Para rechazos
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime timestamp;
    private NotificationType tipo;
    
    public enum NotificationType {
        RESERVA_APROBADA,
        RESERVA_RECHAZADA, 
        RESERVA_CANCELADA,
        RESERVA_AUTO_RECHAZADA,
        NUEVA_RESERVA_ADMIN
    }
}