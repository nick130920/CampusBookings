package edu.usco.campusbookings.application.exception;

public class RolValidationException extends RuntimeException {
    
    public RolValidationException(String message) {
        super(message);
    }
    
    public RolValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static RolValidationException duplicateName(String name) {
        return new RolValidationException("Ya existe un rol con el nombre: " + name);
    }
    
    public static RolValidationException cannotDeleteRoleWithUsers(String roleName, int userCount) {
        return new RolValidationException("No se puede eliminar el rol '" + roleName + 
                "' porque tiene " + userCount + " usuarios asignados");
    }
    
    public static RolValidationException cannotDeactivateRoleWithUsers(String roleName) {
        return new RolValidationException("No se puede desactivar el rol '" + roleName + 
                "' porque tiene usuarios asignados");
    }
}
