package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.ReservaRecurrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRecurrenteResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioEmail;
    private Long escenarioId;
    private String escenarioNombre;
    private ReservaRecurrente.PatronRecurrencia patron;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String observaciones;
    private List<Integer> diasSemana;
    private Integer diaMes;
    private Integer intervaloRepeticion;
    private Boolean activa;
    private Integer maxReservas;
    private Integer reservasGeneradas; // Cantidad de reservas ya generadas
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    // Información adicional para la UI
    private String patronDescripcion;
    private String descripcionCompleta; // Descripción legible del patrón
    private LocalDate proximaFechaGeneracion; // Próxima fecha donde se generará una reserva
    private List<LocalDate> proximasFechas; // Lista de próximas 5 fechas de generación
    private Boolean puedeGenerarMas; // Si puede generar más reservas

    /**
     * Genera una descripción legible del patrón de recurrencia
     */
    public void generarDescripcionCompleta() {
        StringBuilder descripcion = new StringBuilder();
        
        switch (patron) {
            case DIARIO -> {
                if (intervaloRepeticion == 1) {
                    descripcion.append("Todos los días");
                } else {
                    descripcion.append("Cada ").append(intervaloRepeticion).append(" días");
                }
            }
            case SEMANAL -> {
                if (intervaloRepeticion == 1) {
                    descripcion.append("Cada semana");
                } else {
                    descripcion.append("Cada ").append(intervaloRepeticion).append(" semanas");
                }
                if (diasSemana != null && !diasSemana.isEmpty()) {
                    descripcion.append(" los ");
                    List<String> nombresDias = diasSemana.stream()
                        .map(this::obtenerNombreDia)
                        .toList();
                    descripcion.append(String.join(", ", nombresDias));
                }
            }
            case MENSUAL -> {
                if (intervaloRepeticion == 1) {
                    descripcion.append("Cada mes");
                } else {
                    descripcion.append("Cada ").append(intervaloRepeticion).append(" meses");
                }
                if (diaMes != null) {
                    descripcion.append(" el día ").append(diaMes);
                }
            }
            case PERSONALIZADO -> descripcion.append("Patrón personalizado");
        }
        
        descripcion.append(" de ").append(horaInicio).append(" a ").append(horaFin);
        descripcion.append(" desde ").append(fechaInicio).append(" hasta ").append(fechaFin);
        
        this.descripcionCompleta = descripcion.toString();
    }

    private String obtenerNombreDia(Integer dia) {
        return switch (dia) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6 -> "Sábado";
            case 7 -> "Domingo";
            default -> "Día " + dia;
        };
    }
}
