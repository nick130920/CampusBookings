package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.domain.model.Reserva;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements EmailServicePort {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.reservas.from:namc1309@gmail.com}")
    private String fromEmail;
    
    @Value("${mail.reservas.admin-email:namc1309@gmail.com}")
    private String adminEmail;
    
    @Value("${mail.reservas.subject-prefix:[CampusBookings USCO]}")
    private String subjectPrefix;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("es", "ES"));

    @Override
    public void sendConfirmationEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subjectPrefix + " " + subject);
        message.setText(body);
        
        javaMailSender.send(message);
    }

    /**
     * Envía un correo HTML usando Thymeleaf template
     */
    private void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subjectPrefix + " " + subject);
            
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            javaMailSender.send(mimeMessage);
            log.info("Email enviado exitosamente a: {} con asunto: {}", to, subject);
            
        } catch (MessagingException e) {
            log.error("Error enviando email a: {} con asunto: {}", to, subject, e);
            throw new RuntimeException("Error enviando email", e);
        }
    }

    /**
     * Envía un correo de confirmación de reserva al usuario.
     * @param reserva la reserva confirmada
     */
    @Override
    public void enviarCorreoConfirmacionReserva(Reserva reserva) {
        log.info("Enviando correo de confirmación de reserva a: {}", reserva.getUsuario().getEmail());
        
        Context context = new Context();
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("escenarioTipo", reserva.getEscenario().getTipo() != null ? reserva.getEscenario().getTipo().getNombre() : "");
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion() != null ? reserva.getEscenario().getUbicacion().getNombre() : "");
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        context.setVariable("reservaId", reserva.getId());
        context.setVariable("estadoReserva", reserva.getEstado().getNombre());
        
        sendHtmlEmail(
            reserva.getUsuario().getEmail(), 
            "Nueva Reserva Creada - Pendiente de Aprobación", 
            "email/reserva-creada", 
            context
        );
    }

    /**
     * Envía un correo notificando la aprobación de la reserva al usuario.
     * @param reserva la reserva aprobada
     */
    public void enviarCorreoAprobacionReserva(Reserva reserva) {
        log.info("Enviando correo de aprobación de reserva a: {}", reserva.getUsuario().getEmail());
        
        Context context = new Context();
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("escenarioTipo", reserva.getEscenario().getTipo() != null ? reserva.getEscenario().getTipo().getNombre() : "");
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion() != null ? reserva.getEscenario().getUbicacion().getNombre() : "");
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        context.setVariable("reservaId", reserva.getId());
        
        sendHtmlEmail(
            reserva.getUsuario().getEmail(), 
            "¡Reserva Aprobada! - Confirmación Final", 
            "email/reserva-aprobada", 
            context
        );
    }

    /**
     * Envía un correo notificando el rechazo de la reserva al usuario.
     * @param reserva la reserva rechazada
     */
    @Override
    public void enviarCorreoRechazoReserva(Reserva reserva) {
        log.info("Enviando correo de rechazo de reserva a: {}", reserva.getUsuario().getEmail());
        
        Context context = new Context();
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("escenarioTipo", reserva.getEscenario().getTipo() != null ? reserva.getEscenario().getTipo().getNombre() : "");
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion() != null ? reserva.getEscenario().getUbicacion().getNombre() : "");
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        context.setVariable("motivoRechazo", reserva.getMotivoRechazo());
        context.setVariable("reservaId", reserva.getId());
        
        sendHtmlEmail(
            reserva.getUsuario().getEmail(), 
            "Reserva No Aprobada - Información Importante", 
            "email/reserva-rechazada", 
            context
        );
    }

    /**
     * Envía un correo notificando la cancelación de la reserva al usuario.
     * @param reserva la reserva cancelada
     */
    @Override
    public void enviarCorreoCancelacionReserva(Reserva reserva) {
        log.info("Enviando correo de cancelación de reserva a: {}", reserva.getUsuario().getEmail());
        
        Context context = new Context();
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("escenarioTipo", reserva.getEscenario().getTipo() != null ? reserva.getEscenario().getTipo().getNombre() : "");
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion() != null ? reserva.getEscenario().getUbicacion().getNombre() : "");
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        context.setVariable("reservaId", reserva.getId());
        
        sendHtmlEmail(
            reserva.getUsuario().getEmail(), 
            "Reserva Cancelada - Confirmación", 
            "email/reserva-cancelada", 
            context
        );
    }

    /**
     * Envía un correo al administrador notificando una nueva reserva pendiente.
     * @param reserva la nueva reserva pendiente
     */
    public void enviarCorreoNuevaReservaAdmin(Reserva reserva) {
        log.info("Enviando correo de nueva reserva al administrador: {}", adminEmail);
        
        Context context = new Context();
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        context.setVariable("emailUsuario", reserva.getUsuario().getEmail());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("escenarioTipo", reserva.getEscenario().getTipo() != null ? reserva.getEscenario().getTipo().getNombre() : "");
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion() != null ? reserva.getEscenario().getUbicacion().getNombre() : "");
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        context.setVariable("reservaId", reserva.getId());
        context.setVariable("fechaCreacion", reserva.getCreatedDate());
        
        sendHtmlEmail(
            adminEmail, 
            "Nueva Reserva Pendiente de Aprobación - Acción Requerida", 
            "email/admin-nueva-reserva", 
            context
        );
    }
}
