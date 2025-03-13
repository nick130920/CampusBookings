package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository;

import edu.usco.campusbookings.domain.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRolRepository extends JpaRepository<Rol, Long> {
}
