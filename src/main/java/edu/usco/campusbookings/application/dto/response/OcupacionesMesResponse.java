package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response que contiene todas las ocupaciones de un escenario en un mes específico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionesMesResponse {
    
    private Long escenarioId;
    private String escenarioNombre;
    private Integer año;
    private Integer mes;
    
    /**
     * Mapa donde la clave es el día del mes (1-31) y el valor es la lista de ocupaciones de ese día
     */
    private Map<Integer, List<BloqueOcupado>> ocupacionesPorDia;
    
    /**
     * Lista completa de todas las ocupaciones del mes (para búsquedas)
     */
    private List<BloqueOcupado> todasLasOcupaciones;
    
    /**
     * Representa un bloque de tiempo ocupado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloqueOcupado {
        private LocalDateTime horaInicio;
        private LocalDateTime horaFin;
        private String motivo; // "Reserva de Juan Pérez", "Mantenimiento", etc.
        private String estado; // "APROBADA", "PENDIENTE", etc.
        private Long reservaId; // opcional, solo si es una reserva
        private Integer diaDelMes; // día del mes (1-31) para facilitar agrupación
    }
}