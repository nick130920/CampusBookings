package edu.usco.campusbookings.infrastructure.adapter.input.controller.dev;

import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataPermissionRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataUsuarioRepository;
import edu.usco.campusbookings.infrastructure.config.DataInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dev/database")
@RequiredArgsConstructor
@Slf4j
public class DatabaseResetController {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataRolRepository rolRepository;
    private final SpringDataPermissionRepository permissionRepository;
    private final DataInitializer dataInitializer;

    @PostMapping("/reset-users-roles")
    @Transactional
    public ResponseEntity<Map<String, Object>> resetUsersAndRoles() {
        log.warn("🔥 INICIANDO RESET DE USUARIOS Y ROLES - OPERACIÓN DESTRUCTIVA");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Contar registros antes del reset
            long usersBeforeCount = usuarioRepository.count();
            long rolesBeforeCount = rolRepository.count();
            long permissionsBeforeCount = permissionRepository.count();
            
            log.info("Estado antes del reset - Usuarios: {}, Roles: {}, Permisos: {}", 
                    usersBeforeCount, rolesBeforeCount, permissionsBeforeCount);
            
            // 1. Eliminar todos los usuarios (esto limpiará las FK a roles)
            usuarioRepository.deleteAll();
            usuarioRepository.flush();
            log.info("✅ Eliminados todos los usuarios");
            
            // 2. Eliminar todas las asociaciones rol-permisos
            rolRepository.findAll().forEach(rol -> {
                if (rol.getPermissions() != null) {
                    rol.getPermissions().clear();
                }
            });
            rolRepository.flush();
            
            // 3. Eliminar todos los roles
            rolRepository.deleteAll();
            rolRepository.flush();
            log.info("✅ Eliminados todos los roles");
            
            // 4. Eliminar todos los permisos
            permissionRepository.deleteAll();
            permissionRepository.flush();
            log.info("✅ Eliminados todos los permisos");
            
            // Verificar que todo se eliminó
            long usersAfterCount = usuarioRepository.count();
            long rolesAfterCount = rolRepository.count();
            long permissionsAfterCount = permissionRepository.count();
            
            response.put("success", true);
            response.put("message", "Reset de base de datos completado exitosamente");
            response.put("deletedUsers", usersBeforeCount);
            response.put("deletedRoles", rolesBeforeCount);
            response.put("deletedPermissions", permissionsBeforeCount);
            response.put("currentUsers", usersAfterCount);
            response.put("currentRoles", rolesAfterCount);
            response.put("currentPermissions", permissionsAfterCount);
            
            log.warn("🔥 RESET COMPLETADO - La aplicación se reinicializará con datos por defecto en el próximo arranque");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error durante el reset de base de datos: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @PostMapping("/reset-and-reinitialize")
    @Transactional
    public ResponseEntity<Map<String, Object>> resetAndReinitialize() {
        log.warn("🔥 INICIANDO RESET COMPLETO Y REINICIALIZACIÓN - OPERACIÓN DESTRUCTIVA");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. Hacer reset completo
            ResponseEntity<Map<String, Object>> resetResponse = resetUsersAndRoles();
            if (!resetResponse.getStatusCode().is2xxSuccessful()) {
                return resetResponse;
            }
            
            // 2. Forzar reinicialización de datos
            log.info("🔄 Iniciando reinicialización de datos por defecto...");
            // Llamar directamente a los métodos de inicialización
            try {
                log.info("Ejecutando createDefaultPermissions...");
                java.lang.reflect.Method createPermissions = DataInitializer.class.getDeclaredMethod("createDefaultPermissions");
                createPermissions.setAccessible(true);
                createPermissions.invoke(dataInitializer);
                
                log.info("Ejecutando createDefaultRoles...");
                java.lang.reflect.Method createRoles = DataInitializer.class.getDeclaredMethod("createDefaultRoles");
                createRoles.setAccessible(true);
                createRoles.invoke(dataInitializer);
                
                log.info("Ejecutando createDefaultAdminUser...");
                java.lang.reflect.Method createAdmin = DataInitializer.class.getDeclaredMethod("createDefaultAdminUser");
                createAdmin.setAccessible(true);
                createAdmin.invoke(dataInitializer);
                
            } catch (Exception reflectionEx) {
                log.warn("Error usando reflexión, intentando método alternativo: {}", reflectionEx.getMessage());
                // Método alternativo más simple
                dataInitializer.run(new org.springframework.boot.DefaultApplicationArguments());
            }
            
            // 3. Verificar estado después de reinicialización
            long usersAfter = usuarioRepository.count();
            long rolesAfter = rolRepository.count();
            long permissionsAfter = permissionRepository.count();
            
            response.put("success", true);
            response.put("message", "Reset y reinicialización completados exitosamente");
            response.put("finalUsers", usersAfter);
            response.put("finalRoles", rolesAfter);
            response.put("finalPermissions", permissionsAfter);
            
            // Verificar usuario admin
            boolean adminExists = usuarioRepository.findByEmail("admin@usco.edu.co").isPresent();
            response.put("adminUserCreated", adminExists);
            
            log.info("✅ RESET Y REINICIALIZACIÓN COMPLETADOS");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error durante reset y reinicialización: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            long userCount = usuarioRepository.count();
            long roleCount = rolRepository.count();
            long permissionCount = permissionRepository.count();
            
            status.put("users", userCount);
            status.put("roles", roleCount);
            status.put("permissions", permissionCount);
            status.put("timestamp", System.currentTimeMillis());
            
            // Verificar si existe usuario admin
            boolean adminExists = usuarioRepository.findByEmail("admin@usco.edu.co").isPresent();
            status.put("adminUserExists", adminExists);
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            log.error("Error obteniendo estado de base de datos: {}", e.getMessage(), e);
            status.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(status);
        }
    }
}
