package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.input.TipoEscenarioUseCase;
import edu.usco.campusbookings.application.port.output.TipoEscenarioRepositoryPort;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing TipoEscenario entities.
 */
@Service
@RequiredArgsConstructor
public class TipoEscenarioService implements TipoEscenarioUseCase {

    private final TipoEscenarioRepositoryPort tipoEscenarioRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<TipoEscenario> findAll() {
        return tipoEscenarioRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoEscenario> findByNombre(String nombre) {
        return tipoEscenarioRepositoryPort.findByNombre(nombre);
    }

    @Override
    @Transactional
    public TipoEscenario save(TipoEscenario tipoEscenario) {
        // Check if a tipo with the same name already exists
        if (tipoEscenario.getNombre() != null && 
            tipoEscenarioRepositoryPort.existsByNombre(tipoEscenario.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de escenario con el nombre: " + tipoEscenario.getNombre());
        }
        return tipoEscenarioRepositoryPort.save(tipoEscenario);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Check if the tipo is being used by any escenario before deleting
        // This would require adding a method to check references in Escenario
        // For now, we'll just delete it
        tipoEscenarioRepositoryPort.deleteById(id);
    }
}
