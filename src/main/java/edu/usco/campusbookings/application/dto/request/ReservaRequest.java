package edu.usco.campusbookings.application.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import edu.usco.campusbookings.application.util.validation.ValidDateRange;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange(startField = "fechaInicio", endField = "fechaFin", 
               message = "La fecha de fin debe ser posterior a la fecha de inicio y la duración debe estar entre 30 minutos y 8 horas")
public class ReservaRequest {
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDateTime fechaFin;

    @Size(max = 500, message = "Las observaciones no pueden exceder los 500 caracteres")
    private String observaciones;
}
