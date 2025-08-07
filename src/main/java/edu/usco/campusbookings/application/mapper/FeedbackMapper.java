package edu.usco.campusbookings.application.mapper;

import edu.usco.campusbookings.application.dto.response.FeedbackResponse;
import edu.usco.campusbookings.domain.model.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    
    public FeedbackResponse toResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .usuarioId(feedback.getUsuario().getId())
                .usuarioNombre(feedback.getUsuario().getNombre())
                .usuarioApellido(feedback.getUsuario().getApellido())
                .escenarioId(feedback.getEscenario().getId())
                .escenarioNombre(feedback.getEscenario().getNombre())
                .calificacion(feedback.getCalificacion())
                .comentario(feedback.getComentario())
                .activo(feedback.getActivo())
                .fechaCreacion(feedback.getCreatedDate())
                .fechaModificacion(feedback.getModifiedDate())
                .build();
    }
}
