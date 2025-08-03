package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.UbicacionRepositoryPort;
import edu.usco.campusbookings.domain.model.Ubicacion;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.UbicacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter for Ubicacion persistence operations.
 */
@Repository
@RequiredArgsConstructor
public class UbicacionRepository implements UbicacionRepositoryPort {

    private final UbicacionJpaRepository ubicacionJpaRepository;

    @Override
    public Ubicacion save(Ubicacion ubicacion) {
        return ubicacionJpaRepository.save(ubicacion);
    }

    @Override
    public Optional<Ubicacion> findById(Long id) {
        return ubicacionJpaRepository.findById(id);
    }

    @Override
    public Optional<Ubicacion> findByNombre(String nombre) {
        return ubicacionJpaRepository.findByNombre(nombre);
    }

    @Override
    public List<Ubicacion> findAll() {
        return ubicacionJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        ubicacionJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return ubicacionJpaRepository.existsByNombre(nombre);
    }
}
