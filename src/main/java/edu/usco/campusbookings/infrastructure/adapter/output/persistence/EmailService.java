package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServicePort {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendConfirmationEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        javaMailSender.send(message);
    }

    /**
     * Envía un correo de confirmación de reserva al usuario.
     * @param reserva la reserva confirmada
     */
    @Override
    public void enviarCorreoConfirmacionReserva(Reserva reserva) {
        String destinatario = reserva.getUsuario().getEmail();
        String asunto = "Confirmación de Reserva";
        String cuerpo = String.format("Hola %s,\n\nTu reserva para el escenario '%s' ha sido confirmada.\nFecha: %s\n\nGracias por usar CampusBookings!",
                reserva.getUsuario().getNombre(),
                reserva.getEscenario().getNombre(),
                reserva.getFechaInicio().toString());
        sendConfirmationEmail(destinatario, asunto, cuerpo);
    }

    /**
     * Envía un correo notificando el rechazo de la reserva al usuario.
     * @param reserva la reserva rechazada
     */
    @Override
    public void enviarCorreoRechazoReserva(Reserva reserva) {
        String destinatario = reserva.getUsuario().getEmail();
        String asunto = "Reserva Rechazada";
        String cuerpo = String.format("Hola %s,\n\nLamentamos informarte que tu reserva para el escenario '%s' ha sido rechazada.\nFecha: %s\n\nPara más información comunícate con soporte.",
                reserva.getUsuario().getNombre(),
                reserva.getEscenario().getNombre(),
                reserva.getFechaInicio().toString());
        sendConfirmationEmail(destinatario, asunto, cuerpo);
    }

    /**
     * Envía un correo notificando la cancelación de la reserva al usuario.
     * @param reserva la reserva cancelada
     */
    @Override
    public void enviarCorreoCancelacionReserva(Reserva reserva) {
        String destinatario = reserva.getUsuario().getEmail();
        String asunto = "Reserva Cancelada";
        String cuerpo = String.format("Hola %s,\n\nTu reserva para el escenario '%s' ha sido cancelada.\nFecha: %s\n\nSi tienes dudas, comunícate con soporte.",
                reserva.getUsuario().getNombre(),
                reserva.getEscenario().getNombre(),
                reserva.getFechaInicio().toString());
        sendConfirmationEmail(destinatario, asunto, cuerpo);
    }
}
