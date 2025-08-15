package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.GoogleCalendarAuthRequest;
import edu.usco.campusbookings.application.dto.response.GoogleCalendarStatusResponse;
import edu.usco.campusbookings.application.dto.response.GoogleCalendarSyncResponse;
import edu.usco.campusbookings.domain.model.Reserva;

/**
 * Puerto de entrada para casos de uso de Google Calendar
 */
public interface GoogleCalendarUseCase {
    
    /**
     * Obtiene la URL de autorización para conectar con Google Calendar
     */
    GoogleCalendarStatusResponse getAuthorizationUrl();
    
    /**
     * Conecta el usuario con Google Calendar usando el código de autorización
     */
    GoogleCalendarStatusResponse connectToGoogleCalendar(GoogleCalendarAuthRequest request);
    
    /**
     * Desconecta el usuario de Google Calendar
     */
    GoogleCalendarStatusResponse disconnectFromGoogleCalendar();
    
    /**
     * Obtiene el estado actual de la conexión con Google Calendar
     */
    GoogleCalendarStatusResponse getConnectionStatus();
    
    /**
     * Sincroniza una reserva con Google Calendar (crear/actualizar evento)
     */
    String syncReservationWithCalendar(Reserva reserva);
    
    /**
     * Elimina un evento de Google Calendar
     */
    void deleteEventFromCalendar(String eventId);
    
    /**
     * Sincroniza todas las reservas del usuario con Google Calendar
     */
    GoogleCalendarSyncResponse syncAllUserReservations();
}
