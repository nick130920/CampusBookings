package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.usco.campusbookings.domain.model.Escenario;

public interface SpringDataEscenarioRepository extends JpaRepository<Escenario, Long> {
    // Base repository methods are inherited from JpaRepository
}
