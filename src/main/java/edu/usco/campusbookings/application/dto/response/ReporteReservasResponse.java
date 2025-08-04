package edu.usco.campusbookings.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReporteReservasResponse {
    private Long escenarioId;
    private String escenarioNombre;
    private String tipo;
    private String estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer cantidadReservas;
    private String usuarioEmail;
    private String observaciones;
}
