package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.input.UbicacionUseCase;
import edu.usco.campusbookings.application.port.output.UbicacionRepositoryPort;
import edu.usco.campusbookings.domain.model.Ubicacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing Ubicacion entities.
 */
@Service
@RequiredArgsConstructor
public class UbicacionService implements UbicacionUseCase {

    private final UbicacionRepositoryPort ubicacionRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Ubicacion> findAll() {
        return ubicacionRepositoryPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ubicacion> findByNombre(String nombre) {
        return ubicacionRepositoryPort.findByNombre(nombre);
    }

    @Override
    @Transactional
    public Ubicacion save(Ubicacion ubicacion) {
        // Check if a location with the same name already exists
        if (ubicacion.getNombre() != null && 
            ubicacionRepositoryPort.existsByNombre(ubicacion.getNombre())) {
            throw new IllegalArgumentException("Ya existe una ubicación con el nombre: " + ubicacion.getNombre());
        }
        return ubicacionRepositoryPort.save(ubicacion);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        // Check if the location is being used by any escenario before deleting
        // This would require adding a method to check references in Escenario
        // For now, we'll just delete it
        ubicacionRepositoryPort.deleteById(id);
    }
}
