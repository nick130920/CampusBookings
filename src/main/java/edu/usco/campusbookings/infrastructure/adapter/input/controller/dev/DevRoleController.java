package edu.usco.campusbookings.infrastructure.adapter.input.controller.dev;

import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.dto.response.PermissionResponse;
import edu.usco.campusbookings.application.port.input.RoleManagementUseCase;
import edu.usco.campusbookings.application.port.input.PermissionManagementUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador temporal para desarrollo - No usar en producción
 */
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Slf4j
@Profile("!prod")  // No se carga en producción
public class DevRoleController {

    private final RoleManagementUseCase roleManagementUseCase;
    private final PermissionManagementUseCase permissionManagementUseCase;

    @GetMapping("/roles")
    public ResponseEntity<List<RolResponse>> getAllRoles() {
        log.warn("DESARROLLO: Acceso a roles sin autenticación");
        List<RolResponse> roles = roleManagementUseCase.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        log.warn("DESARROLLO: Acceso a permisos sin autenticación");
        List<PermissionResponse> permissions = permissionManagementUseCase.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/permissions/resources")
    public ResponseEntity<List<String>> getAvailableResources() {
        log.warn("DESARROLLO: Acceso a recursos sin autenticación");
        List<String> resources = permissionManagementUseCase.getAvailableResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/permissions/actions")
    public ResponseEntity<List<String>> getAvailableActions() {
        log.warn("DESARROLLO: Acceso a acciones sin autenticación");
        List<String> actions = permissionManagementUseCase.getAvailableActions();
        return ResponseEntity.ok(actions);
    }
}
