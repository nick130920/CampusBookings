package edu.usco.campusbookings.domain.models;

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
    private String tipo;
    private String ubicacion;

    @OneToMany(mappedBy = "escenario")
    private List<HorarioDisponible> horariosDisponibles;

    @OneToMany(mappedBy = "escenario")
    private List<Reserva> reservas;
}
