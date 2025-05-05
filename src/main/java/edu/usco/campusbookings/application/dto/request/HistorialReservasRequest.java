package edu.usco.campusbookings.application.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialReservasRequest {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private String tipo;
}
