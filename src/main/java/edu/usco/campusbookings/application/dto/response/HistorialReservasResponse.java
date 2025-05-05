package edu.usco.campusbookings.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistorialReservasResponse {
    private Long id;
    private String escenarioNombre;
    private String tipo;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
