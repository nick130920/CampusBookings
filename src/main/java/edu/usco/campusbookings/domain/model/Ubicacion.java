package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity that represents a physical location where scenarios are located.
 * Contains information about different locations available for booking scenarios.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ubicaciones")
@EntityListeners(AuditingEntityListener.class)
public class Ubicacion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String direccion;
    
    private String ciudad;
    
    private String pais;
    
    @Builder.Default
    @Column(name = "es_activo")
    private boolean activo = true;
}
