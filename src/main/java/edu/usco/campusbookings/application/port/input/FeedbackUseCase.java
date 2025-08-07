package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ActualizarFeedbackRequest;
import edu.usco.campusbookings.application.dto.request.CrearFeedbackRequest;
import edu.usco.campusbookings.application.dto.response.EstadisticasFeedbackResponse;
import edu.usco.campusbookings.application.dto.response.FeedbackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackUseCase {
    
    FeedbackResponse crearFeedback(CrearFeedbackRequest request, String email);
    
    FeedbackResponse actualizarFeedback(Long feedbackId, ActualizarFeedbackRequest request, String email);
    
    FeedbackResponse obtenerFeedbackPorId(Long feedbackId);
    
    FeedbackResponse obtenerMiFeedbackParaEscenario(Long escenarioId, String email);
    
    Page<FeedbackResponse> obtenerFeedbacksPorEscenario(Long escenarioId, Pageable pageable);
    
    Page<FeedbackResponse> obtenerMisFeedbacks(String email, Pageable pageable);
    
    EstadisticasFeedbackResponse obtenerEstadisticasFeedback(Long escenarioId);
    
    void eliminarFeedback(Long feedbackId, String email);
    
    void desactivarFeedback(Long feedbackId, String email);
}
