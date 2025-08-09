package edu.usco.campusbookings.application.port.input;

import edu.usco.campusbookings.application.dto.response.ScenarioTypePermissionResponse;

import java.util.List;

public interface ScenarioTypePermissionUseCase {
    ScenarioTypePermissionResponse assignPermissionToUser(String userEmail, String tipoNombre, String action);
    void revokePermissionFromUser(String userEmail, String tipoNombre, String action);
    List<ScenarioTypePermissionResponse> getPermissionsForUser(String userEmail);
}


