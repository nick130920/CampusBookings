package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request para obtener todas las ocupaciones de un escenario en un día específico
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionesDiaRequest {
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;
    
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
}