package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link RolRepositoryPort} interface using Spring Data JPA.
 */
@Repository
public class RolRepository implements RolRepositoryPort {

	private final SpringDataRolRepository springDataRolRepository;

	/**
	 * Constructs a new instance of {@link RolRepository}.
	 *
	 * @param springDataRolRepository the Spring Data JPA repository for {@link Rol}
	 */
	public RolRepository(SpringDataRolRepository springDataRolRepository) {
		this.springDataRolRepository = springDataRolRepository;
	}

	/**
	 * Saves a list of rols to the repository.
	 *
	 * @param rols the list of rols to be saved
	 * @return the list of saved rols
	 */
	@Override
	public List<Rol> saveAll(List<Rol> rols) {
		return springDataRolRepository.saveAll(rols);
	}

	/**
	 * Saves a Rol to the repository.
	 *
	 * @param rol the Rol to be saved
	 * @return the saved Rol
	 */
	@Override
	public Rol save(Rol rol) {
		return springDataRolRepository.save(rol);
	}

	/**
	 * Returns a list of all rols in the repository.
	 *
	 * @return a list of all rols
	 */
	@Override
	public List<Rol> findAll() {
		return springDataRolRepository.findAll();
	}

	/**
	 * Returns a Rol by its ID if the Rol exists in the repository.
	 *
	 * @param id the ID of the Rol to be found
	 * @return an optional containing the Rol if found, empty otherwise
	 */
	@Override
	public Optional<Rol> findById(Long id) {
		return springDataRolRepository.findById(id);
	}

	/**
	 * Deletes a Rol by its ID.
	 *
	 * @param id the ID of the Rol to be deleted
	 */
	@Override
	public void deleteById(Long id) {
		springDataRolRepository.deleteById(id);
	}

	/**
	 * Returns a Rol by its nombre if the Rol exists in the repository.
	 *
	 * @param nombre the nombre of the Rol to be found
	 * @return an optional containing the Rol if found, empty otherwise
	 */
	@Override
	public Optional<Rol> findByNombre(String nombre) {
		return springDataRolRepository.findByNombre(nombre);
	}

}