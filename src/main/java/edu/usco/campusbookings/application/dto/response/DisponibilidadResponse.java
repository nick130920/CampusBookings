package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para verificación de disponibilidad.
 * Proporciona información detallada sobre la disponibilidad de un escenario,
 * similar a como Cal.com muestra slots disponibles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadResponse {
    
    /**
     * Indica si el escenario está disponible para el rango solicitado
     */
    private boolean disponible;
    
    /**
     * ID del escenario consultado
     */
    private Long escenarioId;
    
    /**
     * Nombre del escenario consultado
     */
    private String escenarioNombre;
    
    /**
     * Fecha y hora de inicio solicitada
     */
    private LocalDateTime fechaInicio;
    
    /**
     * Fecha y hora de fin solicitada
     */
    private LocalDateTime fechaFin;
    
    /**
     * Mensaje explicativo sobre la disponibilidad
     */
    private String mensaje;
    
    /**
     * Lista de conflictos si el escenario no está disponible
     */
    private List<ConflictoReserva> conflictos;
    
    /**
     * Sugerencias de horarios alternativos disponibles
     */
    private List<SugerenciaHorario> alternativas;
    
    /**
     * Información sobre un conflicto de reserva
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConflictoReserva {
        private Long reservaId;
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String usuarioNombre;
        private String estado;
    }
    
    /**
     * Sugerencia de horario alternativo
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SugerenciaHorario {
        private LocalDateTime fechaInicio;
        private LocalDateTime fechaFin;
        private String descripcion;
    }
}