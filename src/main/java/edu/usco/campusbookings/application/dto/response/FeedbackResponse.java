package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
    
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioApellido;
    private Long escenarioId;
    private String escenarioNombre;
    private Integer calificacion;
    private String comentario;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
