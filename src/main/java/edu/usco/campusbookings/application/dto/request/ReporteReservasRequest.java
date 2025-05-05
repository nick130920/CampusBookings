package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReporteReservasRequest {
    @NotNull(message = "La fecha de inicio es requerida")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    private LocalDateTime fechaFin;

    private String tipo;
    private String estado;
}
