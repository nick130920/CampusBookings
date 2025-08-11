package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.response.ScenarioTypePermissionResponse;
import edu.usco.campusbookings.application.port.input.ScenarioTypePermissionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scenario-type-permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administración de Permisos por Tipo de Escenario", 
     description = "Endpoints para gestionar permisos de usuarios sobre tipos de escenarios específicos")
@PreAuthorize("hasRole('ADMIN')")
public class ScenarioTypePermissionAdminController {

    private final ScenarioTypePermissionUseCase useCase;

    @Operation(summary = "Asignar permiso por tipo", 
               description = "Asigna un permiso específico a un usuario para un tipo de escenario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso asignado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario o tipo de escenario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @PostMapping("/assign")
    public ResponseEntity<ScenarioTypePermissionResponse> assignPermission(
            @Parameter(description = "Email del usuario") @RequestParam String userEmail,
            @Parameter(description = "Nombre del tipo de escenario") @RequestParam String tipoNombre,
            @Parameter(description = "Acción del permiso") @RequestParam String action) {
        log.info("Asignando permiso {} para tipo {} al usuario {}", action, tipoNombre, userEmail);
        ScenarioTypePermissionResponse permission = useCase.assignPermissionToUser(userEmail, tipoNombre, action);
        return ResponseEntity.ok(permission);
    }

    @Operation(summary = "Revocar permiso por tipo", 
               description = "Revoca un permiso específico de un usuario para un tipo de escenario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Permiso revocado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @DeleteMapping("/revoke")
    public ResponseEntity<Void> revokePermission(
            @Parameter(description = "Email del usuario") @RequestParam String userEmail,
            @Parameter(description = "Nombre del tipo de escenario") @RequestParam String tipoNombre,
            @Parameter(description = "Acción del permiso") @RequestParam String action) {
        log.info("Revocando permiso {} para tipo {} del usuario {}", action, tipoNombre, userEmail);
        useCase.revokePermissionFromUser(userEmail, tipoNombre, action);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener permisos de usuario", 
               description = "Obtiene todos los permisos por tipo asignados a un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permisos obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/user/{email}")
    public ResponseEntity<List<ScenarioTypePermissionResponse>> getUserPermissions(
            @Parameter(description = "Email del usuario") @PathVariable String email) {
        log.info("Obteniendo permisos por tipo para usuario: {}", email);
        List<ScenarioTypePermissionResponse> permissions = useCase.getPermissionsForUser(email);
        return ResponseEntity.ok(permissions);
    }
}


