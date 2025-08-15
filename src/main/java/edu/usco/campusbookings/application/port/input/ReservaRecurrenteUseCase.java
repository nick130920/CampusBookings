package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.request.ReservaRecurrenteRequest;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResponse;
import edu.usco.campusbookings.application.dto.response.ReservaRecurrenteResumeResponse;

import java.util.List;

public interface ReservaRecurrenteUseCase {
    
    /**
     * Previsualiza las reservas que se generarán con el patrón especificado
     */
    ReservaRecurrenteResumeResponse previsualizarReservasRecurrentes(ReservaRecurrenteRequest request);
    
    /**
     * Crea una nueva configuración de reserva recurrente y genera las reservas iniciales
     */
    ReservaRecurrenteResponse crearReservaRecurrente(ReservaRecurrenteRequest request);
    
    /**
     * Obtiene todas las reservas recurrentes de un usuario
     */
    List<ReservaRecurrenteResponse> obtenerReservasRecurrentesPorUsuario(Long usuarioId);
    
    /**
     * Obtiene todas las reservas recurrentes activas del sistema (admin)
     */
    List<ReservaRecurrenteResponse> obtenerTodasLasReservasRecurrentes();
    
    /**
     * Obtiene una reserva recurrente específica por ID
     */
    ReservaRecurrenteResponse obtenerReservaRecurrentePorId(Long id);
    
    /**
     * Actualiza una reserva recurrente existente
     */
    ReservaRecurrenteResponse actualizarReservaRecurrente(Long id, ReservaRecurrenteRequest request);
    
    /**
     * Desactiva una reserva recurrente (no genera más reservas)
     */
    ReservaRecurrenteResponse desactivarReservaRecurrente(Long id);
    
    /**
     * Activa una reserva recurrente previamente desactivada
     */
    ReservaRecurrenteResponse activarReservaRecurrente(Long id);
    
    /**
     * Elimina completamente una reserva recurrente y opcionalmente sus reservas futuras
     */
    void eliminarReservaRecurrente(Long id, boolean eliminarReservasFuturas);
    
    /**
     * Genera manualmente las próximas reservas pendientes para todas las configuraciones activas
     * (Usado por scheduler automático)
     */
    void generarReservasPendientes();
    
    /**
     * Genera reservas para una configuración específica hasta una fecha determinada
     */
    List<Long> generarReservasHastaFecha(Long reservaRecurrenteId, java.time.LocalDate fechaLimite);
}
