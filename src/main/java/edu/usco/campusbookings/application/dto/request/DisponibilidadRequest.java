package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DisponibilidadRequest {
    @NotNull(message = "La fecha de inicio es requerida")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es requerida")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime fechaFin;

    private String tipo;
    private String ubicacion;
    private String nombre;
}
