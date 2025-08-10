package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.input.AlertaReservaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio para el procesamiento automático de alertas mediante tareas programadas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaSchedulerService {

    private final AlertaReservaUseCase alertaReservaUseCase;

    /**
     * Procesa alertas pendientes cada 5 minutos
     * Revisa las alertas que deben ser enviadas y las procesa automáticamente
     */
    @Scheduled(fixedRate = 300000) // 5 minutos = 300,000 ms
    public void procesarAlertasPendientes() {
        try {
            log.info("🔄 Iniciando procesamiento automático de alertas pendientes");
            alertaReservaUseCase.procesarAlertasPendientes();
            log.info("✅ Procesamiento de alertas pendientes completado");
        } catch (Exception e) {
            log.error("❌ Error en el procesamiento automático de alertas: {}", e.getMessage(), e);
        }
    }

    /**
     * Limpia alertas vencidas cada hora
     * Cancela alertas que ya pasaron su fecha de envío y no fueron procesadas
     */
    @Scheduled(fixedRate = 3600000) // 1 hora = 3,600,000 ms
    public void limpiarAlertasVencidas() {
        try {
            log.info("🧹 Iniciando limpieza de alertas vencidas");
            alertaReservaUseCase.limpiarAlertasVencidas();
            log.info("✅ Limpieza de alertas vencidas completada");
        } catch (Exception e) {
            log.error("❌ Error en la limpieza de alertas vencidas: {}", e.getMessage(), e);
        }
    }

    /**
     * Genera reporte de estadísticas de alertas cada 6 horas
     * Proporciona información sobre el rendimiento del sistema de alertas
     */
    @Scheduled(fixedRate = 21600000) // 6 horas = 21,600,000 ms
    public void generarEstadisticasAlertas() {
        try {
            log.info("📊 Generando estadísticas de alertas");
            var estadisticas = alertaReservaUseCase.obtenerEstadisticas();
            
            log.info("📈 Estadísticas de Alertas:");
            log.info("   • Total de alertas: {}", estadisticas.getTotalAlertas());
            log.info("   • Alertas pendientes: {}", estadisticas.getAlertasPendientes());
            log.info("   • Alertas enviadas: {}", estadisticas.getAlertasEnviadas());
            log.info("   • Alertas fallidas: {}", estadisticas.getAlertasFallidas());
            log.info("   • Alertas canceladas: {}", estadisticas.getAlertasCanceladas());
            
            // Calcular tasa de éxito
            if (estadisticas.getTotalAlertas() > 0) {
                double tasaExito = (double) estadisticas.getAlertasEnviadas() / estadisticas.getTotalAlertas() * 100;
                log.info("   • Tasa de éxito: {:.2f}%", tasaExito);
                
                if (tasaExito < 90) {
                    log.warn("⚠️ Tasa de éxito de alertas baja ({:.2f}%). Revisar sistema de notificaciones.", tasaExito);
                }
            }
            
        } catch (Exception e) {
            log.error("❌ Error generando estadísticas de alertas: {}", e.getMessage(), e);
        }
    }

    /**
     * Mantenimiento nocturno del sistema de alertas
     * Se ejecuta todos los días a las 2:00 AM para optimizar el rendimiento
     */
    @Scheduled(cron = "0 0 2 * * *") // Todos los días a las 2:00 AM
    public void mantenimientoNocturno() {
        try {
            log.info("🌙 Iniciando mantenimiento nocturno del sistema de alertas");
            
            // Limpiar alertas vencidas
            alertaReservaUseCase.limpiarAlertasVencidas();
            
            // Generar estadísticas completas
            var estadisticas = alertaReservaUseCase.obtenerEstadisticas();
            
            log.info("🌙 Mantenimiento nocturno completado:");
            log.info("   • Alertas procesadas en total: {}", estadisticas.getTotalAlertas());
            log.info("   • Sistema funcionando correctamente");
            
        } catch (Exception e) {
            log.error("❌ Error en el mantenimiento nocturno de alertas: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitoreo de salud del sistema cada 30 minutos
     * Verifica que el sistema de alertas esté funcionando correctamente
     */
    @Scheduled(fixedRate = 1800000) // 30 minutos = 1,800,000 ms
    public void monitorearSaludSistema() {
        try {
            var estadisticas = alertaReservaUseCase.obtenerEstadisticas();
            
            // Verificar si hay demasiadas alertas fallidas
            if (estadisticas.getAlertasFallidas() > 50) {
                log.warn("⚠️ Alto número de alertas fallidas detectado: {}. Revisar configuración de email/notificaciones.", 
                        estadisticas.getAlertasFallidas());
            }
            
            // Verificar si hay demasiadas alertas pendientes acumuladas
            if (estadisticas.getAlertasPendientes() > 100) {
                log.warn("⚠️ Alto número de alertas pendientes acumuladas: {}. Revisar procesamiento.", 
                        estadisticas.getAlertasPendientes());
            }
            
            log.debug("💚 Sistema de alertas funcionando normalmente - Pendientes: {}, Fallidas: {}", 
                    estadisticas.getAlertasPendientes(), estadisticas.getAlertasFallidas());
            
        } catch (Exception e) {
            log.error("❌ Error en el monitoreo de salud del sistema de alertas: {}", e.getMessage(), e);
        }
    }
}
