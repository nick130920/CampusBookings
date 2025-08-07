package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "feedback", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "escenario_id"})
})
public class Feedback extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "El escenario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escenario_id", nullable = false)
    private Escenario escenario;

    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String comentario;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
