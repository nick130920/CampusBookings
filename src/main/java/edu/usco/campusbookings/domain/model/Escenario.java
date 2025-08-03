package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Escenario extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_escenario_id", nullable = false)
    private TipoEscenario tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id", nullable = false)
    private Ubicacion ubicacion;
    private Integer capacidad;
    private String descripcion;
    private String recursos;
    private String imagenUrl;
    @Builder.Default
    private Boolean disponible = true;

    @OneToMany(mappedBy = "escenario")
    private List<HorarioDisponible> horariosDisponibles;

    @OneToMany(mappedBy = "escenario")
    private List<Reserva> reservas;
}
