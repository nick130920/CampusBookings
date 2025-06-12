package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Reserva;

/**
 * Puerto para el servicio de correos electrónicos.
 * Define los métodos para enviar diferentes tipos de notificaciones por correo.
 */
public interface EmailServicePort {

    /**
     * Envía un correo de confirmación cuando una reserva es aprobada.
     *
     * @param reserva la reserva aprobada
     */
    void enviarCorreoConfirmacionReserva(Reserva reserva);

    /**
     * Envía un correo de notificación cuando una reserva es rechazada.
     *
     * @param reserva la reserva rechazada
     */
    void enviarCorreoRechazoReserva(Reserva reserva);

    /**
     * Envía un correo de notificación cuando una reserva es cancelada.
     *
     * @param reserva la reserva cancelada
     */
    void enviarCorreoCancelacionReserva(Reserva reserva);

    /**
     * Envía un correo electrónico de confirmación de reserva.
     * 
     * @param to Email del destinatario
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     */
    void sendConfirmationEmail(String to, String subject, String body);
}
