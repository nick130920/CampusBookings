package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRecurrenteJpaRepository extends JpaRepository<ReservaRecurrente, Long> {
    
    List<ReservaRecurrente> findByUsuarioId(Long usuarioId);
    
    List<ReservaRecurrente> findByUsuarioIdAndActiva(Long usuarioId, Boolean activa);
    
    List<ReservaRecurrente> findByActiva(Boolean activa);
    
    List<ReservaRecurrente> findByEscenarioId(Long escenarioId);
    
    List<ReservaRecurrente> findByEscenarioIdAndActiva(Long escenarioId, Boolean activa);
    
    /**
     * Encuentra todas las reservas recurrentes activas que necesitan generar nuevas reservas
     * (aquellas donde la fecha actual está dentro del rango y aún no han llegado al máximo)
     */
    @Query("SELECT rr FROM ReservaRecurrente rr WHERE " +
           "rr.activa = true AND " +
           "rr.fechaInicio <= CURRENT_DATE AND " +
           "rr.fechaFin >= CURRENT_DATE AND " +
           "(rr.maxReservas IS NULL OR " +
           " (SELECT COUNT(r) FROM Reserva r WHERE r.reservaRecurrente.id = rr.id) < rr.maxReservas)")
    List<ReservaRecurrente> findActivasParaGeneracion();
    
    /**
     * Encuentra reservas recurrentes que terminan en una fecha específica o antes
     */
    List<ReservaRecurrente> findByFechaFinLessThanEqual(LocalDate fecha);
    
    /**
     * Cuenta el número de reservas generadas por una reserva recurrente específica
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.reservaRecurrente.id = :reservaRecurrenteId")
    Long countReservasGeneradas(@Param("reservaRecurrenteId") Long reservaRecurrenteId);
    
    /**
     * Encuentra reservas recurrentes por rango de fechas
     */
    @Query("SELECT rr FROM ReservaRecurrente rr WHERE " +
           "rr.fechaInicio <= :fechaFin AND rr.fechaFin >= :fechaInicio")
    List<ReservaRecurrente> findByRangoFechas(@Param("fechaInicio") LocalDate fechaInicio, 
                                             @Param("fechaFin") LocalDate fechaFin);
    
    /**
     * Encuentra reservas recurrentes activas de un usuario específico
     */
    @Query("SELECT rr FROM ReservaRecurrente rr WHERE " +
           "rr.usuario.id = :usuarioId AND rr.activa = true AND " +
           "rr.fechaFin >= CURRENT_DATE")
    List<ReservaRecurrente> findActivasByUsuario(@Param("usuarioId") Long usuarioId);
}
