package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link UsuarioRepositoryPort} interface using Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class UsuarioRepository implements UsuarioRepositoryPort {

	private final SpringDataUsuarioRepository springDataUsuarioRepository;

	@Override
	public List<Usuario> saveAll(List<Usuario> usuarios) {
		return springDataUsuarioRepository.saveAll(usuarios);
	}

	/**
	 * Saves a Usuario to the repository.
	 *
	 * @param usuario the Usuario to be saved
	 * @return the saved Usuario
	 */
	@Override
	public Usuario save(Usuario usuario) {
		return springDataUsuarioRepository.save(usuario);
	}

	/**
	 * Returns a Usuario by its ID if the Usuario exists in the repository.
	 *
	 * @param id the ID of the Usuario to be found
	 * @return an optional containing the Usuario if found, empty otherwise
	 */
	@Override
	public Optional<Usuario> findById(Long id) {
		return springDataUsuarioRepository.findById(id);
	}

	/**
	 * Returns a Usuario by its email if the Usuario exists in the repository.
	 *
	 * @param email the email of the Usuario to be found
	 * @return an optional containing the Usuario if found, empty otherwise
	 */
	@Override
	public Optional<Usuario> findByEmail(String email) {
		return springDataUsuarioRepository.findByEmail(email);
	}

	/**
	 * Returns a list of all usuarios in the repository.
	 *
	 * @return a list of all usuarios
	 */
	@Override
	public List<Usuario> findAll() {
		return springDataUsuarioRepository.findAll();
	}

	/**
	 * Deletes a Usuario by its ID.
	 *
	 * @param id the ID of the Usuario to be deleted
	 */
	@Override
	public void deleteById(Long id) {
		springDataUsuarioRepository.deleteById(id);
	}

	/**
	 * Checks if a Usuario exists in the repository by its ID.
	 *
	 * @param id the ID of the Usuario to be checked
	 * @return true if the Usuario exists, false otherwise
	 */
	@Override
	public boolean existsById(Long id) {
		return springDataUsuarioRepository.existsById(id);
	}

	/**
	 * Finds all usuarios with a specific role.
	 *
	 * @param role the role to search for
	 * @return list of usuarios with the specified role
	 */
	@Override
	public List<Usuario> findByRolesContaining(String role) {
		return springDataUsuarioRepository.findByRolesContaining(role);
	}

}