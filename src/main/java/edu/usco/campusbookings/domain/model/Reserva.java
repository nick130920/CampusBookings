package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reserva extends Auditable {

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

    @NotNull(message = "El estado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoReserva estado;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    private String observaciones;
    private String motivoRechazo;
}
