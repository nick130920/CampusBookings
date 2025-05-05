package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Reserva;

/**
 * Puerto para el servicio de notificaciones en tiempo real.
 * Define los métodos para enviar diferentes tipos de notificaciones.
 */
public interface NotificacionServicePort {
    
    /**
     * Notifica a los administradores sobre una nueva reserva.
     * 
     * @param reserva la nueva reserva
     */
    void notificarNuevaReserva(Reserva reserva);
    
    /**
     * Notifica al usuario que su reserva ha sido aprobada.
     * 
     * @param reserva la reserva aprobada
     */
    void notificarReservaAprobada(Reserva reserva);
    
    /**
     * Notifica al usuario que su reserva ha sido rechazada.
     * 
     * @param reserva la reserva rechazada
     */
    void notificarReservaRechazada(Reserva reserva);
    
    /**
     * Notifica a los administradores que una reserva ha sido cancelada.
     * 
     * @param reserva la reserva cancelada
     */
    void notificarReservaCancelada(Reserva reserva);
}
