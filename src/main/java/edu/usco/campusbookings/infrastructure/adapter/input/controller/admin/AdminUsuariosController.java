package edu.usco.campusbookings.infrastructure.adapter.input.controller.admin;

import edu.usco.campusbookings.application.dto.response.UsuarioDetailResponse;
import edu.usco.campusbookings.application.port.input.UserManagementUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador específico para endpoints requeridos por el frontend
 * Proporciona endpoints simplificados para listados de usuarios
 */
@RestController
@RequestMapping("/api/admin/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios para Frontend", description = "Endpoints simplificados para el frontend")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuariosController {

    private final UserManagementUseCase userManagementUseCase;

    @Operation(summary = "Listar usuarios para dropdown", 
               description = "Obtiene lista simplificada de usuarios para componentes de selección")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuarios obtenidos exitosamente"),
        @ApiResponse(responseCode = "403", description = "Sin permisos de administrador")
    })
    @GetMapping
    public ResponseEntity<List<UserForDropdown>> getAvailableUsers() {
        log.info("Solicitud para obtener usuarios disponibles para dropdown");
        
        List<UsuarioDetailResponse> allUsers = userManagementUseCase.getAllUsers();
        
        // Mapear a formato simplificado para el frontend
        List<UserForDropdown> userDropdown = allUsers.stream()
                .map(user -> new UserForDropdown(
                    user.getId(), 
                    user.getEmail(), 
                    user.getNombre() + " " + user.getApellido()
                ))
                .collect(Collectors.toList());
        
        log.info("Retornando {} usuarios para dropdown", userDropdown.size());
        return ResponseEntity.ok(userDropdown);
    }

    /**
     * DTO simplificado para dropdowns del frontend
     */
    public static class UserForDropdown {
        private Long id;
        private String email;
        private String nombre;

        public UserForDropdown(Long id, String email, String nombre) {
            this.id = id;
            this.email = email;
            this.nombre = nombre;
        }

        // Getters
        public Long getId() { return id; }
        public String getEmail() { return email; }
        public String getNombre() { return nombre; }

        // Setters
        public void setId(Long id) { this.id = id; }
        public void setEmail(String email) { this.email = email; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}
