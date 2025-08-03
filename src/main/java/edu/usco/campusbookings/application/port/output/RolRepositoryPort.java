package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Rol;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Rol repository.
 *
 * @author Firstname Lastname
 */
public interface RolRepositoryPort {
	/**
	 * Save all rols.
	 *
	 * @param rols the rols to save
	 * @return the saved rols
	 */
	List<Rol> saveAll(List<Rol> rols);
	/**
	 * Save a Rol.
	 *
	 * @param rol the Rol to save
	 * @return the saved Rol
	 */
	Rol save(Rol rol);
	/**
	 * Get all rols.
	 *
	 * @return all rols
	 */
	List<Rol> findAll();
	/**
	 * Find a Rol by id.
	 *
	 * @param id the id of the Rol
	 * @return the Rol if found, optional empty if not found
	 */
	Optional<Rol> findById(Long id);
	/**
	 * Delete a Rol by id.
	 *
	 * @param id the id of the Rol
	 */
	void deleteById(Long id);
	
	/**
	 * Find a Rol by nombre.
	 *
	 * @param nombre the nombre of the Rol
	 * @return the Rol if found, optional empty if not found
	 */
	Optional<Rol> findByNombre(String nombre);
}