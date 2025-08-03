package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.domain.model.Ubicacion;

import java.util.List;
import java.util.Optional;

/**
 * Use case port for managing Ubicacion entities.
 */
public interface UbicacionUseCase {

    /**
     * Retrieves all locations.
     *
     * @return a list of all locations
     */
    List<Ubicacion> findAll();
    
    /**
     * Finds a location by its name.
     *
     * @param nombre the name to search for
     * @return the found location or empty if not found
     */
    Optional<Ubicacion> findByNombre(String nombre);
    
    /**
     * Saves a location.
     *
     * @param ubicacion the location to save
     * @return the saved location
     */
    Ubicacion save(Ubicacion ubicacion);
    
    /**
     * Deletes a location by its ID.
     *
     * @param id the ID of the location to delete
     */
    void deleteById(Long id);
}
