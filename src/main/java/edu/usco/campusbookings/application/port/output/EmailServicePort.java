package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Reserva;

/**
 * Puerto para el servicio de correos electrónicos.
 * Define los métodos para enviar diferentes tipos de notificaciones por correo.
 */
public interface EmailServicePort {

    /**
     * Envía un correo de confirmación cuando una reserva es creada (estado PENDIENTE).
     *
     * @param reserva la nueva reserva pendiente
     */
    void enviarCorreoConfirmacionReserva(Reserva reserva);

    /**
     * Envía un correo de confirmación cuando una reserva es aprobada.
     *
     * @param reserva la reserva aprobada
     */
    void enviarCorreoAprobacionReserva(Reserva reserva);

    /**
     * Envía un correo al administrador notificando una nueva reserva pendiente.
     *
     * @param reserva la nueva reserva pendiente
     */
    void enviarCorreoNuevaReservaAdmin(Reserva reserva);

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
    
    /**
     * Envía un email con código de verificación para recuperación de contraseña
     * @param destinatario Email del usuario
     * @param nombre Nombre del usuario
     * @param codigo Código de verificación de 6 dígitos
     */
    void sendPasswordResetEmail(String destinatario, String nombre, String codigo);
}
