package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para consulta de disponibilidad de escenarios.
 * Usado para listar escenarios y su estado de disponibilidad general.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscenarioDisponibilidadResponse {
    
    /**
     * ID del escenario
     */
    private Long id;
    
    /**
     * Nombre del escenario
     */
    private String nombre;
    
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
     * Indica si el escenario está disponible para reservas
     */
    private boolean disponible;
    
    /**
     * Descripción del escenario
     */
    private String descripcion;
    
    /**
     * URL de la imagen del escenario
     */
    private String imagenUrl;
}