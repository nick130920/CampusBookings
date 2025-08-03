package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for Ubicacion entities.
 */
@Repository
public interface UbicacionJpaRepository extends JpaRepository<Ubicacion, Long> {
    
    /**
     * Finds an Ubicacion by its name.
     *
     * @param nombre the name to search for
     * @return an Optional containing the found Ubicacion or empty if not found
     */
    Optional<Ubicacion> findByNombre(String nombre);
    
    /**
     * Checks if an Ubicacion with the given name exists.
     *
     * @param nombre the name to check
     * @return true if an Ubicacion with the given name exists, false otherwise
     */
    boolean existsByNombre(String nombre);
}
