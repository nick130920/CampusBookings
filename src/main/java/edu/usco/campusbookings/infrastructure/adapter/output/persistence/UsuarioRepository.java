package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository.SpringDataUsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link UsuarioRepositoryPort} interface using Spring Data JPA.
 */
@Repository
public class UsuarioRepository implements UsuarioRepositoryPort {

	private final SpringDataUsuarioRepository springDataUsuarioRepository;

	/**
	 * Constructs a new instance of {@link UsuarioRepository}.
	 *
	 * @param springDataUsuarioRepository the Spring Data JPA repository for {@link Usuario}
	 */
	public UsuarioRepository(SpringDataUsuarioRepository springDataUsuarioRepository) {
		this.springDataUsuarioRepository = springDataUsuarioRepository;
	}

	/**
	 * Saves a list of usuarios to the repository.
	 *
	 * @param usuarios the list of usuarios to be saved
	 * @return the list of saved usuarios
	 */
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
	 * Returns a list of all usuarios in the repository.
	 *
	 * @return a list of all usuarios
	 */
	@Override
	public List<Usuario> findAll() {
		return springDataUsuarioRepository.findAll();
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
	 * Deletes a Usuario by its ID.
	 *
	 * @param id the ID of the Usuario to be deleted
	 */
	@Override
	public void deleteById(Long id) {
		springDataUsuarioRepository.deleteById(id);
	}

}