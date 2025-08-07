package edu.usco.campusbookings.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearFeedbackRequest {
    
    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    private String comentario;
}
