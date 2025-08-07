package edu.usco.campusbookings.infrastructure.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para validar permisos específicos en métodos de controladores.
 * 
 * Esta anotación permite especificar el recurso y la acción requerida
 * para acceder a un endpoint específico.
 * 
 * @author Sistema de Campus Bookings
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    
    /**
     * Recurso sobre el que se requiere el permiso
     * Ejemplo: "ESCENARIOS", "RESERVAS", "USUARIOS"
     */
    String resource();
    
    /**
     * Acción que se requiere sobre el recurso
     * Ejemplo: "CREATE", "READ", "UPDATE", "DELETE", "MANAGE"
     */
    String action();
    
    /**
     * Mensaje personalizado para mostrar cuando se deniegue el acceso
     */
    String message() default "No tienes permisos suficientes para realizar esta acción";
}

