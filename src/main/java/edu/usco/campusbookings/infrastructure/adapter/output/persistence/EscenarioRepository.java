package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.EscenarioRepositoryPort;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataEscenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for the Escenario repository that implements the EscenarioRepositoryPort interface.
 * This class acts as an adapter between the domain layer and the Spring Data JPA repository.
 */
@Repository
@RequiredArgsConstructor
public class EscenarioRepository implements EscenarioRepositoryPort {

    private final SpringDataEscenarioRepository springDataEscenarioRepository;

    @Override
    public List<Escenario> saveAll(List<Escenario> escenarios) {
        return springDataEscenarioRepository.saveAll(escenarios);
    }

    @Override
    public Escenario save(Escenario escenario) {
        return springDataEscenarioRepository.save(escenario);
    }

    @Override
    public List<Escenario> findAll() {
        return springDataEscenarioRepository.findAll();
    }

    @Override
    public Optional<Escenario> findById(Long id) {
        return springDataEscenarioRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        springDataEscenarioRepository.deleteById(id);
    }
}