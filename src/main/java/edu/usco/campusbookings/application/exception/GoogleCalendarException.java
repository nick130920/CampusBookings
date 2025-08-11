package edu.usco.campusbookings.application.exception;

/**
 * Excepción personalizada para errores relacionados con Google Calendar
 */
public class GoogleCalendarException extends RuntimeException {
    
    public GoogleCalendarException(String message) {
        super(message);
    }
    
    public GoogleCalendarException(String message, Throwable cause) {
        super(message, cause);
    }
}
