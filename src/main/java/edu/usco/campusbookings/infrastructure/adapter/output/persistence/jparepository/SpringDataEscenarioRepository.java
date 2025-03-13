package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jparepository;

import edu.usco.campusbookings.domain.model.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEscenarioRepository extends JpaRepository<Escenario, Long> {
}
