package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para mostrar un resumen de las reservas que se generarán antes de confirmar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRecurrenteResumeResponse {

    private ReservaRecurrente.PatronRecurrencia patron;
    private String descripcionPatron;
    private Integer totalReservasAGenerar;
    private List<FechaReservaResumen> fechasReservas;
    private List<String> conflictos; // Fechas que tienen conflictos
    private List<String> advertencias; // Advertencias o notas importantes

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FechaReservaResumen {
        private LocalDate fecha;
        private String diaSemana;
        private Boolean tieneConflicto;
        private String detalleConflicto;
    }
}
