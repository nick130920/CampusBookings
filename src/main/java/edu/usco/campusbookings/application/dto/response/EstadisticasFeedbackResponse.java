package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadisticasFeedbackResponse {
    
    private Long escenarioId;
    private String escenarioNombre;
    private Double calificacionPromedio;
    private Long totalFeedbacks;
    private Long[] distribucionCalificaciones; // Array de 5 elementos (1-5 estrellas)
}
