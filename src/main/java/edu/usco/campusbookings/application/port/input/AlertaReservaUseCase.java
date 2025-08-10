package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ConfigurarAlertaRequest;
import edu.usco.campusbookings.application.dto.response.AlertaReservaResponse;
import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaReservaUseCase {

    /**
     * Crea alertas automáticas para una reserva
     */
    List<AlertaReservaResponse> crearAlertasParaReserva(Reserva reserva);

    /**
     * Configura alertas personalizadas
     */
    List<AlertaReservaResponse> configurarAlertas(ConfigurarAlertaRequest request);

    /**
     * Obtiene todas las alertas
     */
    List<AlertaReservaResponse> obtenerTodasLasAlertas();

    /**
     * Obtiene alertas por ID de reserva
     */
    List<AlertaReservaResponse> obtenerAlertasPorReserva(Long reservaId);

    /**
     * Obtiene alertas por usuario
     */
    List<AlertaReservaResponse> obtenerAlertasPorUsuario(Long usuarioId);

    /**
     * Obtiene alertas pendientes de envío
     */
    List<AlertaReservaResponse> obtenerAlertasPendientes();

    /**
     * Procesa alertas que deben ser enviadas
     */
    void procesarAlertasPendientes();

    /**
     * Envía una alerta específica
     */
    AlertaReservaResponse enviarAlerta(Long alertaId);

    /**
     * Cancela una alerta
     */
    AlertaReservaResponse cancelarAlerta(Long alertaId);

    /**
     * Reenvía una alerta fallida
     */
    AlertaReservaResponse reenviarAlerta(Long alertaId);

    /**
     * Elimina alertas de una reserva cancelada
     */
    void eliminarAlertasDeReservaCancelada(Long reservaId);

    /**
     * Obtiene estadísticas de alertas
     */
    EstadisticasAlertas obtenerEstadisticas();

    /**
     * Limpia alertas vencidas
     */
    void limpiarAlertasVencidas();

    /**
     * Clase para estadísticas de alertas
     */
    class EstadisticasAlertas {
        private long totalAlertas;
        private long alertasPendientes;
        private long alertasEnviadas;
        private long alertasFallidas;
        private long alertasCanceladas;

        // Constructor, getters y setters
        public EstadisticasAlertas(long totalAlertas, long alertasPendientes, 
                                 long alertasEnviadas, long alertasFallidas, long alertasCanceladas) {
            this.totalAlertas = totalAlertas;
            this.alertasPendientes = alertasPendientes;
            this.alertasEnviadas = alertasEnviadas;
            this.alertasFallidas = alertasFallidas;
            this.alertasCanceladas = alertasCanceladas;
        }

        // Getters
        public long getTotalAlertas() { return totalAlertas; }
        public long getAlertasPendientes() { return alertasPendientes; }
        public long getAlertasEnviadas() { return alertasEnviadas; }
        public long getAlertasFallidas() { return alertasFallidas; }
        public long getAlertasCanceladas() { return alertasCanceladas; }
    }
}
