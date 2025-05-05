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
    private String tipo;
    private String ubicacion;
    private Integer capacidad;
    private String descripcion;
    private String recursos;
    private String imagenUrl;

    @OneToMany(mappedBy = "escenario")
    private List<HorarioDisponible> horariosDisponibles;

    @OneToMany(mappedBy = "escenario")
    private List<Reserva> reservas;
}
