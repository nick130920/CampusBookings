package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {
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
    boolean existsByEscenarioIdAndFechaInicioBetween(
            Long escenarioId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Reserva> findAll();
}
