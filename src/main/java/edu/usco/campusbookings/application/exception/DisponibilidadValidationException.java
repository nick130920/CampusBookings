package edu.usco.campusbookings.application.exception;

/**
 * Excepción lanzada cuando hay errores de validación en la consulta de disponibilidad.
 */
public class DisponibilidadValidationException extends RuntimeException {
    
    public DisponibilidadValidationException(String message) {
        super(message);
    }
    
    public DisponibilidadValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 