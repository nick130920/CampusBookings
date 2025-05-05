package edu.usco.campusbookings.infrastructure.exception;

import lombok.Getter;

@Getter
public enum ErrorDocumentation {
    NOT_FOUND(404, "Not Found", "El recurso solicitado no fue encontrado"),
    BAD_REQUEST(400, "Bad Request", "La solicitud contiene datos inválidos"),
    UNAUTHORIZED(401, "Unauthorized", "Credenciales inválidas o token expirado"),
    FORBIDDEN(403, "Forbidden", "No tiene permisos para realizar esta acción"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "Ocurrió un error inesperado en el servidor");

    private final int statusCode;
    private final String error;
    private final String description;

    ErrorDocumentation(int statusCode, String error, String description) {
        this.statusCode = statusCode;
        this.error = error;
        this.description = description;
    }
} 