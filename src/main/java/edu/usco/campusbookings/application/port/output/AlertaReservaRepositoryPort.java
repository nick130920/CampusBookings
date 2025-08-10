package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AlertaReservaRepositoryPort {

    AlertaReserva save(AlertaReserva alertaReserva);

    Optional<AlertaReserva> findById(Long id);

    List<AlertaReserva> findAll();

    List<AlertaReserva> findByReservaId(Long reservaId);

    List<AlertaReserva> findByReservaUsuarioId(Long usuarioId);

    List<AlertaReserva> findByEstado(AlertaReserva.EstadoAlerta estado);

    List<AlertaReserva> findByTipo(AlertaReserva.TipoAlerta tipo);

    /**
     * Encuentra alertas pendientes que deben ser enviadas
     */
    List<AlertaReserva> findAlertasPendientesParaEnvio(LocalDateTime fechaLimite);

    /**
     * Encuentra alertas programadas para una fecha específica
     */
    List<AlertaReserva> findAlertasProgramadasPara(LocalDateTime fecha);

    /**
     * Encuentra alertas vencidas que no fueron enviadas
     */
    List<AlertaReserva> findAlertasVencidas();

    /**
     * Encuentra alertas fallidas que pueden ser reintentadas
     */
    List<AlertaReserva> findAlertasFallidasParaReintentar(int maxIntentos);

    /**
     * Elimina alertas asociadas a una reserva
     */
    void deleteByReserva(Reserva reserva);

    /**
     * Cuenta alertas por estado
     */
    long countByEstado(AlertaReserva.EstadoAlerta estado);

    /**
     * Cuenta alertas por tipo
     */
    long countByTipo(AlertaReserva.TipoAlerta tipo);

    /**
     * Busca si ya existe una alerta para una reserva y tipo específico
     */
    boolean existsByReservaAndTipo(Reserva reserva, AlertaReserva.TipoAlerta tipo);

    /**
     * Encuentra alertas por rango de fechas
     */
    List<AlertaReserva> findByFechaEnvioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Elimina alertas por ID
     */
    void deleteById(Long id);

    /**
     * Elimina múltiples alertas
     */
    void deleteAll(List<AlertaReserva> alertas);
}
