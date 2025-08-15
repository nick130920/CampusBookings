package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.port.input.ReservaRecurrenteUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio programado para la generación automática de reservas recurrentes.
 * Se ejecuta diariamente para crear las reservas pendientes según las configuraciones activas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "campusbookings.scheduler.reservas-recurrentes.enabled", havingValue = "true", matchIfMissing = true)
public class ReservaRecurrenteSchedulerService {

    private final ReservaRecurrenteUseCase reservaRecurrenteUseCase;

    /**
     * Se ejecuta todos los días a las 6:00 AM para generar las reservas recurrentes pendientes
     */
    @Scheduled(cron = "0 0 6 * * *") // Todos los días a las 6:00 AM
    @Async
    public void generarReservasRecurrentesDiarias() {
        log.info("=== Iniciando generación automática de reservas recurrentes ===");
        
        try {
            reservaRecurrenteUseCase.generarReservasPendientes();
            log.info("=== Generación automática de reservas recurrentes completada exitosamente ===");
            
        } catch (Exception e) {
            log.error("Error durante la generación automática de reservas recurrentes: {}", e.getMessage(), e);
        }
    }

    /**
     * Se ejecuta cada 2 horas durante horario laboral para verificar y generar reservas
     * que puedan haberse perdido o necesiten generación inmediata
     */
    @Scheduled(cron = "0 0 8,10,12,14,16,18 * * MON-FRI") // Cada 2 horas de 8AM a 6PM, Lunes a Viernes
    @Async
    public void verificacionHorarioLaboral() {
        log.debug("Verificando reservas recurrentes durante horario laboral");
        
        try {
            reservaRecurrenteUseCase.generarReservasPendientes();
            log.debug("Verificación de horario laboral completada");
            
        } catch (Exception e) {
            log.warn("Error durante verificación de horario laboral: {}", e.getMessage());
        }
    }
}
