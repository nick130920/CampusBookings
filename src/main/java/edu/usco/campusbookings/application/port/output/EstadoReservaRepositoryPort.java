package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.EstadoReserva;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EstadoReservaRepositoryPort {
    EstadoReserva save(EstadoReserva estadoReserva);
    List<EstadoReserva> saveAll(List<EstadoReserva> estadosReserva);
    Optional<EstadoReserva> findById(Long id);
    Optional<EstadoReserva> findByNombre(String nombre);
    List<EstadoReserva> findAll();
    List<EstadoReserva> findAll(Pageable pageable);
    List<EstadoReserva> findAllByNombreContaining(String nombre, Pageable pageable);
    boolean existsById(Long id);
    boolean existsByNombre(String nombre);
    void deleteById(Long id);
}
