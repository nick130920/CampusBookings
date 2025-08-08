package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Usuario repository.
 *
 * @author Firstname Lastname
 */
public interface UsuarioRepositoryPort {
	/**
	 * Save all usuarios.
	 *
	 * @param usuarios the usuarios to save
	 * @return the saved usuarios
	 */
	List<Usuario> saveAll(List<Usuario> usuarios);
	/**
	 * Save a Usuario.
	 *
	 * @param usuario the Usuario to save
	 * @return the saved Usuario
	 */
	Usuario save(Usuario usuario);
	/**
	 * Get all usuarios.
	 *
	 * @return all usuarios
	 */
	List<Usuario> findAll();
	/**
	 * Find a Usuario by id.
	 *
	 * @param id the id of the Usuario
	 * @return the Usuario if found, optional empty if not found
	 */
	Optional<Usuario> findById(Long id);
	/**
	 * Delete a Usuario by id.
	 *
	 * @param id the id of the Usuario
	 */
	void deleteById(Long id);
	/**
	 * Find a Usuario by email.
	 *
	 * @param email the email of the Usuario
	 * @return the Usuario if found, optional empty if not found
	 */
	Optional<Usuario> findByEmail(String email);

	/**
	 * Find all usuarios with a specific role.
	 *
	 * @param role the role to search for
	 * @return list of usuarios with the specified role
	 */
	List<Usuario> getUsuariosByRole(String role);
	/**
	 * Check if a Usuario exists by id.
	 *
	 * @param id the id of the Usuario
	 * @return true if the Usuario exists, false otherwise
	 */
	boolean existsById(Long id);

	/**
	 * Search usuarios by term (nombre, apellido, email).
	 *
	 * @param searchTerm the search term
	 * @return list of usuarios matching the search term
	 */
	List<Usuario> searchUsuarios(String searchTerm);

	/**
	 * Find usuarios by role ID.
	 *
	 * @param roleId the role ID
	 * @return list of usuarios with the specified role
	 */
	List<Usuario> findByRolId(Long roleId);
}