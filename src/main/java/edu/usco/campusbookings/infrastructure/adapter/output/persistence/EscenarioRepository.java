package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository.SpringDataEscenarioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class implements the {@link EscenarioRepositoryPort} interface using Spring Data JPA.
 */
@Repository
public class EscenarioRepository implements EscenarioRepositoryPort {

	private final SpringDataEscenarioRepository springDataEscenarioRepository;

	/**
	 * Constructs a new instance of {@link EscenarioRepository}.
	 *
	 * @param springDataEscenarioRepository the Spring Data JPA repository for {@link Escenario}
	 */
	public EscenarioRepository(SpringDataEscenarioRepository springDataEscenarioRepository) {
		this.springDataEscenarioRepository = springDataEscenarioRepository;
	}

	/**
	 * Saves a list of escenarios to the repository.
	 *
	 * @param escenarios the list of escenarios to be saved
	 * @return the list of saved escenarios
	 */
	@Override
	public List<Escenario> saveAll(List<Escenario> escenarios) {
		return springDataEscenarioRepository.saveAll(escenarios);
	}

	/**
	 * Saves a Escenario to the repository.
	 *
	 * @param escenario the Escenario to be saved
	 * @return the saved Escenario
	 */
	@Override
	public Escenario save(Escenario escenario) {
		return springDataEscenarioRepository.save(escenario);
	}

	/**
	 * Returns a list of all escenarios in the repository.
	 *
	 * @return a list of all escenarios
	 */
	@Override
	public List<Escenario> findAll() {
		return springDataEscenarioRepository.findAll();
	}

	/**
	 * Returns a Escenario by its ID if the Escenario exists in the repository.
	 *
	 * @param id the ID of the Escenario to be found
	 * @return an optional containing the Escenario if found, empty otherwise
	 */
	@Override
	public Optional<Escenario> findById(Long id) {
		return springDataEscenarioRepository.findById(id);
	}

	/**
	 * Deletes a Escenario by its ID.
	 *
	 * @param id the ID of the Escenario to be deleted
	 */
	@Override
	public void deleteById(Long id) {
		springDataEscenarioRepository.deleteById(id);
	}

}