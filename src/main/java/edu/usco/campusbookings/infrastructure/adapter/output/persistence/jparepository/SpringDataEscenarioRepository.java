package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.usco.campusbookings.domain.model.Escenario;

public interface SpringDataEscenarioRepository extends JpaRepository<Escenario, Long> {
    List<Escenario> findByTipoOrNombreOrUbicacion(String tipo, String nombre, String ubicacion);
    List<Escenario> findByNombreContainingOrUbicacionContainingOrTipoContaining(String nombre, String ubicacion,
            String tipo);
    void deleteById(Long id);
}
