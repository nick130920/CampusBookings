package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.usco.campusbookings.domain.model.Reserva;

@Repository
public interface ReservaJpaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByEscenarioId(Long escenarioId);
    List<Reserva> findByEstadoNombre(String estadoNombre);

    @Query("SELECT r FROM Reserva r WHERE r.escenario.id = :escenarioId AND r.fechaInicio BETWEEN :fechaInicio AND :fechaFin")
    List<Reserva> findByEscenarioIdAndFechaInicioBetween(
            @Param("escenarioId") Long escenarioId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    boolean existsByEscenarioIdAndFechaInicioBetween(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    boolean existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long escenarioId,
            LocalDateTime fechaFin,
            LocalDateTime fechaInicio
    );  
}
