package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.TipoEscenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for TipoEscenario entities.
 */
@Repository
public interface TipoEscenarioJpaRepository extends JpaRepository<TipoEscenario, Long> {
    
    /**
     * Finds a TipoEscenario by its name.
     *
     * @param nombre the name to search for
     * @return an Optional containing the found TipoEscenario or empty if not found
     */
    Optional<TipoEscenario> findByNombre(String nombre);
    
    /**
     * Checks if a TipoEscenario with the given name exists.
     *
     * @param nombre the name to check
     * @return true if a TipoEscenario with the given name exists, false otherwise
     */
    boolean existsByNombre(String nombre);
}
