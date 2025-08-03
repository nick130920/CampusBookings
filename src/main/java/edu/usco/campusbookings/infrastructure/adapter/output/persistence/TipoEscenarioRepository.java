package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.TipoEscenarioRepositoryPort;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.TipoEscenarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for TipoEscenario persistence operations.
 */
@Repository
@RequiredArgsConstructor
public class TipoEscenarioRepository implements TipoEscenarioRepositoryPort {

    private final TipoEscenarioJpaRepository tipoEscenarioJpaRepository;

    @Override
    public TipoEscenario save(TipoEscenario tipoEscenario) {
        return tipoEscenarioJpaRepository.save(tipoEscenario);
    }

    @Override
    public Optional<TipoEscenario> findById(Long id) {
        return tipoEscenarioJpaRepository.findById(id);
    }

    @Override
    public Optional<TipoEscenario> findByNombre(String nombre) {
        return tipoEscenarioJpaRepository.findByNombre(nombre);
    }

    @Override
    public List<TipoEscenario> findAll() {
        return tipoEscenarioJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        tipoEscenarioJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return tipoEscenarioJpaRepository.existsByNombre(nombre);
    }
}
