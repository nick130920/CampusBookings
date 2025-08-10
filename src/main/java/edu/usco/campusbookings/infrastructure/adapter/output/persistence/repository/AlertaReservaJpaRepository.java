package edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository;

import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertaReservaJpaRepository extends JpaRepository<AlertaReserva, Long> {

    /**
     * Encuentra alertas por ID de reserva
     */
    List<AlertaReserva> findByReservaId(Long reservaId);

    /**
     * Encuentra alertas por ID de usuario de la reserva
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.reserva.usuario.id = :usuarioId")
    List<AlertaReserva> findByReservaUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Encuentra alertas por estado
     */
    List<AlertaReserva> findByEstado(AlertaReserva.EstadoAlerta estado);

    /**
     * Encuentra alertas por tipo
     */
    List<AlertaReserva> findByTipo(AlertaReserva.TipoAlerta tipo);

    /**
     * Encuentra alertas con estados específicos y fecha de envío menor o igual a la fecha límite
     */
    List<AlertaReserva> findByEstadoInAndFechaEnvioLessThanEqual(
            List<AlertaReserva.EstadoAlerta> estados, LocalDateTime fechaLimite);

    /**
     * Encuentra alertas programadas para una fecha específica (con margen de 5 minutos)
     */
    List<AlertaReserva> findByEstadoAndFechaEnvioBetween(
            AlertaReserva.EstadoAlerta estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Encuentra alertas vencidas (pendientes o programadas con fecha de envío pasada)
     */
    List<AlertaReserva> findByEstadoInAndFechaEnvioLessThan(
            List<AlertaReserva.EstadoAlerta> estados, LocalDateTime fecha);

    /**
     * Encuentra alertas fallidas con intentos menores al máximo
     */
    List<AlertaReserva> findByEstadoAndIntentosEnvioLessThan(
            AlertaReserva.EstadoAlerta estado, int maxIntentos);

    /**
     * Elimina todas las alertas de una reserva
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
     * Verifica si existe una alerta para una reserva y tipo específico
     */
    boolean existsByReservaAndTipo(Reserva reserva, AlertaReserva.TipoAlerta tipo);

    /**
     * Encuentra alertas por rango de fechas de envío
     */
    List<AlertaReserva> findByFechaEnvioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Encuentra alertas activas (no canceladas) para una reserva
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.reserva.id = :reservaId AND a.estado != 'CANCELADO'")
    List<AlertaReserva> findAlertasActivasByReservaId(@Param("reservaId") Long reservaId);

    /**
     * Encuentra alertas próximas a vencer (en las próximas 2 horas)
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.estado IN ('PENDIENTE', 'PROGRAMADO') " +
           "AND a.fechaEnvio BETWEEN :ahora AND :dosDespues")
    List<AlertaReserva> findAlertasProximasAVencer(
            @Param("ahora") LocalDateTime ahora, @Param("dosDespues") LocalDateTime dosDespues);

    /**
     * Encuentra alertas pendientes ordenadas por fecha de envío
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.estado = 'PENDIENTE' ORDER BY a.fechaEnvio ASC")
    List<AlertaReserva> findAlertasPendientesOrderByFechaEnvio();

    /**
     * Encuentra alertas para procesamiento masivo
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.estado IN ('PENDIENTE', 'PROGRAMADO') " +
           "AND a.fechaEnvio <= :fechaLimite ORDER BY a.fechaEnvio ASC")
    List<AlertaReserva> findAlertasParaProcesamiento(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Encuentra estadísticas de alertas por usuario
     */
    @Query("SELECT a.reserva.usuario.id, COUNT(a) FROM AlertaReserva a " +
           "WHERE a.createdDate >= :fechaDesde GROUP BY a.reserva.usuario.id")
    List<Object[]> findEstadisticasAlertasPorUsuario(@Param("fechaDesde") LocalDateTime fechaDesde);

    /**
     * Encuentra alertas que requieren limpieza (muy antiguas y completadas)
     */
    @Query("SELECT a FROM AlertaReserva a WHERE a.estado IN ('ENVIADO', 'CANCELADO') " +
           "AND a.modifiedDate < :fechaLimite")
    List<AlertaReserva> findAlertasParaLimpieza(@Param("fechaLimite") LocalDateTime fechaLimite);
}
