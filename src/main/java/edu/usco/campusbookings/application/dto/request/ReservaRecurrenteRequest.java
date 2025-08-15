package edu.usco.campusbookings.application.dto.request;

import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRecurrenteRequest {

    @NotNull(message = "El ID del escenario es obligatorio")
    private Long escenarioId;

    @NotNull(message = "El patrón de recurrencia es obligatorio")
    private ReservaRecurrente.PatronRecurrencia patron;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser presente o futura")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate fechaFin;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private String observaciones;

    // Para patrón SEMANAL: lista de días de la semana (1=Lunes, 7=Domingo)
    private List<Integer> diasSemana;

    // Para patrón MENSUAL: día específico del mes (1-31)
    @Min(value = 1, message = "El día del mes debe ser entre 1 y 31")
    @Max(value = 31, message = "El día del mes debe ser entre 1 y 31")
    private Integer diaMes;

    // Intervalo de repetición (cada X días/semanas/meses)
    @Min(value = 1, message = "El intervalo de repetición debe ser al menos 1")
    private Integer intervaloRepeticion = 1;

    // Límite máximo de reservas a generar
    @Min(value = 1, message = "El máximo de reservas debe ser al menos 1")
    @Max(value = 365, message = "El máximo de reservas no puede exceder 365")
    private Integer maxReservas = 52; // Por defecto, máximo 52 reservas (1 año semanal)

    // Validaciones personalizadas
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isFechaFinValida() {
        if (fechaInicio == null || fechaFin == null) {
            return true; // Dejar que @NotNull maneje estos casos
        }
        return fechaFin.isAfter(fechaInicio);
    }

    @AssertTrue(message = "La hora de fin debe ser posterior a la hora de inicio")
    public boolean isHoraFinValida() {
        if (horaInicio == null || horaFin == null) {
            return true; // Dejar que @NotNull maneje estos casos
        }
        return horaFin.isAfter(horaInicio);
    }

    @AssertTrue(message = "Para patrón SEMANAL, debe especificar al menos un día de la semana")
    public boolean isDiasSemanaValido() {
        if (patron != ReservaRecurrente.PatronRecurrencia.SEMANAL) {
            return true;
        }
        return diasSemana != null && !diasSemana.isEmpty() && 
               diasSemana.stream().allMatch(dia -> dia >= 1 && dia <= 7);
    }

    @AssertTrue(message = "Para patrón MENSUAL, debe especificar el día del mes")
    public boolean isDiaMesValido() {
        if (patron != ReservaRecurrente.PatronRecurrencia.MENSUAL) {
            return true;
        } 
        return diaMes != null && diaMes >= 1 && diaMes <= 31;
    }
}
