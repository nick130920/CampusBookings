package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository;

import edu.usco.campusbookings.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataUsuarioRepository extends JpaRepository<Usuario, Long> {
}
