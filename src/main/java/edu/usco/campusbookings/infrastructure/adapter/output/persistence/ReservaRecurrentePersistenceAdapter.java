package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.ReservaRecurrentePersistencePort;
import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.ReservaRecurrenteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservaRecurrentePersistenceAdapter implements ReservaRecurrentePersistencePort {

    private final ReservaRecurrenteJpaRepository repository;

    @Override
    public ReservaRecurrente save(ReservaRecurrente reservaRecurrente) {
        return repository.save(reservaRecurrente);
    }

    @Override
    public Optional<ReservaRecurrente> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ReservaRecurrente> findByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<ReservaRecurrente> findByUsuarioIdAndActiva(Long usuarioId, Boolean activa) {
        return repository.findByUsuarioIdAndActiva(usuarioId, activa);
    }

    @Override
    public List<ReservaRecurrente> findByActiva(Boolean activa) {
        return repository.findByActiva(activa);
    }

    @Override
    public List<ReservaRecurrente> findByEscenarioId(Long escenarioId) {
        return repository.findByEscenarioId(escenarioId);
    }

    @Override
    public List<ReservaRecurrente> findByEscenarioIdAndActiva(Long escenarioId, Boolean activa) {
        return repository.findByEscenarioIdAndActiva(escenarioId, activa);
    }

    @Override
    public List<ReservaRecurrente> findActivasParaGeneracion() {
        return repository.findActivasParaGeneracion();
    }

    @Override
    public List<ReservaRecurrente> findByFechaFinLessThanEqual(LocalDate fecha) {
        return repository.findByFechaFinLessThanEqual(fecha);
    }

    @Override
    public Long countReservasGeneradas(Long reservaRecurrenteId) {
        return repository.countReservasGeneradas(reservaRecurrenteId);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
