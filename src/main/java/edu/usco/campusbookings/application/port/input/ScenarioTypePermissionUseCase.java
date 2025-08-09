package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.domain.model.ScenarioTypePermission;

import java.util.List;

public interface ScenarioTypePermissionUseCase {
    ScenarioTypePermission assignPermissionToUser(String userEmail, String tipoNombre, String action);
    void revokePermissionFromUser(String userEmail, String tipoNombre, String action);
    List<ScenarioTypePermission> getPermissionsForUser(String userEmail);
}


