package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.ReservaRecurrente;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRecurrentePersistencePort {
    
    ReservaRecurrente save(ReservaRecurrente reservaRecurrente);
    
    Optional<ReservaRecurrente> findById(Long id);
    
    List<ReservaRecurrente> findByUsuarioId(Long usuarioId);
    
    List<ReservaRecurrente> findByUsuarioIdAndActiva(Long usuarioId, Boolean activa);
    
    List<ReservaRecurrente> findByActiva(Boolean activa);
    
    List<ReservaRecurrente> findByEscenarioId(Long escenarioId);
    
    List<ReservaRecurrente> findByEscenarioIdAndActiva(Long escenarioId, Boolean activa);
    
    /**
     * Encuentra todas las reservas recurrentes activas que necesitan generar nuevas reservas
     * (aquellas donde la fecha actual está dentro del rango y aún no han llegado al máximo)
     */
    List<ReservaRecurrente> findActivasParaGeneracion();
    
    /**
     * Encuentra reservas recurrentes que terminan en una fecha específica o antes
     */
    List<ReservaRecurrente> findByFechaFinLessThanEqual(LocalDate fecha);
    
    /**
     * Cuenta el número de reservas generadas por una reserva recurrente específica
     */
    Long countReservasGeneradas(Long reservaRecurrenteId);
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}
