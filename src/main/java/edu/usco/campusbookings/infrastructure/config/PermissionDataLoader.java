package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.application.port.output.PermissionRepositoryPort;
import edu.usco.campusbookings.application.port.output.RolRepositoryPort;
import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cargador de datos iniciales para permisos y roles.
 * Se ejecuta al iniciar la aplicación para asegurar que existan los permisos básicos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(100) // Ejecutar después de InitialDataLoader
public class PermissionDataLoader implements CommandLineRunner {

    private final PermissionRepositoryPort permissionRepository;
    private final RolRepositoryPort rolRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Iniciando carga de permisos del sistema...");
        
        // Solo cargar si no existen permisos en la BD
        if (permissionRepository.findAll().isEmpty()) {
            createDefaultPermissions();
            assignPermissionsToRoles();
            log.info("Permisos del sistema cargados exitosamente");
        } else {
            log.info("Los permisos del sistema ya existen, omitiendo carga inicial");
        }
    }

    private void createDefaultPermissions() {
        log.info("Creando permisos por defecto...");
        
        List<Permission> defaultPermissions = List.of(
            // Permisos para USUARIOS
            new Permission("USUARIOS_CREATE", "Crear nuevos usuarios en el sistema", "USUARIOS", "CREATE"),
            new Permission("USUARIOS_READ", "Ver información de usuarios", "USUARIOS", "READ"),
            new Permission("USUARIOS_UPDATE", "Actualizar información de usuarios", "USUARIOS", "UPDATE"),
            new Permission("USUARIOS_DELETE", "Eliminar usuarios del sistema", "USUARIOS", "DELETE"),
            new Permission("USUARIOS_MANAGE", "Gestión completa de usuarios", "USUARIOS", "MANAGE"),

            // Permisos para ROLES
            new Permission("ROLES_CREATE", "Crear nuevos roles en el sistema", "ROLES", "CREATE"),
            new Permission("ROLES_READ", "Ver información de roles", "ROLES", "READ"),
            new Permission("ROLES_UPDATE", "Actualizar información de roles", "ROLES", "UPDATE"),
            new Permission("ROLES_DELETE", "Eliminar roles del sistema", "ROLES", "DELETE"),
            new Permission("ROLES_MANAGE", "Gestión completa de roles", "ROLES", "MANAGE"),

            // Permisos para PERMISOS
            new Permission("PERMISOS_CREATE", "Crear nuevos permisos en el sistema", "PERMISOS", "CREATE"),
            new Permission("PERMISOS_READ", "Ver información de permisos", "PERMISOS", "READ"),
            new Permission("PERMISOS_UPDATE", "Actualizar información de permisos", "PERMISOS", "UPDATE"),
            new Permission("PERMISOS_DELETE", "Eliminar permisos del sistema", "PERMISOS", "DELETE"),
            new Permission("PERMISOS_MANAGE", "Gestión completa de permisos", "PERMISOS", "MANAGE"),

            // Permisos para RESERVAS
            new Permission("RESERVAS_CREATE", "Crear nuevas reservas", "RESERVAS", "CREATE"),
            new Permission("RESERVAS_READ", "Ver información de reservas", "RESERVAS", "READ"),
            new Permission("RESERVAS_UPDATE", "Actualizar reservas existentes", "RESERVAS", "UPDATE"),
            new Permission("RESERVAS_DELETE", "Eliminar reservas", "RESERVAS", "DELETE"),
            new Permission("RESERVAS_MANAGE", "Gestión completa de reservas", "RESERVAS", "MANAGE"),

            // Permisos para ESCENARIOS
            new Permission("ESCENARIOS_CREATE", "Crear nuevos escenarios", "ESCENARIOS", "CREATE"),
            new Permission("ESCENARIOS_READ", "Ver información de escenarios", "ESCENARIOS", "READ"),
            new Permission("ESCENARIOS_UPDATE", "Actualizar escenarios existentes", "ESCENARIOS", "UPDATE"),
            new Permission("ESCENARIOS_DELETE", "Eliminar escenarios", "ESCENARIOS", "DELETE"),
            new Permission("ESCENARIOS_MANAGE", "Gestión completa de escenarios", "ESCENARIOS", "MANAGE"),

            // Permisos para CONFIGURACION
            new Permission("CONFIGURACION_READ", "Ver configuración del sistema", "CONFIGURACION", "READ"),
            new Permission("CONFIGURACION_UPDATE", "Actualizar configuración del sistema", "CONFIGURACION", "UPDATE"),
            new Permission("CONFIGURACION_MANAGE", "Gestión completa de configuración", "CONFIGURACION", "MANAGE"),

            // Permisos para REPORTES
            new Permission("REPORTES_READ", "Ver reportes del sistema", "REPORTES", "READ"),
            new Permission("REPORTES_CREATE", "Generar nuevos reportes", "REPORTES", "CREATE"),
            new Permission("REPORTES_MANAGE", "Gestión completa de reportes", "REPORTES", "MANAGE"),

            // Permisos para AUDITORIA
            new Permission("AUDITORIA_READ", "Ver logs de auditoría", "AUDITORIA", "READ"),
            new Permission("AUDITORIA_MANAGE", "Gestión completa de auditoría", "AUDITORIA", "MANAGE")
        );

        defaultPermissions.forEach(permissionRepository::save);
        log.info("Creados {} permisos por defecto", defaultPermissions.size());
    }

    private void assignPermissionsToRoles() {
        log.info("Asignando permisos a roles existentes...");
        
        // Obtener todos los permisos
        List<Permission> allPermissions = permissionRepository.findAll();
        
        // Asignar todos los permisos al rol ADMIN
        rolRepository.findByNombre("ADMIN").ifPresent(adminRole -> {
            if (adminRole.getPermissions() == null || adminRole.getPermissions().isEmpty()) {
                adminRole.setPermissions(new HashSet<>(allPermissions));
                rolRepository.save(adminRole);
                log.info("Asignados {} permisos al rol ADMIN", allPermissions.size());
            }
        });

        // Asignar permisos básicos al rol USER/USUARIO
        Set<String> userPermissions = Set.of(
            "RESERVAS_CREATE", "RESERVAS_READ", "RESERVAS_UPDATE",
            "ESCENARIOS_READ", "CONFIGURACION_READ"
        );
        
        assignPermissionsToRole("USER", userPermissions);
        assignPermissionsToRole("USUARIO", userPermissions);

        // Asignar permisos intermedios al rol COORDINATOR (si existe)
        Set<String> coordinatorPermissions = Set.of(
            "RESERVAS_CREATE", "RESERVAS_READ", "RESERVAS_UPDATE", "RESERVAS_DELETE", "RESERVAS_MANAGE",
            "ESCENARIOS_READ", "ESCENARIOS_UPDATE",
            "USUARIOS_READ", "CONFIGURACION_READ",
            "REPORTES_READ", "REPORTES_CREATE"
        );
        
        assignPermissionsToRole("COORDINATOR", coordinatorPermissions);
    }

    private void assignPermissionsToRole(String roleName, Set<String> permissionNames) {
        rolRepository.findByNombre(roleName).ifPresent(role -> {
            if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
                Set<Permission> permissions = new HashSet<>();
                for (String permissionName : permissionNames) {
                    permissionRepository.findByName(permissionName).ifPresent(permissions::add);
                }
                
                if (!permissions.isEmpty()) {
                    role.setPermissions(permissions);
                    rolRepository.save(role);
                    log.info("Asignados {} permisos al rol {}", permissions.size(), roleName);
                }
            }
        });
    }
}

