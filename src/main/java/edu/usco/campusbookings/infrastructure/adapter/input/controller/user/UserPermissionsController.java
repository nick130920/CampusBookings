package edu.usco.campusbookings.infrastructure.adapter.input.controller.user;

import edu.usco.campusbookings.application.dto.response.UserPermissionsResponse;
import edu.usco.campusbookings.application.port.input.UserManagementUseCase;
import edu.usco.campusbookings.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuario", description = "Endpoints para el usuario autenticado")
public class UserPermissionsController {

    private final UserManagementUseCase userManagementUseCase;
    private final JwtService jwtService;

    @Operation(summary = "Obtener mis permisos", description = "Obtiene los permisos del usuario actualmente autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permisos obtenidos exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/my-permissions")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('COORDINATOR')")
    public ResponseEntity<UserPermissionsResponse> getMyPermissions(HttpServletRequest request) {
        try {
            // Extraer token del header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Token de autorización no encontrado o inválido");
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);
            String userEmail = jwtService.extractUsername(token);
            
            if (userEmail == null) {
                log.warn("No se pudo extraer el email del token");
                return ResponseEntity.status(401).build();
            }

            log.info("Solicitud para obtener permisos del usuario: {}", userEmail);
            
            // Obtener el ID del usuario desde el token
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                log.warn("No se pudo extraer el ID del usuario del token");
                return ResponseEntity.status(401).build();
            }

            UserPermissionsResponse permissions = userManagementUseCase.getUserPermissions(userId);
            log.info("Permisos obtenidos exitosamente para usuario: {} con {} permisos", 
                    userEmail, permissions.getPermissions().size());
            
            return ResponseEntity.ok(permissions);
            
        } catch (Exception e) {
            log.error("Error obteniendo permisos del usuario", e);
            return ResponseEntity.status(500).build();
        }
    }
}
