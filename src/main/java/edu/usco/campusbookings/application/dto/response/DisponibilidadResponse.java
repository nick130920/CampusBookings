package edu.usco.campusbookings.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DisponibilidadResponse {
    private Long id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private boolean disponible;
}
