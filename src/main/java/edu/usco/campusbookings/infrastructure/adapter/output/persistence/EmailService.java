package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.domain.model.AlertaReserva;
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

    @Override
    public void sendPasswordResetEmail(String destinatario, String nombre, String codigo) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(subjectPrefix + " 🔐 Código de recuperación de contraseña");
            helper.setFrom(fromEmail);
            
            // Crear el contenido HTML del email
            String htmlContent = buildPasswordResetEmailContent(nombre, codigo);
            helper.setText(htmlContent, true);
            
            javaMailSender.send(message);
            log.info("Email de recuperación de contraseña enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error enviando email de recuperación a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error enviando email de recuperación de contraseña", e);
        }
    }

    private String buildPasswordResetEmailContent(String nombre, String codigo) {
        String template = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recuperación de Contraseña</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    
                    <!-- Header -->
                    <div style="background: linear-gradient(135deg, #8B1538 0%%, #A91D3A 100%%); color: white; padding: 30px 20px; text-align: center;">
                        <h1 style="margin: 0; font-size: 24px; font-weight: bold;">🔐 Recuperación de Contraseña</h1>
                        <p style="margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;">Universidad Surcolombiana</p>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 40px 30px;">
                        <h2 style="color: #333; margin: 0 0 20px 0; font-size: 20px;">Hola %s,</h2>
                        
                        <p style="color: #555; line-height: 1.6; margin: 0 0 20px 0;">
                            Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en CampusBookings.
                        </p>
                        
                        <p style="color: #555; line-height: 1.6; margin: 0 0 30px 0;">
                            Tu código de verificación es:
                        </p>
                        
                        <!-- Código -->
                        <div style="background-color: #f8f9fa; border: 2px dashed #8B1538; border-radius: 8px; padding: 20px; text-align: center; margin: 0 0 30px 0;">
                            <div style="font-size: 32px; font-weight: bold; color: #8B1538; letter-spacing: 4px; font-family: 'Courier New', monospace;">
                                %s
                            </div>
                            <p style="margin: 10px 0 0 0; color: #666; font-size: 14px;">
                                Este código expira en 15 minutos
                            </p>
                        </div>
                        
                        <div style="background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 0 0 20px 0;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                <strong>⚠️ Importante:</strong> Si no solicitaste este cambio, puedes ignorar este email. Tu contraseña no será modificada.
                            </p>
                        </div>
                        
                        <p style="color: #555; line-height: 1.6; margin: 0;">
                            Si tienes problemas, contacta al soporte técnico de CampusBookings.
                        </p>
                    </div>
                    
                    <!-- Footer -->
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e9ecef;">
                        <p style="margin: 0; color: #6c757d; font-size: 12px;">
                            Sistema de Reservas CampusBookings<br>
                            Universidad Surcolombiana<br>
                            Este es un email automático, no responder.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;
            
        return String.format(template, nombre, codigo);
    }

    @Override
    public void enviarCorreoAlertaReserva(AlertaReserva alerta) {
        try {
            String template = obtenerPlantillaAlerta(alerta.getTipo());
            Context context = crearContextoAlerta(alerta);
            
            String subject = obtenerAsuntoAlerta(alerta.getTipo(), alerta.getReserva().getEscenario().getNombre());
            
            sendHtmlEmail(alerta.getReserva().getUsuario().getEmail(), subject, template, context);
            
            log.info("Email de alerta {} enviado a {}", alerta.getTipo(), alerta.getReserva().getUsuario().getEmail());
            
        } catch (Exception e) {
            log.error("Error enviando email de alerta {}: {}", alerta.getTipo(), e.getMessage(), e);
            throw new RuntimeException("Error enviando email de alerta", e);
        }
    }

    /**
     * Obtiene la plantilla correspondiente según el tipo de alerta
     */
    private String obtenerPlantillaAlerta(AlertaReserva.TipoAlerta tipo) {
        return switch (tipo) {
            case RECORDATORIO_24H -> "email/alerta-recordatorio-24h";
            case RECORDATORIO_2H -> "email/alerta-recordatorio-2h";
            case RECORDATORIO_30MIN -> "email/alerta-recordatorio-30min";
            case CONFIRMACION_LLEGADA -> "email/alerta-recordatorio-30min"; // Reutilizamos
            case EXPIRACION_RESERVA, CANCELACION_AUTOMATICA -> "email/reserva-cancelada";
            case CAMBIO_ESTADO -> "email/reserva-aprobada";
        };
    }

    /**
     * Crea el contexto para las plantillas de alerta
     */
    private Context crearContextoAlerta(AlertaReserva alerta) {
        Context context = new Context();
        Reserva reserva = alerta.getReserva();
        
        // Datos del usuario
        context.setVariable("nombreUsuario", reserva.getUsuario().getNombre());
        context.setVariable("apellidoUsuario", reserva.getUsuario().getApellido());
        
        // Datos de la reserva
        context.setVariable("reservaId", reserva.getId());
        context.setVariable("escenarioNombre", reserva.getEscenario().getNombre());
        context.setVariable("fechaReserva", reserva.getFechaInicio().format(DATE_FORMATTER));
        context.setVariable("horaInicio", reserva.getFechaInicio().format(TIME_FORMATTER));
        context.setVariable("horaFin", reserva.getFechaFin().format(TIME_FORMATTER));
        context.setVariable("observaciones", reserva.getObservaciones());
        
        // Datos del escenario si están disponibles
        if (reserva.getEscenario().getTipo() != null) {
            context.setVariable("escenarioTipo", reserva.getEscenario().getTipo().getNombre());
        }
        context.setVariable("escenarioUbicacion", reserva.getEscenario().getUbicacion());
        
        // Datos específicos de la alerta
        context.setVariable("tipoAlerta", alerta.getTipo().getDescripcion());
        context.setVariable("mensajeAlerta", alerta.getMensaje());
        
        return context;
    }

    /**
     * Obtiene el asunto del email según el tipo de alerta
     */
    private String obtenerAsuntoAlerta(AlertaReserva.TipoAlerta tipo, String escenario) {
        return switch (tipo) {
            case RECORDATORIO_24H -> 
                String.format("Recordatorio: Tu reserva de %s es mañana", escenario);
            case RECORDATORIO_2H -> 
                String.format("¡Tu reserva de %s comienza en 2 horas!", escenario);
            case RECORDATORIO_30MIN -> 
                String.format("¡ÚLTIMA LLAMADA! Tu reserva de %s en 30 minutos", escenario);
            case CONFIRMACION_LLEGADA -> 
                String.format("Confirma tu llegada a %s", escenario);
            case EXPIRACION_RESERVA -> 
                String.format("Tu reserva de %s ha expirado", escenario);
            case CANCELACION_AUTOMATICA -> 
                String.format("Reserva de %s cancelada automáticamente", escenario);
            case CAMBIO_ESTADO -> 
                String.format("Cambio en tu reserva de %s", escenario);
        };
    }
}
