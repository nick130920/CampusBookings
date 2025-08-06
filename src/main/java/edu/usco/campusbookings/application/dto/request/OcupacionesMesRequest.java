package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para obtener todas las ocupaciones de un escenario en un mes específico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionesMesRequest {
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;
    
    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "El año debe ser mayor a 2020")
    @Max(value = 2030, message = "El año debe ser menor a 2030")
    private Integer año;
    
    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mes;
}