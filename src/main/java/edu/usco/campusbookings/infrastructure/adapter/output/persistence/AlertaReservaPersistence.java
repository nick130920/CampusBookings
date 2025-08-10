package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.AlertaReservaRepositoryPort;
import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository.AlertaReservaJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AlertaReservaPersistence implements AlertaReservaRepositoryPort {

    private final AlertaReservaJpaRepository alertaReservaJpaRepository;

    @Override
    public AlertaReserva save(AlertaReserva alertaReserva) {
        log.debug("Saving alerta reserva with ID: {}", alertaReserva.getId());
        return alertaReservaJpaRepository.save(alertaReserva);
    }

    @Override
    public Optional<AlertaReserva> findById(Long id) {
        log.debug("Finding alerta reserva by ID: {}", id);
        return alertaReservaJpaRepository.findById(id);
    }

    @Override
    public List<AlertaReserva> findAll() {
        log.debug("Finding all alerta reservas");
        return alertaReservaJpaRepository.findAll();
    }

    @Override
    public List<AlertaReserva> findByReservaId(Long reservaId) {
        log.debug("Finding alertas by reserva ID: {}", reservaId);
        return alertaReservaJpaRepository.findByReservaId(reservaId);
    }

    @Override
    public List<AlertaReserva> findByReservaUsuarioId(Long usuarioId) {
        log.debug("Finding alertas by usuario ID: {}", usuarioId);
        return alertaReservaJpaRepository.findByReservaUsuarioId(usuarioId);
    }

    @Override
    public List<AlertaReserva> findByEstado(AlertaReserva.EstadoAlerta estado) {
        log.debug("Finding alertas by estado: {}", estado);
        return alertaReservaJpaRepository.findByEstado(estado);
    }

    @Override
    public List<AlertaReserva> findByTipo(AlertaReserva.TipoAlerta tipo) {
        log.debug("Finding alertas by tipo: {}", tipo);
        return alertaReservaJpaRepository.findByTipo(tipo);
    }

    @Override
    public List<AlertaReserva> findAlertasPendientesParaEnvio(LocalDateTime fechaLimite) {
        log.debug("Finding alertas pendientes para envio hasta: {}", fechaLimite);
        return alertaReservaJpaRepository.findByEstadoInAndFechaEnvioLessThanEqual(
                List.of(AlertaReserva.EstadoAlerta.PENDIENTE, AlertaReserva.EstadoAlerta.PROGRAMADO),
                fechaLimite
        );
    }

    @Override
    public List<AlertaReserva> findAlertasProgramadasPara(LocalDateTime fecha) {
        log.debug("Finding alertas programadas para: {}", fecha);
        return alertaReservaJpaRepository.findByEstadoAndFechaEnvioBetween(
                AlertaReserva.EstadoAlerta.PROGRAMADO,
                fecha.minusMinutes(5),
                fecha.plusMinutes(5)
        );
    }

    @Override
    public List<AlertaReserva> findAlertasVencidas() {
        log.debug("Finding alertas vencidas");
        LocalDateTime ahora = LocalDateTime.now();
        return alertaReservaJpaRepository.findByEstadoInAndFechaEnvioLessThan(
                List.of(AlertaReserva.EstadoAlerta.PENDIENTE, AlertaReserva.EstadoAlerta.PROGRAMADO),
                ahora
        );
    }

    @Override
    public List<AlertaReserva> findAlertasFallidasParaReintentar(int maxIntentos) {
        log.debug("Finding alertas fallidas para reintentar con max intentos: {}", maxIntentos);
        return alertaReservaJpaRepository.findByEstadoAndIntentosEnvioLessThan(
                AlertaReserva.EstadoAlerta.FALLIDO, maxIntentos);
    }

    @Override
    public void deleteByReserva(Reserva reserva) {
        log.debug("Deleting alertas by reserva ID: {}", reserva.getId());
        alertaReservaJpaRepository.deleteByReserva(reserva);
    }

    @Override
    public long countByEstado(AlertaReserva.EstadoAlerta estado) {
        log.debug("Counting alertas by estado: {}", estado);
        return alertaReservaJpaRepository.countByEstado(estado);
    }

    @Override
    public long countByTipo(AlertaReserva.TipoAlerta tipo) {
        log.debug("Counting alertas by tipo: {}", tipo);
        return alertaReservaJpaRepository.countByTipo(tipo);
    }

    @Override
    public boolean existsByReservaAndTipo(Reserva reserva, AlertaReserva.TipoAlerta tipo) {
        log.debug("Checking if exists alerta for reserva {} and tipo {}", reserva.getId(), tipo);
        return alertaReservaJpaRepository.existsByReservaAndTipo(reserva, tipo);
    }

    @Override
    public List<AlertaReserva> findByFechaEnvioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Finding alertas by fecha envio between {} and {}", fechaInicio, fechaFin);
        return alertaReservaJpaRepository.findByFechaEnvioBetween(fechaInicio, fechaFin);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Deleting alerta by ID: {}", id);
        alertaReservaJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<AlertaReserva> alertas) {
        log.debug("Deleting {} alertas", alertas.size());
        alertaReservaJpaRepository.deleteAll(alertas);
    }
}
