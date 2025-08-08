package edu.usco.campusbookings.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "permissions")
public class Permission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String resource; // ej: "RESERVAS", "USUARIOS", "ESCENARIOS"

    @Column(nullable = false)
    private String action; // ej: "CREATE", "READ", "UPDATE", "DELETE"

    @ManyToMany(mappedBy = "permissions")
    @EqualsAndHashCode.Exclude
    private Set<Rol> roles;

    // Constructor de conveniencia para crear permisos
    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }
}
