package edu.usco.campusbookings.infrastructure.adapter.input.controller.admin;

import edu.usco.campusbookings.application.port.input.ScenarioTypePermissionUseCase;
import edu.usco.campusbookings.domain.model.ScenarioTypePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/scenario-type-permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ScenarioTypePermissionAdminController {

    private final ScenarioTypePermissionUseCase useCase;

    @PostMapping("/assign")
    public ResponseEntity<ScenarioTypePermission> assign(
            @RequestParam String userEmail,
            @RequestParam String tipoNombre,
            @RequestParam String action) {
        return ResponseEntity.ok(useCase.assignPermissionToUser(userEmail, tipoNombre, action));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<ScenarioTypePermission>> list(@PathVariable("email") String email) {
        return ResponseEntity.ok(useCase.getPermissionsForUser(email));
    }
}


