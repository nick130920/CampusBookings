package edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa;

import edu.usco.campusbookings.domain.model.ColaEspera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataColaEsperaRepository extends JpaRepository<ColaEspera, Long> {
}
