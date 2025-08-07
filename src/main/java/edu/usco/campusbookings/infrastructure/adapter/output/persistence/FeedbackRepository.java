package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.domain.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    Optional<Feedback> findByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId);
    
    List<Feedback> findByEscenarioId(Long escenarioId);
    
    List<Feedback> findByUsuarioId(Long usuarioId);
    
    Page<Feedback> findByEscenarioId(Long escenarioId, Pageable pageable);
    
    Page<Feedback> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    List<Feedback> findByEscenarioIdAndActivoTrue(Long escenarioId);
    
    @Query("SELECT AVG(f.calificacion) FROM Feedback f WHERE f.escenario.id = :escenarioId AND f.activo = true")
    Double findCalificacionPromedioByEscenarioId(@Param("escenarioId") Long escenarioId);
    
    Long countByEscenarioId(Long escenarioId);
    
    Long countByEscenarioIdAndActivoTrue(Long escenarioId);
    
    boolean existsByUsuarioIdAndEscenarioId(Long usuarioId, Long escenarioId);
    
    @Query("SELECT f FROM Feedback f WHERE f.escenario.id = :escenarioId AND f.activo = true ORDER BY f.createdDate DESC")
    Page<Feedback> findActiveByEscenarioIdOrderByCreatedDateDesc(@Param("escenarioId") Long escenarioId, Pageable pageable);
    
    @Query("SELECT f FROM Feedback f WHERE f.usuario.id = :usuarioId AND f.activo = true ORDER BY f.createdDate DESC")
    Page<Feedback> findActiveByUsuarioIdOrderByCreatedDateDesc(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
