package edu.usco.campusbookings.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private String email;
    private String nombre;
    private String apellido;
    private String rol; // Nuevo campo para el rol del usuario
    private Long userId; // ID del usuario para referencia
} 