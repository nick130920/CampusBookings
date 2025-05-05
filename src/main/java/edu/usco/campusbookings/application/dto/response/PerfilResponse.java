package edu.usco.campusbookings.application.dto.response;

import lombok.Data;

@Data
public class PerfilResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
}
