package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ActualizarFeedbackRequest;
import edu.usco.campusbookings.application.dto.request.CrearFeedbackRequest;
import edu.usco.campusbookings.application.dto.response.EstadisticasFeedbackResponse;
import edu.usco.campusbookings.application.dto.response.FeedbackResponse;
import edu.usco.campusbookings.application.exception.FeedbackNotFoundException;
import edu.usco.campusbookings.application.exception.EscenarioNotFoundException;
import edu.usco.campusbookings.application.exception.UsuarioNotFoundException;
import edu.usco.campusbookings.application.mapper.FeedbackMapper;
import edu.usco.campusbookings.application.port.input.FeedbackUseCase;
import edu.usco.campusbookings.application.port.output.FeedbackPort;
import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.Feedback;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService implements FeedbackUseCase {
    
    private final FeedbackPort feedbackPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final EscenarioRepositoryPort escenarioRepositoryPort;
    private final FeedbackMapper feedbackMapper;
    
    @Override
    public FeedbackResponse crearFeedback(CrearFeedbackRequest request, String email) {
        log.debug("Creando feedback para escenario {} por usuario {}", request.getEscenarioId(), email);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        Escenario escenario = escenarioRepositoryPort.findById(request.getEscenarioId())
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario no encontrado: " + request.getEscenarioId()));
        
        // Verificar si ya existe feedback para esta combinación usuario-escenario
        if (feedbackPort.existsByUsuarioIdAndEscenarioId(usuario.getId(), escenario.getId())) {
            throw new IllegalArgumentException("Ya existe un feedback para este escenario por parte del usuario");
        }
        
        Feedback feedback = Feedback.builder()
                .usuario(usuario)
                .escenario(escenario)
                .calificacion(request.getCalificacion())
                .comentario(request.getComentario())
                .activo(true)
                .build();
        
        Feedback savedFeedback = feedbackPort.save(feedback);
        log.info("Feedback creado exitosamente con ID: {}", savedFeedback.getId());
        
        return feedbackMapper.toResponse(savedFeedback);
    }
    
    @Override
    public FeedbackResponse actualizarFeedback(Long feedbackId, ActualizarFeedbackRequest request, String email) {
        log.debug("Actualizando feedback {} por usuario {}", feedbackId, email);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        Feedback feedback = feedbackPort.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback no encontrado: " + feedbackId));
        
        // Verificar que el usuario es el propietario del feedback
        if (!feedback.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para modificar este feedback");
        }
        
        feedback.setCalificacion(request.getCalificacion());
        feedback.setComentario(request.getComentario());
        
        Feedback updatedFeedback = feedbackPort.save(feedback);
        log.info("Feedback actualizado exitosamente: {}", feedbackId);
        
        return feedbackMapper.toResponse(updatedFeedback);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse obtenerFeedbackPorId(Long feedbackId) {
        log.debug("Obteniendo feedback por ID: {}", feedbackId);
        
        Feedback feedback = feedbackPort.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback no encontrado: " + feedbackId));
        
        return feedbackMapper.toResponse(feedback);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse obtenerMiFeedbackParaEscenario(Long escenarioId, String email) {
        log.debug("Obteniendo feedback del usuario {} para escenario {}", email, escenarioId);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        return feedbackPort.findByUsuarioIdAndEscenarioId(usuario.getId(), escenarioId)
                .map(feedbackMapper::toResponse)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> obtenerFeedbacksPorEscenario(Long escenarioId, Pageable pageable) {
        log.debug("Obteniendo feedbacks para escenario: {}", escenarioId);
        
        Page<Feedback> feedbacks = feedbackPort.findByEscenarioId(escenarioId, pageable);
        return feedbacks.map(feedbackMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> obtenerMisFeedbacks(String email, Pageable pageable) {
        log.debug("Obteniendo feedbacks del usuario: {}", email);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        Page<Feedback> feedbacks = feedbackPort.findByUsuarioId(usuario.getId(), pageable);
        return feedbacks.map(feedbackMapper::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public EstadisticasFeedbackResponse obtenerEstadisticasFeedback(Long escenarioId) {
        log.debug("Obteniendo estadísticas de feedback para escenario: {}", escenarioId);
        
        Escenario escenario = escenarioRepositoryPort.findById(escenarioId)
                .orElseThrow(() -> new EscenarioNotFoundException("Escenario no encontrado: " + escenarioId));
        
        Double promedioCalificacion = feedbackPort.getCalificacionPromedioByEscenarioId(escenarioId);
        Long totalFeedbacks = feedbackPort.countByEscenarioIdAndActivoTrue(escenarioId);
        
        // Obtener distribución de calificaciones
        List<Feedback> feedbacks = feedbackPort.findByEscenarioIdAndActivoTrue(escenarioId);
        Long[] distribucion = new Long[5];
        for (int i = 0; i < 5; i++) {
            distribucion[i] = 0L;
        }
        
        for (Feedback feedback : feedbacks) {
            if (feedback.getCalificacion() >= 1 && feedback.getCalificacion() <= 5) {
                distribucion[feedback.getCalificacion() - 1]++;
            }
        }
        
        return EstadisticasFeedbackResponse.builder()
                .escenarioId(escenarioId)
                .escenarioNombre(escenario.getNombre())
                .calificacionPromedio(promedioCalificacion)
                .totalFeedbacks(totalFeedbacks)
                .distribucionCalificaciones(distribucion)
                .build();
    }
    
    @Override
    public void eliminarFeedback(Long feedbackId, String email) {
        log.debug("Eliminando feedback {} por usuario {}", feedbackId, email);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        Feedback feedback = feedbackPort.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback no encontrado: " + feedbackId));
        
        // Verificar que el usuario es el propietario del feedback
        if (!feedback.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para eliminar este feedback");
        }
        
        feedbackPort.delete(feedback);
        log.info("Feedback eliminado exitosamente: {}", feedbackId);
    }
    
    @Override
    public void desactivarFeedback(Long feedbackId, String email) {
        log.debug("Desactivando feedback {} por usuario {}", feedbackId, email);
        
        Usuario usuario = usuarioRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado: " + email));
        
        Feedback feedback = feedbackPort.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException("Feedback no encontrado: " + feedbackId));
        
        // Verificar que el usuario es el propietario del feedback
        if (!feedback.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permisos para modificar este feedback");
        }
        
        feedback.setActivo(false);
        feedbackPort.save(feedback);
        log.info("Feedback desactivado exitosamente: {}", feedbackId);
    }
}
