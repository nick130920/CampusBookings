package edu.usco.campusbookings.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para manejar la autorización de Google Calendar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCalendarAuthRequest {
    private String authorizationCode;
}
