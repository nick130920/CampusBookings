package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.usco.campusbookings.application.exception.ReservaNotFoundException;
import edu.usco.campusbookings.application.port.output.ReservaPersistencePort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataEscenarioRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.EstadoReservaJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.ReservaJpaRepository;
import lombok.RequiredArgsConstructor;

/**
 * Repositorio de reservas que implementa la interfaz ReservaRepositoryPort.
 * Proporciona métodos para gestionar las operaciones CRUD de reservas.
 */
@Repository
@RequiredArgsConstructor
public class ReservaPersistence implements ReservaPersistencePort {

    private final ReservaJpaRepository reservaJpaRepository;
    private final EstadoReservaJpaRepository estadoReservaJpaRepository;
    private final SpringDataEscenarioRepository escenarioJpaRepository;

    /**
     * Guarda una nueva reserva en el sistema.
     * 
     * @param reserva La reserva a guardar
     * @return La reserva guardada con su ID generado
     */
    @Override
    @Transactional
    public Reserva save(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        return reservaJpaRepository.save(reserva);
    }

    /**
     * Guarda múltiples reservas en el sistema.
     * 
     * @param reservas La lista de reservas a guardar
     * @return La lista de reservas guardadas
     * @throws IllegalArgumentException Si la lista de reservas es nula o vacía
     */
    @Override
    @Transactional
    public List<Reserva> saveAll(List<Reserva> reservas) {
        if (reservas == null || reservas.isEmpty()) {
            throw new IllegalArgumentException("La lista de reservas no puede ser nula o vacía");
        }
        return reservaJpaRepository.saveAll(reservas);
    }

    /**
     * Busca una reserva por su ID.
     * 
     * @param id El ID de la reserva a buscar
     * @return La reserva encontrada, o Optional.empty() si no existe
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Reserva> findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la reserva no puede ser nulo");
        }
        return reservaJpaRepository.findById(id);
    }

    /**
     * Busca todas las reservas de un usuario.
     * 
     * @param usuarioId El ID del usuario
     * @return La lista de reservas del usuario
     * @throws IllegalArgumentException Si el ID del usuario es nulo
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        return reservaJpaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Busca todas las reservas de un escenario.
     * 
     * @param escenarioId El ID del escenario
     * @return La lista de reservas del escenario
     * @throws IllegalArgumentException Si el ID del escenario es nulo
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByEscenarioId(Long escenarioId) {
        if (escenarioId == null) {
            throw new IllegalArgumentException("El ID del escenario no puede ser nulo");
        }
        return reservaJpaRepository.findByEscenarioId(escenarioId);
    }

    /**
     * Busca todas las reservas por estado.
     * 
     * @param estadoNombre El nombre del estado
     * @return La lista de reservas con el estado especificado
     * @throws IllegalArgumentException Si el nombre del estado es nulo o vacío
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findByEstadoNombre(String estadoNombre) {
        if (estadoNombre == null || estadoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede ser nulo o vacío");
        }
        return reservaJpaRepository.findByEstadoNombre(estadoNombre);
    }

    /**
     * Verifica si existe una reserva para un escenario en un rango de fechas.
     * 
     * @param escenarioId El ID del escenario
     * @param fechaInicio La fecha de inicio del rango
     * @param fechaFin La fecha de fin del rango
     * @return true si existe una reserva, false en caso contrario
     * @throws IllegalArgumentException Si alguno de los parámetros es nulo
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEscenarioIdAndFechaInicioBetween(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {
        if (escenarioId == null || fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Ningún parámetro puede ser nulo");
        }
        return reservaJpaRepository.existsByEscenarioIdAndFechaInicioBetween(
                escenarioId,
                fechaInicio,
                fechaFin
        );
    }

    /**
     * Obtiene todas las reservas del sistema.
     * 
     * @return La lista de todas las reservas
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findAll() {
        return reservaJpaRepository.findAll();
    }
    
    @Override
    public boolean existsByEscenarioIdAndFechaInicioBetweenAndEstadoNombre(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String estadoNombre) {
        
        return reservaJpaRepository.existsByEscenarioIdAndFechaInicioBetweenAndEstado_Nombre(
                escenarioId, fechaInicio, fechaFin, estadoNombre);
    }

    /**
     * Elimina una reserva por su ID.
     * 
     * @param id El ID de la reserva a eliminar
     * @throws ReservaNotFoundException Si la reserva no existe
     */
    @Override
    @Transactional
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la reserva no puede ser nulo");
        }
        if (!reservaJpaRepository.existsById(id)) {
            throw new ReservaNotFoundException("Reserva no encontrada con ID: " + id);
        }
        reservaJpaRepository.deleteById(id);
    }

    /**
     * Verifica si existe una reserva con el ID especificado.
     * 
     * @param id El ID de la reserva a verificar
     * @return true si existe, false en caso contrario
     * @throws IllegalArgumentException Si el ID es nulo
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la reserva no puede ser nulo");
        }
        return reservaJpaRepository.existsById(id);
    }
    
    @Override
    public Optional<EstadoReserva> findEstadoByNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede estar vacío");
        }
        return estadoReservaJpaRepository.findByNombre(nombre);
    }

    @Override
    public boolean existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(Long escenarioId,
            LocalDateTime fechaFin, LocalDateTime fechaInicio) {
        return reservaJpaRepository.existsByEscenarioIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                escenarioId,
                fechaFin,
                fechaInicio
        );
    }

    /**
     * Encuentra un escenario por su ID.
     * 
     * @param escenarioId ID del escenario a buscar
     * @return Optional con el escenario encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Escenario> findEscenarioById(Long escenarioId) {
        if (escenarioId == null) {
            throw new IllegalArgumentException("El ID del escenario no puede ser nulo");
        }
        return escenarioJpaRepository.findById(escenarioId);
    }

    /**
     * Encuentra reservas que entran en conflicto con el rango de fechas especificado.
     * Solo considera reservas APROBADAS.
     * 
     * @param escenarioId ID del escenario
     * @param fechaInicio Fecha de inicio del rango a verificar
     * @param fechaFin Fecha de fin del rango a verificar
     * @return Lista de reservas que entran en conflicto
     */
    @Override
    @Transactional(readOnly = true)
    public List<Reserva> findConflictingReservations(Long escenarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (escenarioId == null || fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Ningún parámetro puede ser nulo");
        }
        
        // Buscar reservas aprobadas que se solapen con el rango de fechas
        return reservaJpaRepository.findConflictingReservations(escenarioId, fechaInicio, fechaFin);
    }
}
