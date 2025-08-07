package edu.usco.campusbookings.infrastructure.adapter.input.controller.admin;

import edu.usco.campusbookings.application.dto.request.CreateRolRequest;
import edu.usco.campusbookings.application.dto.request.UpdateRolRequest;
import edu.usco.campusbookings.application.dto.response.RolDetailResponse;
import edu.usco.campusbookings.application.dto.response.RolResponse;
import edu.usco.campusbookings.application.port.input.RoleManagementUseCase;
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
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Roles", description = "Endpoints para la gestión administrativa de roles y permisos")
@PreAuthorize("hasRole('ADMIN')")
public class RoleManagementController {

    private final RoleManagementUseCase roleManagementUseCase;

    @Operation(summary = "Crear un nuevo rol", description = "Crea un nuevo rol con los permisos especificados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rol creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un rol con ese nombre")
    })
    @PostMapping
    public ResponseEntity<RolDetailResponse> createRole(
            @Valid @RequestBody CreateRolRequest request) {
        log.info("Solicitud para crear rol: {}", request.getNombre());
        RolDetailResponse response = roleManagementUseCase.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un rol", description = "Actualiza un rol existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{roleId}")
    public ResponseEntity<RolDetailResponse> updateRole(
            @Parameter(description = "ID del rol a actualizar") @PathVariable Long roleId,
            @Valid @RequestBody UpdateRolRequest request) {
        log.info("Solicitud para actualizar rol con ID: {}", roleId);
        RolDetailResponse response = roleManagementUseCase.updateRole(roleId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar un rol", description = "Elimina un rol del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rol eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
        @ApiResponse(responseCode = "409", description = "No se puede eliminar el rol porque tiene usuarios asignados")
    })
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID del rol a eliminar") @PathVariable Long roleId) {
        log.info("Solicitud para eliminar rol con ID: {}", roleId);
        roleManagementUseCase.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener todos los roles", description = "Retorna todos los roles del sistema")
    @GetMapping
    public ResponseEntity<List<RolResponse>> getAllRoles() {
        log.debug("Solicitud para obtener todos los roles");
        List<RolResponse> roles = roleManagementUseCase.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtener roles activos", description = "Retorna solo los roles activos")
    @GetMapping("/active")
    public ResponseEntity<List<RolResponse>> getActiveRoles() {
        log.debug("Solicitud para obtener roles activos");
        List<RolResponse> roles = roleManagementUseCase.getActiveRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtener rol por ID", description = "Retorna un rol específico con sus permisos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol encontrado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{roleId}")
    public ResponseEntity<RolDetailResponse> getRoleById(
            @Parameter(description = "ID del rol") @PathVariable Long roleId) {
        log.debug("Solicitud para obtener rol con ID: {}", roleId);
        RolDetailResponse response = roleManagementUseCase.getRoleById(roleId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar roles", description = "Busca roles por nombre o descripción")
    @GetMapping("/search")
    public ResponseEntity<List<RolResponse>> searchRoles(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm) {
        log.debug("Solicitud para buscar roles con término: {}", searchTerm);
        List<RolResponse> roles = roleManagementUseCase.searchRoles(searchTerm);
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Asignar permisos a rol", description = "Asigna nuevos permisos a un rol existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permisos asignados exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol o permisos no encontrados")
    })
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<RolDetailResponse> assignPermissionsToRole(
            @Parameter(description = "ID del rol") @PathVariable Long roleId,
            @Parameter(description = "IDs de los permisos a asignar") @RequestBody List<Long> permissionIds) {
        log.info("Solicitud para asignar permisos al rol con ID: {}", roleId);
        RolDetailResponse response = roleManagementUseCase.assignPermissionsToRole(roleId, permissionIds);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remover permisos de rol", description = "Remueve permisos de un rol existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permisos removidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<RolDetailResponse> removePermissionsFromRole(
            @Parameter(description = "ID del rol") @PathVariable Long roleId,
            @Parameter(description = "IDs de los permisos a remover") @RequestBody List<Long> permissionIds) {
        log.info("Solicitud para remover permisos del rol con ID: {}", roleId);
        RolDetailResponse response = roleManagementUseCase.removePermissionsFromRole(roleId, permissionIds);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado de rol", description = "Activa o desactiva un rol")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del rol cambiado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
        @ApiResponse(responseCode = "409", description = "No se puede desactivar el rol porque tiene usuarios asignados")
    })
    @PatchMapping("/{roleId}/toggle-status")
    public ResponseEntity<RolResponse> toggleRoleStatus(
            @Parameter(description = "ID del rol") @PathVariable Long roleId) {
        log.info("Solicitud para cambiar estado del rol con ID: {}", roleId);
        RolResponse response = roleManagementUseCase.toggleRoleStatus(roleId);
        return ResponseEntity.ok(response);
    }
}
