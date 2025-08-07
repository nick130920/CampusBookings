package edu.usco.campusbookings.application.exception;

public class RolNotFoundException extends RuntimeException {
    
    public RolNotFoundException(String message) {
        super(message);
    }
    
    public RolNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static RolNotFoundException withId(Long id) {
        return new RolNotFoundException("Rol no encontrado con ID: " + id);
    }
    
    public static RolNotFoundException withName(String name) {
        return new RolNotFoundException("Rol no encontrado con nombre: " + name);
    }
}