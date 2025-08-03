package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.domain.model.TipoEscenario;

import java.util.List;
import java.util.Optional;

/**
 * Use case port for managing TipoEscenario entities.
 */
public interface TipoEscenarioUseCase {

    /**
     * Retrieves all scenario types.
     *
     * @return a list of all scenario types
     */
    List<TipoEscenario> findAll();
    
    /**
     * Finds a scenario type by its name.
     *
     * @param nombre the name to search for
     * @return the found scenario type or empty if not found
     */
    Optional<TipoEscenario> findByNombre(String nombre);
    
    /**
     * Saves a scenario type.
     *
     * @param tipoEscenario the scenario type to save
     * @return the saved scenario type
     */
    TipoEscenario save(TipoEscenario tipoEscenario);
    
    /**
     * Deletes a scenario type by its ID.
     *
     * @param id the ID of the scenario type to delete
     */
    void deleteById(Long id);
}
