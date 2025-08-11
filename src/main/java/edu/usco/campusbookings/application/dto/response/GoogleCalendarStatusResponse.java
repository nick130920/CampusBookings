package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta del estado de conexión con Google Calendar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalendarStatusResponse {
    private boolean connected;
    private String authorizationUrl;
    private String message;
}
