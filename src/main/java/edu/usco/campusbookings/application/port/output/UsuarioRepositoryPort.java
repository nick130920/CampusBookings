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
}