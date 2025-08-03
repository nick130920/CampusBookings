package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.usco.campusbookings.domain.model.Reserva;

@Repository
public interface SpringDataReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByEstadoNombre(String estadoNombre);

    @Query("SELECT r FROM Reserva r WHERE r.escenario.id = :escenarioId AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    boolean existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long escenarioId,
            LocalDateTime fechaFin,
            LocalDateTime fechaInicio
    );
}
