package edu.usco.campusbookings.application.exception;

public class PermissionNotFoundException extends RuntimeException {
    
    public PermissionNotFoundException(String message) {
        super(message);
    }
    
    public PermissionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PermissionNotFoundException withId(Long id) {
        return new PermissionNotFoundException("Permiso no encontrado con ID: " + id);
    }
    
    public static PermissionNotFoundException withName(String name) {
        return new PermissionNotFoundException("Permiso no encontrado con nombre: " + name);
    }
}
