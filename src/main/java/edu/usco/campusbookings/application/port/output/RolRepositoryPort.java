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

	/**
	 * Find all active rols.
	 *
	 * @return all active rols
	 */
	List<Rol> findByActivoTrue();

	/**
	 * Find a Rol by id with permissions loaded.
	 *
	 * @param id the id of the Rol
	 * @return the Rol with permissions if found, optional empty if not found
	 */
	Optional<Rol> findByIdWithPermissions(Long id);

	/**
	 * Find a Rol by nombre with permissions loaded.
	 *
	 * @param nombre the nombre of the Rol
	 * @return the Rol with permissions if found, optional empty if not found
	 */
	Optional<Rol> findByNombreWithPermissions(String nombre);

	/**
	 * Search rols by a search term.
	 *
	 * @param searchTerm the search term
	 * @return matching rols
	 */
	List<Rol> searchRoles(String searchTerm);

	/**
	 * Delete a Rol.
	 *
	 * @param rol the Rol to delete
	 */
	void delete(Rol rol);

	/**
	 * Check if a Rol exists by nombre.
	 *
	 * @param nombre the nombre to check
	 * @return true if exists, false otherwise
	 */
	boolean existsByNombre(String nombre);

	/**
	 * Get all rols with permissions loaded.
	 *
	 * @return all rols with permissions
	 */
	List<Rol> findAllWithPermissions();

	/**
	 * Count usuarios by rol ID.
	 *
	 * @param rolId the rol ID
	 * @return count of usuarios
	 */
	long countUsuariosByRolId(Long rolId);
}