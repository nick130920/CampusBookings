package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.TipoEscenario;

import java.util.List;
import java.util.Optional;

/**
 * Port for managing TipoEscenario persistence operations.
 */
public interface TipoEscenarioRepositoryPort {
    
    /**
     * Saves a TipoEscenario.
     *
     * @param tipoEscenario the TipoEscenario to save
     * @return the saved TipoEscenario
     */
    TipoEscenario save(TipoEscenario tipoEscenario);
    
    /**
     * Finds a TipoEscenario by ID.
     *
     * @param id the ID of the TipoEscenario
     * @return an Optional containing the found TipoEscenario or empty if not found
     */
    Optional<TipoEscenario> findById(Long id);
    
    /**
     * Finds a TipoEscenario by name.
     *
     * @param nombre the name of the TipoEscenario
     * @return an Optional containing the found TipoEscenario or empty if not found
     */
    Optional<TipoEscenario> findByNombre(String nombre);
    
    /**
     * Finds all TipoEscenario.
     *
     * @return a list of all TipoEscenario
     */
    List<TipoEscenario> findAll();
    
    /**
     * Deletes a TipoEscenario by ID.
     *
     * @param id the ID of the TipoEscenario to delete
     */
    void deleteById(Long id);
    
    /**
     * Checks if a TipoEscenario exists by name.
     *
     * @param nombre the name to check
     * @return true if a TipoEscenario with the given name exists, false otherwise
     */
    boolean existsByNombre(String nombre);
}
