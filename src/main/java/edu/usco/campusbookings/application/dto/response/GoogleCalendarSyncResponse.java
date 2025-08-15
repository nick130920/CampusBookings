package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de sincronización masiva con Google Calendar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalendarSyncResponse {
    private boolean success;
    private String message;
    private int totalReservas;
    private int reservasSincronizadas;
    private int errores;
    private boolean connected;
}
