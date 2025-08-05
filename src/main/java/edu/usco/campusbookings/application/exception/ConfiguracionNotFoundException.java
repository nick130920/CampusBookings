package edu.usco.campusbookings.application.exception;

public class ConfiguracionNotFoundException extends RuntimeException {

    public ConfiguracionNotFoundException() {
        super("Configuración del sistema no encontrada");
    }

    public ConfiguracionNotFoundException(String message) {
        super(message);
    }

    public ConfiguracionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfiguracionNotFoundException(Throwable cause) {
        super("Configuración del sistema no encontrada", cause);
    }
}