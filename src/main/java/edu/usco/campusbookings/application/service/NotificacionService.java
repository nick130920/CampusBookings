package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final EmailServicePort emailServicePort;

    /**
     * Envía una notificación por correo electrónico sobre el estado de una reserva.
     * 
     * @param reserva La reserva para la cual se enviará la notificación
     */
    public void enviarNotificacionReserva(Reserva reserva) {
        String asunto = "Estado de su reserva - " + reserva.getEscenario().getNombre();
        String cuerpo = construirMensaje(reserva);
        
        emailServicePort.sendConfirmationEmail(
            reserva.getUsuario().getEmail(),
            asunto,
            cuerpo
        );
    }

    private String construirMensaje(Reserva reserva) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a ").append(reserva.getUsuario().getNombre()).append("\n\n");
        mensaje.append("Le informamos que su reserva para el escenario ").append(reserva.getEscenario().getNombre());
        mensaje.append(" ha sido ").append(reserva.getEstado().getNombre()).append(".\n\n");
        mensaje.append("Detalles de la reserva:\n");
        mensaje.append("- Fecha de inicio: ").append(reserva.getFechaInicio()).append("\n");
        mensaje.append("- Fecha de fin: ").append(reserva.getFechaFin()).append("\n");
        mensaje.append("- Tipo de escenario: ").append(reserva.getEscenario().getTipo()).append("\n\n");
        
        // Verificar si el estado es "RECHAZADA" (sin usar enum)
        if ("RECHAZADA".equals(reserva.getEstado().getNombre().toUpperCase())) {
            mensaje.append("Motivo del rechazo: ").append(reserva.getMotivoRechazo()).append("\n");
        }
        
        mensaje.append("\nSi tiene alguna pregunta, no dude en contactarnos.\n\n");
        mensaje.append("Atentamente,\n");
        mensaje.append("Equipo de Campus Bookings");
        
        return mensaje.toString();
    }
}
