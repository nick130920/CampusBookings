package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.ScenarioTypePermission;

import java.util.List;
import java.util.Optional;

public interface ScenarioTypePermissionRepositoryPort {
    ScenarioTypePermission save(ScenarioTypePermission permission);

    List<ScenarioTypePermission> findByUsuarioEmail(String email);

    boolean existsByUsuarioEmailAndTipoNombreAndAction(String email, String tipoNombre, String action);
}


