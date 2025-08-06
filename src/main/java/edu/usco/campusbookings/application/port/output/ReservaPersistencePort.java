package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaPersistencePort {
    Reserva save(Reserva reserva);
    List<Reserva> saveAll(List<Reserva> reservas);
    Optional<Reserva> findById(Long id);
    boolean existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long escenarioId,
            LocalDateTime fechaFin,
            LocalDateTime fechaInicio
    );
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByEscenarioId(Long escenarioId);
    List<Reserva> findByEstadoNombre(String estadoNombre);
    
    /**
     * Finds an EstadoReserva by its name
     * @param nombre The name of the state to find
     * @return An Optional containing the found state, or empty if not found
     */
    Optional<EstadoReserva> findEstadoByNombre(String nombre);
    
    boolean existsByEscenarioIdAndFechaInicioBetween(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    
    boolean existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String estadoNombre
    );
    
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Reserva> findAll();
    
    /**
     * Encuentra un escenario por su ID
     * @param escenarioId ID del escenario a buscar
     * @return Optional con el escenario encontrado
     */
    Optional<Escenario> findEscenarioById(Long escenarioId);
    
    /**
     * Encuentra reservas que entran en conflicto con el rango de fechas especificado.
     * Solo considera reservas APROBADAS.
     * @param escenarioId ID del escenario
     * @param fechaInicio Fecha de inicio del rango a verificar
     * @param fechaFin Fecha de fin del rango a verificar
     * @return Lista de reservas que entran en conflicto
     */
    List<Reserva> findConflictingReservations(Long escenarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Encuentra todas las reservas de un escenario en un rango de fechas específico.
     * Incluye reservas APROBADAS y PENDIENTES.
     * @param escenarioId ID del escenario
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de reservas en el rango especificado
     */
    List<Reserva> findByEscenarioIdAndFechaRange(Long escenarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
