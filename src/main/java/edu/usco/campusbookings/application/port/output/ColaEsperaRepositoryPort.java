package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.ColaEspera;

import java.util.List;
import java.util.Optional;

/**
 * Output port for ColaEspera repository.
 *
 * @author Firstname Lastname
 */
public interface ColaEsperaRepositoryPort {
	/**
	 * Save all colaesperas.
	 *
	 * @param colaesperas the colaesperas to save
	 * @return the saved colaesperas
	 */
	List<ColaEspera> saveAll(List<ColaEspera> colaesperas);
	/**
	 * Save a ColaEspera.
	 *
	 * @param colaespera the ColaEspera to save
	 * @return the saved ColaEspera
	 */
	ColaEspera save(ColaEspera colaespera);
	/**
	 * Get all colaesperas.
	 *
	 * @return all colaesperas
	 */
	List<ColaEspera> findAll();
	/**
	 * Find a ColaEspera by id.
	 *
	 * @param id the id of the ColaEspera
	 * @return the ColaEspera if found, optional empty if not found
	 */
	Optional<ColaEspera> findById(Long id);
	/**
	 * Delete a ColaEspera by id.
	 *
	 * @param id the id of the ColaEspera
	 */
	void deleteById(Long id);
}