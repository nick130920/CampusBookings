package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.EstadoReservaRepositoryPort;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.EstadoReservaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EstadoReservaRepository implements EstadoReservaRepositoryPort {

    private final EstadoReservaJpaRepository estadoReservaJpaRepository;

    @Override
    public EstadoReserva save(EstadoReserva estadoReserva) {
        return estadoReservaJpaRepository.save(estadoReserva);
    }

    @Override
    public List<EstadoReserva> saveAll(List<EstadoReserva> estadosReserva) {
        return estadoReservaJpaRepository.saveAll(estadosReserva);
    }

    @Override
    public Optional<EstadoReserva> findById(Long id) {
        return estadoReservaJpaRepository.findById(id);
    }

    @Override
    public Optional<EstadoReserva> findByNombre(String nombre) {
        return estadoReservaJpaRepository.findByNombre(nombre);
    }

    @Override
    public List<EstadoReserva> findAll() {
        return estadoReservaJpaRepository.findAll();
    }

    @Override
    public List<EstadoReserva> findAll(Pageable pageable) {
        return estadoReservaJpaRepository.findAll(pageable).toList();
    }

    @Override
    public List<EstadoReserva> findAllByNombreContaining(String nombre, Pageable pageable) {
        return estadoReservaJpaRepository.findAllByNombreContaining(nombre, pageable).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return estadoReservaJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return estadoReservaJpaRepository.existsByNombre(nombre);
    }

    @Override
    public void deleteById(Long id) {
        estadoReservaJpaRepository.deleteById(id);
    }
}
