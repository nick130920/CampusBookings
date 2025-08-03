package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import edu.usco.campusbookings.application.util.validation.ValidDateRange;

import java.time.LocalDateTime;

/**
 * DTO para verificar disponibilidad de un escenario en tiempo real.
 * Inspirado en las verificaciones instantáneas de Cal.com.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(startField = "fechaInicio", endField = "fechaFin", 
               message = "Rango de fechas inválido para verificación de disponibilidad")
public class VerificarDisponibilidadRequest {
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDateTime fechaFin;
}