package edu.usco.campusbookings.application.exception;

public class AlertaNotFoundException extends RuntimeException {

    public AlertaNotFoundException(String message) {
        super(message);
    }

    public AlertaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
