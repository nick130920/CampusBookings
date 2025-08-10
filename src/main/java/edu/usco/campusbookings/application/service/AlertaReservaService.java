package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ConfigurarAlertaRequest;
import edu.usco.campusbookings.application.dto.response.AlertaReservaResponse;
import edu.usco.campusbookings.application.exception.AlertaNotFoundException;
import edu.usco.campusbookings.application.mapper.AlertaReservaMapper;
import edu.usco.campusbookings.application.port.input.AlertaReservaUseCase;
import edu.usco.campusbookings.application.port.output.AlertaReservaRepositoryPort;
import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.domain.model.AlertaReserva;
import edu.usco.campusbookings.domain.model.Reserva;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaReservaService implements AlertaReservaUseCase {

    private final AlertaReservaRepositoryPort alertaRepositoryPort;
    private final AlertaReservaMapper alertaMapper;
    private final EmailServicePort emailService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public List<AlertaReservaResponse> crearAlertasParaReserva(Reserva reserva) {
        log.info("Creando alertas automáticas para reserva ID: {}", reserva.getId());
        
        List<AlertaReserva> alertas = new ArrayList<>();
        LocalDateTime fechaInicio = reserva.getFechaInicio();
        
        // Alerta 24 horas antes
        if (fechaInicio.isAfter(LocalDateTime.now().plusHours(24))) {
            AlertaReserva alerta24h = crearAlerta(reserva, AlertaReserva.TipoAlerta.RECORDATORIO_24H, 
                    fechaInicio.minusHours(24));
            alertas.add(alerta24h);
        }
        
        // Alerta 2 horas antes
        if (fechaInicio.isAfter(LocalDateTime.now().plusHours(2))) {
            AlertaReserva alerta2h = crearAlerta(reserva, AlertaReserva.TipoAlerta.RECORDATORIO_2H, 
                    fechaInicio.minusHours(2));
            alertas.add(alerta2h);
        }
        
        // Alerta 30 minutos antes
        if (fechaInicio.isAfter(LocalDateTime.now().plusMinutes(30))) {
            AlertaReserva alerta30min = crearAlerta(reserva, AlertaReserva.TipoAlerta.RECORDATORIO_30MIN, 
                    fechaInicio.minusMinutes(30));
            alertas.add(alerta30min);
        }
        
        // Guardar todas las alertas
        List<AlertaReserva> alertasGuardadas = alertas.stream()
                .map(alertaRepositoryPort::save)
                .collect(Collectors.toList());
        
        log.info("Creadas {} alertas automáticas para reserva ID: {}", alertasGuardadas.size(), reserva.getId());
        
        return alertaMapper.toDtoList(alertasGuardadas);
    }

    @Override
    @Transactional
    public List<AlertaReservaResponse> configurarAlertas(ConfigurarAlertaRequest request) {
        log.info("Configurando alertas personalizadas");
        // Implementación para configuración personalizada de alertas
        // Por ahora retornamos lista vacía
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaReservaResponse> obtenerTodasLasAlertas() {
        log.info("Obteniendo todas las alertas");
        List<AlertaReserva> alertas = alertaRepositoryPort.findAll();
        return alertaMapper.toDtoList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaReservaResponse> obtenerAlertasPorReserva(Long reservaId) {
        log.info("Obteniendo alertas para reserva ID: {}", reservaId);
        List<AlertaReserva> alertas = alertaRepositoryPort.findByReservaId(reservaId);
        return alertaMapper.toDtoList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaReservaResponse> obtenerAlertasPorUsuario(Long usuarioId) {
        log.info("Obteniendo alertas para usuario ID: {}", usuarioId);
        List<AlertaReserva> alertas = alertaRepositoryPort.findByReservaUsuarioId(usuarioId);
        return alertaMapper.toDtoList(alertas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaReservaResponse> obtenerAlertasPendientes() {
        log.info("Obteniendo alertas pendientes");
        List<AlertaReserva> alertas = alertaRepositoryPort.findByEstado(AlertaReserva.EstadoAlerta.PENDIENTE);
        return alertaMapper.toDtoList(alertas);
    }

    @Override
    @Transactional
    public void procesarAlertasPendientes() {
        log.info("Procesando alertas pendientes para envío");
        
        LocalDateTime ahora = LocalDateTime.now();
        List<AlertaReserva> alertasParaEnviar = alertaRepositoryPort.findAlertasPendientesParaEnvio(ahora);
        
        log.info("Encontradas {} alertas para enviar", alertasParaEnviar.size());
        
        for (AlertaReserva alerta : alertasParaEnviar) {
            try {
                enviarAlertaInterna(alerta);
                log.info("Alerta {} enviada exitosamente", alerta.getId());
            } catch (Exception e) {
                log.error("Error enviando alerta {}: {}", alerta.getId(), e.getMessage());
                alerta.marcarComoFallida(e.getMessage());
                alertaRepositoryPort.save(alerta);
            }
        }
    }

    @Override
    @Transactional
    public AlertaReservaResponse enviarAlerta(Long alertaId) {
        log.info("Enviando alerta ID: {}", alertaId);
        
        AlertaReserva alerta = alertaRepositoryPort.findById(alertaId)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + alertaId));
        
        if (!alerta.puedeSerEnviada()) {
            throw new IllegalStateException("La alerta no puede ser enviada en su estado actual: " + alerta.getEstado());
        }
        
        try {
            enviarAlertaInterna(alerta);
            alerta = alertaRepositoryPort.save(alerta);
            log.info("Alerta {} enviada manualmente", alertaId);
        } catch (Exception e) {
            log.error("Error enviando alerta {}: {}", alertaId, e.getMessage());
            alerta.marcarComoFallida(e.getMessage());
            alerta = alertaRepositoryPort.save(alerta);
            throw new RuntimeException("Error enviando alerta: " + e.getMessage());
        }
        
        return alertaMapper.toDto(alerta);
    }

    @Override
    @Transactional
    public AlertaReservaResponse cancelarAlerta(Long alertaId) {
        log.info("Cancelando alerta ID: {}", alertaId);
        
        AlertaReserva alerta = alertaRepositoryPort.findById(alertaId)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + alertaId));
        
        if (alerta.getEstado() == AlertaReserva.EstadoAlerta.ENVIADO) {
            throw new IllegalStateException("No se puede cancelar una alerta ya enviada");
        }
        
        alerta.setEstado(AlertaReserva.EstadoAlerta.CANCELADO);
        alerta = alertaRepositoryPort.save(alerta);
        
        log.info("Alerta {} cancelada exitosamente", alertaId);
        return alertaMapper.toDto(alerta);
    }

    @Override
    @Transactional
    public AlertaReservaResponse reenviarAlerta(Long alertaId) {
        log.info("Reenviando alerta ID: {}", alertaId);
        
        AlertaReserva alerta = alertaRepositoryPort.findById(alertaId)
                .orElseThrow(() -> new AlertaNotFoundException("Alerta no encontrada con ID: " + alertaId));
        
        if (alerta.getEstado() != AlertaReserva.EstadoAlerta.FALLIDO) {
            throw new IllegalStateException("Solo se pueden reenviar alertas fallidas");
        }
        
        alerta.setEstado(AlertaReserva.EstadoAlerta.PENDIENTE);
        alerta.setMotivoFallo(null);
        
        try {
            enviarAlertaInterna(alerta);
            alerta = alertaRepositoryPort.save(alerta);
            log.info("Alerta {} reenviada exitosamente", alertaId);
        } catch (Exception e) {
            log.error("Error reenviando alerta {}: {}", alertaId, e.getMessage());
            alerta.marcarComoFallida(e.getMessage());
            alerta = alertaRepositoryPort.save(alerta);
            throw new RuntimeException("Error reenviando alerta: " + e.getMessage());
        }
        
        return alertaMapper.toDto(alerta);
    }

    @Override
    @Transactional
    public void eliminarAlertasDeReservaCancelada(Long reservaId) {
        log.info("Eliminando alertas de reserva cancelada ID: {}", reservaId);
        
        List<AlertaReserva> alertas = alertaRepositoryPort.findByReservaId(reservaId);
        List<AlertaReserva> alertasACancelar = alertas.stream()
                .filter(alerta -> alerta.getEstado() == AlertaReserva.EstadoAlerta.PENDIENTE || 
                                alerta.getEstado() == AlertaReserva.EstadoAlerta.PROGRAMADO)
                .collect(Collectors.toList());
        
        for (AlertaReserva alerta : alertasACancelar) {
            alerta.setEstado(AlertaReserva.EstadoAlerta.CANCELADO);
            alertaRepositoryPort.save(alerta);
        }
        
        log.info("Canceladas {} alertas de reserva {}", alertasACancelar.size(), reservaId);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasAlertas obtenerEstadisticas() {
        log.info("Obteniendo estadísticas de alertas");
        
        long totalAlertas = alertaRepositoryPort.findAll().size();
        long alertasPendientes = alertaRepositoryPort.countByEstado(AlertaReserva.EstadoAlerta.PENDIENTE);
        long alertasEnviadas = alertaRepositoryPort.countByEstado(AlertaReserva.EstadoAlerta.ENVIADO);
        long alertasFallidas = alertaRepositoryPort.countByEstado(AlertaReserva.EstadoAlerta.FALLIDO);
        long alertasCanceladas = alertaRepositoryPort.countByEstado(AlertaReserva.EstadoAlerta.CANCELADO);
        
        return new EstadisticasAlertas(totalAlertas, alertasPendientes, alertasEnviadas, 
                                     alertasFallidas, alertasCanceladas);
    }

    @Override
    @Transactional
    public void limpiarAlertasVencidas() {
        log.info("Limpiando alertas vencidas");
        
        List<AlertaReserva> alertasVencidas = alertaRepositoryPort.findAlertasVencidas();
        
        for (AlertaReserva alerta : alertasVencidas) {
            if (alerta.getEstado() == AlertaReserva.EstadoAlerta.PENDIENTE) {
                alerta.setEstado(AlertaReserva.EstadoAlerta.CANCELADO);
                alertaRepositoryPort.save(alerta);
            }
        }
        
        log.info("Procesadas {} alertas vencidas", alertasVencidas.size());
    }

    /**
     * Crea una nueva alerta
     */
    private AlertaReserva crearAlerta(Reserva reserva, AlertaReserva.TipoAlerta tipo, LocalDateTime fechaEnvio) {
        String mensaje = generarMensajeAlerta(reserva, tipo);
        
        return AlertaReserva.builder()
                .reserva(reserva)
                .tipo(tipo)
                .fechaEnvio(fechaEnvio)
                .estado(AlertaReserva.EstadoAlerta.PROGRAMADO)
                .mensaje(mensaje)
                .canalEnvio("EMAIL,WEBSOCKET")
                .intentosEnvio(0)
                .build();
    }

    /**
     * Genera el mensaje personalizado para cada tipo de alerta
     */
    private String generarMensajeAlerta(Reserva reserva, AlertaReserva.TipoAlerta tipo) {
        String escenario = reserva.getEscenario().getNombre();
        String fecha = reserva.getFechaInicio().toLocalDate().toString();
        String hora = reserva.getFechaInicio().toLocalTime().toString();
        
        return switch (tipo) {
            case RECORDATORIO_24H -> String.format("Recordatorio: Tu reserva de %s es mañana (%s a las %s)", 
                    escenario, fecha, hora);
            case RECORDATORIO_2H -> String.format("Recordatorio: Tu reserva de %s comienza en 2 horas (%s)", 
                    escenario, hora);
            case RECORDATORIO_30MIN -> String.format("¡Última llamada! Tu reserva de %s comienza en 30 minutos", 
                    escenario);
            case CONFIRMACION_LLEGADA -> String.format("¿Ya llegaste a %s? Confirma tu asistencia", escenario);
            default -> String.format("Notificación sobre tu reserva de %s", escenario);
        };
    }

    /**
     * Envía una alerta internamente
     */
    private void enviarAlertaInterna(AlertaReserva alerta) {
        try {
            // Enviar por email
            if (alerta.getCanalEnvio().contains("EMAIL")) {
                emailService.enviarCorreoAlertaReserva(alerta);
            }
            
            // Enviar por WebSocket
            if (alerta.getCanalEnvio().contains("WEBSOCKET")) {
                notificationService.enviarAlertaWebSocket(alerta);
            }
            
            alerta.marcarComoEnviada();
            
        } catch (Exception e) {
            log.error("Error enviando alerta {}: {}", alerta.getId(), e.getMessage());
            throw e;
        }
    }
}
