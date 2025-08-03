package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.ColaEsperaRepositoryPort;
import edu.usco.campusbookings.domain.model.ColaEspera;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataColaEsperaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link ColaEsperaRepositoryPort} interface using Spring Data JPA.
 */
@Repository
public class ColaEsperaRepository implements ColaEsperaRepositoryPort {

	private final SpringDataColaEsperaRepository springDataColaEsperaRepository;

	/**
	 * Constructs a new instance of {@link ColaEsperaRepository}.
	 *
	 * @param springDataColaEsperaRepository the Spring Data JPA repository for {@link ColaEspera}
	 */
	public ColaEsperaRepository(SpringDataColaEsperaRepository springDataColaEsperaRepository) {
		this.springDataColaEsperaRepository = springDataColaEsperaRepository;
	}

	/**
	 * Saves a list of colaesperas to the repository.
	 *
	 * @param colaesperas the list of colaesperas to be saved
	 * @return the list of saved colaesperas
	 */
	@Override
	public List<ColaEspera> saveAll(List<ColaEspera> colaesperas) {
		return springDataColaEsperaRepository.saveAll(colaesperas);
	}

	/**
	 * Saves a ColaEspera to the repository.
	 *
	 * @param colaespera the ColaEspera to be saved
	 * @return the saved ColaEspera
	 */
	@Override
	public ColaEspera save(ColaEspera colaespera) {
		return springDataColaEsperaRepository.save(colaespera);
	}

	/**
	 * Returns a list of all colaesperas in the repository.
	 *
	 * @return a list of all colaesperas
	 */
	@Override
	public List<ColaEspera> findAll() {
		return springDataColaEsperaRepository.findAll();
	}

	/**
	 * Returns a ColaEspera by its ID if the ColaEspera exists in the repository.
	 *
	 * @param id the ID of the ColaEspera to be found
	 * @return an optional containing the ColaEspera if found, empty otherwise
	 */
	@Override
	public Optional<ColaEspera> findById(Long id) {
		return springDataColaEsperaRepository.findById(id);
	}

	/**
	 * Deletes a ColaEspera by its ID.
	 *
	 * @param id the ID of the ColaEspera to be deleted
	 */
	@Override
	public void deleteById(Long id) {
		springDataColaEsperaRepository.deleteById(id);
	}

}