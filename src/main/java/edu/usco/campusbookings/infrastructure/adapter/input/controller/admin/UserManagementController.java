package edu.usco.campusbookings.infrastructure.adapter.input.controller.admin;

import edu.usco.campusbookings.application.dto.request.UpdateUsuarioRolRequest;
import edu.usco.campusbookings.application.dto.response.UsuarioDetailResponse;
import edu.usco.campusbookings.application.port.input.UserManagementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Gestión de Usuarios", description = "Endpoints para la gestión administrativa de usuarios y sus roles")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementUseCase userManagementUseCase;

    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene todos los usuarios con información de sus roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioDetailResponse>> getAllUsers() {
        log.info("Solicitud para obtener todos los usuarios");
        List<UsuarioDetailResponse> users = userManagementUseCase.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico con información de su rol")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UsuarioDetailResponse> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        log.info("Solicitud para obtener usuario con ID: {}", userId);
        UsuarioDetailResponse user = userManagementUseCase.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre, apellido o email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/search")
    public ResponseEntity<List<UsuarioDetailResponse>> searchUsers(
            @Parameter(description = "Término de búsqueda") @RequestParam String searchTerm) {
        log.info("Solicitud para buscar usuarios con término: {}", searchTerm);
        List<UsuarioDetailResponse> users = userManagementUseCase.searchUsers(searchTerm);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Cambiar rol de usuario", description = "Cambia el rol asignado a un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rol cambiado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @PutMapping("/{userId}/role")
    public ResponseEntity<UsuarioDetailResponse> updateUserRole(
            @Parameter(description = "ID del usuario") @PathVariable Long userId,
            @Valid @RequestBody UpdateUsuarioRolRequest request) {
        log.info("Solicitud para cambiar rol del usuario {} al rol {}", userId, request.getRolId());
        UsuarioDetailResponse updatedUser = userManagementUseCase.updateUserRole(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Obtener usuarios por rol", description = "Obtiene todos los usuarios que tienen un rol específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping("/by-role/{roleId}")
    public ResponseEntity<List<UsuarioDetailResponse>> getUsersByRole(
            @Parameter(description = "ID del rol") @PathVariable Long roleId) {
        log.info("Solicitud para obtener usuarios con rol ID: {}", roleId);
        List<UsuarioDetailResponse> users = userManagementUseCase.getUsersByRole(roleId);
        return ResponseEntity.ok(users);
    }
}
