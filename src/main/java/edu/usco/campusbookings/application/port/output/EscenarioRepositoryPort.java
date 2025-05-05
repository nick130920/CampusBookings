package edu.usco.campusbookings.application.port.output;

import java.util.List;
import java.util.Optional;

import edu.usco.campusbookings.application.dto.response.DetalleEscenarioResponse;
import edu.usco.campusbookings.domain.model.Escenario;

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

	/**
	 * Busca escenarios por tipo, nombre o ubicación.
	 *
	 * @param tipo el tipo del escenario
	 * @param nombre el nombre del escenario
	 * @param ubicacion la ubicación del escenario
	 * @return lista de escenarios que coinciden con los criterios
	 */
	List<Escenario> findByTipoOrNombreOrUbicacion(String tipo, String nombre, String ubicacion);

	/**
	 * Busca escenarios por nombre, ubicación o tipo.
	 *
	 * @param nombre el nombre del escenario
	 * @param ubicacion la ubicación del escenario
	 * @param tipo el tipo del escenario
	 * @return lista de escenarios que coinciden con los criterios
	 */
	List<Escenario> findByNombreContainingOrUbicacionContainingOrTipoContaining(String nombre, String ubicacion, String tipo);

	/**
	 * Obtiene los detalles completos de un escenario.
	 *
	 * @param id el ID del escenario
	 * @return detalles del escenario
	 */
	DetalleEscenarioResponse obtenerDetalles(Long id);
}