package edu.usco.campusbookings.application.exception;

public class EscenarioNotFoundException extends RuntimeException {
    public EscenarioNotFoundException(String message) {
        super(message);
    }
}