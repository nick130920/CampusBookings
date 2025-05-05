package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.EstadoReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EstadoReservaJpaRepository extends JpaRepository<EstadoReserva, Long> {
    Optional<EstadoReserva> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT e FROM EstadoReserva e WHERE e.nombre LIKE %:nombre%")
    Page<EstadoReserva> findAllByNombreContaining(@Param("nombre") String nombre, Pageable pageable);
}
