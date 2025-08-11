package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Usuario;

import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia relacionadas con Google Calendar
 */
public interface GoogleCalendarRepositoryPort {
    
    /**
     * Actualiza los tokens de Google Calendar del usuario
     */
    Usuario updateGoogleCalendarTokens(Long userId, String accessToken, String refreshToken, boolean connected);
    
    /**
     * Busca un usuario por su ID
     */
    Optional<Usuario> findById(Long userId);
    
    /**
     * Desconecta Google Calendar del usuario (limpia los tokens)
     */
    Usuario disconnectGoogleCalendar(Long userId);
}
