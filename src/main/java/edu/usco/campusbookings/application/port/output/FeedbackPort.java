package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FeedbackPort {
    
    Feedback save(Feedback feedback);
    
    Optional<Feedback> findById(Long id);
    
    Optional<Feedback> findByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId);
    
    List<Feedback> findByEscenarioId(Long escenarioId);
    
    List<Feedback> findByUsuarioId(Long usuarioId);
    
    Page<Feedback> findByEscenarioId(Long escenarioId, Pageable pageable);
    
    Page<Feedback> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Feedback> findAll(Pageable pageable);
    
    List<Feedback> findByEscenarioIdAndActivoTrue(Long escenarioId);
    
    Double getCalificacionPromedioByEscenarioId(Long escenarioId);
    
    Long countByEscenarioId(Long escenarioId);
    
    Long countByEscenarioIdAndActivoTrue(Long escenarioId);
    
    void delete(Feedback feedback);
    
    void deleteById(Long id);
    
    boolean existsByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId);
}
