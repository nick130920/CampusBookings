package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Escenario;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Escenario repository.
 *
 * @author Firstname Lastname
 */
public interface EscenarioRepositoryPort {
	/**
	 * Save all escenarios.
	 *
	 * @param escenarios the escenarios to save
	 * @return the saved escenarios
	 */
	List<Escenario> saveAll(List<Escenario> escenarios);
	/**
	 * Save a Escenario.
	 *
	 * @param escenario the Escenario to save
	 * @return the saved Escenario
	 */
	Escenario save(Escenario escenario);
	/**
	 * Get all escenarios.
	 *
	 * @return all escenarios
	 */
	List<Escenario> findAll();
	/**
	 * Find a Escenario by id.
	 *
	 * @param id the id of the Escenario
	 * @return the Escenario if found, optional empty if not found
	 */
	Optional<Escenario> findById(Long id);
	/**
	 * Delete a Escenario by id.
	 *
	 * @param id the id of the Escenario
	 */
	void deleteById(Long id);
}