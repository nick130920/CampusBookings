package edu.usco.campusbookings.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response que contiene todas las ocupaciones de un escenario en un día específico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcupacionesDiaResponse {
    
    private Long escenarioId;
    private String escenarioNombre;
    private LocalDate fecha;
    private List<BloqueOcupado> bloquesOcupados;
    
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
    }
}