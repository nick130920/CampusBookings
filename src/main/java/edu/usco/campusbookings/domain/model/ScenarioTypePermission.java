package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "scenario_type_permissions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "tipo_escenario_id", "action"})
)
@Getter
@Setter
@NoArgsConstructor
public class ScenarioTypePermission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_escenario_id")
    private TipoEscenario tipoEscenario;

    // Acciones: READ, CREATE, UPDATE, DELETE, MANAGE
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}


