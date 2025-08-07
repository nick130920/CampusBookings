package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.FeedbackPort;
import edu.usco.campusbookings.domain.model.Feedback;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FeedbackPersistenceAdapter implements FeedbackPort {
    
    private final FeedbackRepository feedbackRepository;
    
    @Override
    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
    
    @Override
    public Optional<Feedback> findById(Long id) {
        return feedbackRepository.findById(id);
    }
    
    @Override
    public Optional<Feedback> findByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId) {
        return feedbackRepository.findByUsuarioIdAndEscenarioId(usuarioId, escenarioId);
    }
    
    @Override
    public List<Feedback> findByEscenarioId(Long escenarioId) {
        return feedbackRepository.findByEscenarioId(escenarioId);
    }
    
    @Override
    public List<Feedback> findByUsuarioId(Long usuarioId) {
        return feedbackRepository.findByUsuarioId(usuarioId);
    }
    
    @Override
    public Page<Feedback> findByEscenarioId(Long escenarioId, Pageable pageable) {
        return feedbackRepository.findActiveByEscenarioIdOrderByCreatedDateDesc(escenarioId, pageable);
    }
    
    @Override
    public Page<Feedback> findByUsuarioId(Long usuarioId, Pageable pageable) {
        return feedbackRepository.findActiveByUsuarioIdOrderByCreatedDateDesc(usuarioId, pageable);
    }
    
    @Override
    public Page<Feedback> findAll(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }
    
    @Override
    public List<Feedback> findByEscenarioIdAndActivoTrue(Long escenarioId) {
        return feedbackRepository.findByEscenarioIdAndActivoTrue(escenarioId);
    }
    
    @Override
    public Double getCalificacionPromedioByEscenarioId(Long escenarioId) {
        Double promedio = feedbackRepository.findCalificacionPromedioByEscenarioId(escenarioId);
        return promedio != null ? promedio : 0.0;
    }
    
    @Override
    public Long countByEscenarioId(Long escenarioId) {
        return feedbackRepository.countByEscenarioId(escenarioId);
    }
    
    @Override
    public Long countByEscenarioIdAndActivoTrue(Long escenarioId) {
        return feedbackRepository.countByEscenarioIdAndActivoTrue(escenarioId);
    }
    
    @Override
    public void delete(Feedback feedback) {
        feedbackRepository.delete(feedback);
    }
    
    @Override
    public void deleteById(Long id) {
        feedbackRepository.deleteById(id);
    }
    
    @Override
    public boolean existsByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId) {
        return feedbackRepository.existsByUsuarioIdAndEscenarioId(usuarioId, escenarioId);
    }
}
