package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta para el calendario de disponibilidad.
 * Contiene información detallada de la disponibilidad por día.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarioDisponibilidadResponse {
    
    /**
     * ID del escenario
     */
    private Long escenarioId;
    
    /**
     * Nombre del escenario
     */
    private String escenarioNombre;
    
    /**
     * Tipo de escenario
     */
    private String tipo;
    
    /**
     * Ubicación del escenario
     */
    private String ubicacion;
    
    /**
     * Capacidad del escenario
     */
    private Integer capacidad;
    
    /**
     * Descripción del escenario
     */
    private String descripcion;
    
    /**
     * URL de la imagen del escenario
     */
    private String imagenUrl;
    
    /**
     * Lista de días con su disponibilidad
     */
    private List<DiaDisponibilidad> diasDisponibilidad;
    
    /**
     * Información de un día específico
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaDisponibilidad {
        
        /**
         * Fecha del día
         */
        private LocalDate fecha;
        
        /**
         * Indica si el día está disponible
         */
        private boolean disponible;
        
        /**
         * Estado de disponibilidad (DISPONIBLE, RESERVADO, NO_DISPONIBLE)
         */
        private String estado;
        
        /**
         * Lista de reservas existentes para ese día
         */
        private List<ReservaInfo> reservas;
        
        /**
         * Horarios disponibles para ese día
         */
        private List<String> horariosDisponibles;
    }
    
    /**
     * Información básica de una reserva
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservaInfo {
        
        /**
         * ID de la reserva
         */
        private Long reservaId;
        
        /**
         * Fecha y hora de inicio
         */
        private String fechaInicio;
        
        /**
         * Fecha y hora de fin
         */
        private String fechaFin;
        
        /**
         * Estado de la reserva
         */
        private String estado;
        
        /**
         * Nombre del usuario que hizo la reserva
         */
        private String usuarioNombre;
    }
} 