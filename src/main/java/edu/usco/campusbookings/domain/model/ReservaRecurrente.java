package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservas_recurrentes")
public class ReservaRecurrente extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El escenario es obligatorio")
    @ManyToOne
    @JoinColumn(name = "escenario_id", nullable = false)
    private Escenario escenario;

    @NotNull(message = "El patrón de recurrencia es obligatorio")
    @Enumerated(EnumType.STRING)
    private PatronRecurrencia patron;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    private String observaciones;

    @Column(name = "dias_semana")
    private String diasSemana; // Para patrones semanales: JSON array de días [1,2,3] (1=Lunes, 7=Domingo)

    @Column(name = "dia_mes")
    private Integer diaMes; // Para patrones mensuales: día específico del mes

    @Column(name = "intervalo_repeticion")
    private Integer intervaloRepeticion; // Cada X días/semanas/meses

    @Column(name = "activa")
    @Builder.Default
    private Boolean activa = true;

    @Column(name = "max_reservas")
    private Integer maxReservas; // Límite máximo de reservas a generar

    @OneToMany(mappedBy = "reservaRecurrente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Reserva> reservasGeneradas;

    public enum PatronRecurrencia {
        DIARIO("Diario"),
        SEMANAL("Semanal"), 
        MENSUAL("Mensual"),
        PERSONALIZADO("Personalizado");

        private final String descripcion;

        PatronRecurrencia(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    /**
     * Calcula la próxima fecha de reserva basada en el patrón de recurrencia
     */
    public LocalDate calcularProximaFecha(LocalDate fechaActual) {
        return switch (this.patron) {
            case DIARIO -> fechaActual.plusDays(this.intervaloRepeticion != null ? this.intervaloRepeticion : 1);
            case SEMANAL -> fechaActual.plusWeeks(this.intervaloRepeticion != null ? this.intervaloRepeticion : 1);
            case MENSUAL -> fechaActual.plusMonths(this.intervaloRepeticion != null ? this.intervaloRepeticion : 1);
            case PERSONALIZADO -> fechaActual.plusDays(1); // Implementación personalizada según diasSemana
        };
    }

    /**
     * Verifica si una fecha específica coincide con el patrón de recurrencia
     */
    public boolean coincideConPatron(LocalDate fecha) {
        if (fecha.isBefore(this.fechaInicio) || fecha.isAfter(this.fechaFin)) {
            return false;
        }

        return switch (this.patron) {
            case DIARIO -> true; // Todos los días dentro del rango
            case SEMANAL -> {
                // Verificar si el día de la semana coincide con diasSemana
                if (this.diasSemana == null || this.diasSemana.isEmpty()) {
                    yield false;
                }
                int diaSemana = fecha.getDayOfWeek().getValue(); // 1=Lunes, 7=Domingo
                yield this.diasSemana.contains(String.valueOf(diaSemana));
            }
            case MENSUAL -> {
                // Verificar si el día del mes coincide
                if (this.diaMes == null) {
                    yield false;
                }
                yield fecha.getDayOfMonth() == this.diaMes;
            }
            case PERSONALIZADO -> {
                // Lógica personalizada según necesidades específicas
                yield true;
            }
        };
    }

    /**
     * Genera el LocalDateTime de inicio para una fecha específica
     */
    public LocalDateTime generarFechaHoraInicio(LocalDate fecha) {
        return LocalDateTime.of(fecha, this.horaInicio);
    }

    /**
     * Genera el LocalDateTime de fin para una fecha específica
     */
    public LocalDateTime generarFechaHoraFin(LocalDate fecha) {
        return LocalDateTime.of(fecha, this.horaFin);
    }
}
