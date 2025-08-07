package edu.usco.campusbookings.infrastructure.adapter.input.controller.admin;

import edu.usco.campusbookings.application.dto.request.CreatePermissionRequest;
import edu.usco.campusbookings.application.dto.request.UpdatePermissionRequest;
import edu.usco.campusbookings.application.dto.response.PermissionResponse;
import edu.usco.campusbookings.application.port.input.PermissionManagementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Permisos", description = "Endpoints para la gestión administrativa de permisos")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionManagementController {

    private final PermissionManagementUseCase permissionManagementUseCase;

    @Operation(summary = "Crear un nuevo permiso", description = "Crea un nuevo permiso en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Permiso creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un permiso con ese nombre")
    })
    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(
            @Valid @RequestBody CreatePermissionRequest request) {
        log.info("Solicitud para crear permiso: {}", request.getName());
        PermissionResponse response = permissionManagementUseCase.createPermission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un permiso", description = "Actualiza un permiso existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> updatePermission(
            @Parameter(description = "ID del permiso a actualizar") @PathVariable Long permissionId,
            @Valid @RequestBody UpdatePermissionRequest request) {
        log.info("Solicitud para actualizar permiso con ID: {}", permissionId);
        PermissionResponse response = permissionManagementUseCase.updatePermission(permissionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un permiso", description = "Elimina un permiso del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Permiso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "ID del permiso a eliminar") @PathVariable Long permissionId) {
        log.info("Solicitud para eliminar permiso con ID: {}", permissionId);
        permissionManagementUseCase.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener todos los permisos", description = "Retorna todos los permisos del sistema")
    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        log.debug("Solicitud para obtener todos los permisos");
        List<PermissionResponse> permissions = permissionManagementUseCase.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Obtener permiso por ID", description = "Retorna un permiso específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permiso encontrado"),
        @ApiResponse(responseCode = "404", description = "Permiso no encontrado")
    })
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponse> getPermissionById(
            @Parameter(description = "ID del permiso") @PathVariable Long permissionId) {
        log.debug("Solicitud para obtener permiso con ID: {}", permissionId);
        PermissionResponse response = permissionManagementUseCase.getPermissionById(permissionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener permisos por recurso", description = "Retorna permisos filtrados por recurso")
    @GetMapping("/by-resource")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByResource(
            @Parameter(description = "Nombre del recurso") @RequestParam String resource) {
        log.debug("Solicitud para obtener permisos por recurso: {}", resource);
        List<PermissionResponse> permissions = permissionManagementUseCase.getPermissionsByResource(resource);
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Obtener permisos por acción", description = "Retorna permisos filtrados por acción")
    @GetMapping("/by-action")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByAction(
            @Parameter(description = "Nombre de la acción") @RequestParam String action) {
        log.debug("Solicitud para obtener permisos por acción: {}", action);
        List<PermissionResponse> permissions = permissionManagementUseCase.getPermissionsByAction(action);
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Buscar permisos", description = "Busca permisos por nombre o descripción")
    @GetMapping("/search")
    public ResponseEntity<List<PermissionResponse>> searchPermissions(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm) {
        log.debug("Solicitud para buscar permisos con término: {}", searchTerm);
        List<PermissionResponse> permissions = permissionManagementUseCase.searchPermissions(searchTerm);
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Obtener recursos disponibles", description = "Retorna la lista de recursos disponibles en el sistema")
    @GetMapping("/resources")
    public ResponseEntity<List<String>> getAvailableResources() {
        log.debug("Solicitud para obtener recursos disponibles");
        List<String> resources = permissionManagementUseCase.getAvailableResources();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Obtener acciones disponibles", description = "Retorna la lista de acciones disponibles en el sistema")
    @GetMapping("/actions")
    public ResponseEntity<List<String>> getAvailableActions() {
        log.debug("Solicitud para obtener acciones disponibles");
        List<String> actions = permissionManagementUseCase.getAvailableActions();
        return ResponseEntity.ok(actions);
    }
}
