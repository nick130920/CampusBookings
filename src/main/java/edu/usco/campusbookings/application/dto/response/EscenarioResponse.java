package edu.usco.campusbookings.application.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para respuestas de escenarios.
 * Incluye todos los campos necesarios para el frontend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscenarioResponse {
    
    private Long id;
    
    private String nombre;
    
    private String descripcion;
    
    private String tipo;
    
    private String ubicacion;
    
    private Integer capacidad;
    
    private Boolean disponible;
    
    private String recursos;
    
    private String imagenUrl;
    
    private LocalTime horarioApertura;
    
    private LocalTime horarioCierre;
    
    private Double costoPorHora;
    
    private List<String> caracteristicas;
    
    // Campos de auditoría
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaActualizacion;
    
    private String creadoPor;
    
    private String actualizadoPor;
}