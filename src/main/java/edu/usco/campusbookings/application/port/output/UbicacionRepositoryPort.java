package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Ubicacion;

import java.util.List;
import java.util.Optional;

/**
 * Port for managing Ubicacion persistence operations.
 */
public interface UbicacionRepositoryPort {
    
    /**
     * Saves an Ubicacion.
     *
     * @param ubicacion the Ubicacion to save
     * @return the saved Ubicacion
     */
    Ubicacion save(Ubicacion ubicacion);
    
    /**
     * Finds an Ubicacion by ID.
     *
     * @param id the ID of the Ubicacion
     * @return an Optional containing the found Ubicacion or empty if not found
     */
    Optional<Ubicacion> findById(Long id);
    
    /**
     * Finds an Ubicacion by name.
     *
     * @param nombre the name of the Ubicacion
     * @return an Optional containing the found Ubicacion or empty if not found
     */
    Optional<Ubicacion> findByNombre(String nombre);
    
    /**
     * Finds all Ubicaciones.
     *
     * @return a list of all Ubicaciones
     */
    List<Ubicacion> findAll();
    
    /**
     * Deletes an Ubicacion by ID.
     *
     * @param id the ID of the Ubicacion to delete
     */
    void deleteById(Long id);
    
    /**
     * Checks if an Ubicacion exists by name.
     *
     * @param nombre the name to check
     * @return true if an Ubicacion with the given name exists, false otherwise
     */
    boolean existsByNombre(String nombre);
}
