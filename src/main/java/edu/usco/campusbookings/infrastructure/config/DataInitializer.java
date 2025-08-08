package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataPermissionRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Inicializador de datos por defecto para roles y permisos
 * Se ejecuta al iniciar la aplicación
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final SpringDataRolRepository rolRepository;
    private final SpringDataPermissionRepository permissionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Iniciando carga de datos por defecto...");
        
        try {
            // Solo cargar datos si no existen
            if (permissionRepository.count() == 0) {
                createDefaultPermissions();
            }
            
            if (rolRepository.count() == 0) {
                createDefaultRoles();
            } else {
                // Si los roles ya existen, verificar que tengan permisos asignados
                updateExistingRolesWithPermissions();
            }
            
            log.info("Carga de datos por defecto completada");
        } catch (Exception e) {
            log.error("Error durante la inicialización de datos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    private void createDefaultPermissions() {
        log.info("Creando permisos por defecto...");
        
        List<Permission> permissions = Arrays.asList(
            // Permisos de Escenarios
            Permission.builder()
                .name("READ_SCENARIOS")
                .description("Permite leer información de escenarios")
                .resource("SCENARIOS")
                .action("READ")
                .build(),
            Permission.builder()
                .name("CREATE_SCENARIOS")
                .description("Permite crear nuevos escenarios")
                .resource("SCENARIOS")
                .action("CREATE")
                .build(),
            Permission.builder()
                .name("UPDATE_SCENARIOS")
                .description("Permite actualizar escenarios existentes")
                .resource("SCENARIOS")
                .action("UPDATE")
                .build(),
            Permission.builder()
                .name("DELETE_SCENARIOS")
                .description("Permite eliminar escenarios")
                .resource("SCENARIOS")
                .action("DELETE")
                .build(),
            
            // Permisos de Reservas
            Permission.builder()
                .name("CREATE_RESERVATIONS")
                .description("Permite crear reservas")
                .resource("RESERVATIONS")
                .action("CREATE")
                .build(),
            Permission.builder()
                .name("MANAGE_RESERVATIONS")
                .description("Permite gestionar todas las reservas")
                .resource("RESERVATIONS")
                .action("MANAGE")
                .build(),
            Permission.builder()
                .name("READ_RESERVATIONS")
                .description("Permite leer reservas")
                .resource("RESERVATIONS")
                .action("READ")
                .build(),
            Permission.builder()
                .name("CANCEL_RESERVATIONS")
                .description("Permite cancelar reservas")
                .resource("RESERVATIONS")
                .action("CANCEL")
                .build(),
            
            // Permisos de Usuarios
            Permission.builder()
                .name("READ_USERS")
                .description("Permite leer información de usuarios")
                .resource("USERS")
                .action("READ")
                .build(),
            Permission.builder()
                .name("MANAGE_USERS")
                .description("Permite gestionar usuarios del sistema")
                .resource("USERS")
                .action("MANAGE")
                .build(),
            
            // Permisos de Roles
            Permission.builder()
                .name("READ_ROLES")
                .description("Permite leer roles del sistema")
                .resource("ROLES")
                .action("READ")
                .build(),
            Permission.builder()
                .name("MANAGE_ROLES")
                .description("Permite gestionar roles y permisos")
                .resource("ROLES")
                .action("MANAGE")
                .build(),
            
            // Permisos de Reportes
            Permission.builder()
                .name("VIEW_REPORTS")
                .description("Permite ver reportes del sistema")
                .resource("REPORTS")
                .action("VIEW")
                .build(),
            Permission.builder()
                .name("EXPORT_REPORTS")
                .description("Permite exportar reportes")
                .resource("REPORTS")
                .action("EXPORT")
                .build(),
            
            // Permisos de Configuración
            Permission.builder()
                .name("VIEW_SYSTEM_CONFIG")
                .description("Permite ver configuración del sistema")
                .resource("SYSTEM_CONFIG")
                .action("VIEW")
                .build(),
            Permission.builder()
                .name("MANAGE_SYSTEM_CONFIG")
                .description("Permite gestionar configuración del sistema")
                .resource("SYSTEM_CONFIG")
                .action("MANAGE")
                .build()
        );
        
        permissionRepository.saveAll(permissions);
        log.info("Creados {} permisos por defecto", permissions.size());
    }

    @Transactional
    private void createDefaultRoles() {
        log.info("Creando roles por defecto...");
        
        // Obtener todos los permisos
        List<Permission> allPermissions = permissionRepository.findAll();
        
        // Crear rol ADMIN con todos los permisos
        Set<Permission> adminPermissions = new HashSet<>(allPermissions);
        Rol adminRole = Rol.builder()
            .nombre("ADMIN")
            .descripcion("Administrador del sistema con acceso completo")
            .activo(true)
            .permissions(adminPermissions)
            .build();
        
        // Crear rol COORDINATOR con permisos limitados
        Set<Permission> coordinatorPermissions = new HashSet<>();
        coordinatorPermissions.addAll(getPermissionsByActions(allPermissions, "READ"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "SCENARIOS"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "RESERVATIONS"));
        coordinatorPermissions.addAll(getPermissionsByActions(allPermissions, "VIEW"));
        
        Rol coordinatorRole = Rol.builder()
            .nombre("COORDINATOR")
            .descripcion("Coordinador con permisos de gestión de escenarios y reservas")
            .activo(true)
            .permissions(coordinatorPermissions)
            .build();
        
        // Crear rol USER con permisos básicos
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.addAll(getPermissionsByNames(allPermissions, 
            "READ_SCENARIOS", "CREATE_RESERVATIONS", "READ_RESERVATIONS", "CANCEL_RESERVATIONS"));
        
        Rol userRole = Rol.builder()
            .nombre("USER")
            .descripcion("Usuario básico con permisos de reserva")
            .activo(true)
            .permissions(userPermissions)
            .build();
        
        rolRepository.saveAll(Arrays.asList(adminRole, coordinatorRole, userRole));
        log.info("Creados 3 roles por defecto: ADMIN, COORDINATOR, USER");
    }

    @Transactional
    private void updateExistingRolesWithPermissions() {
        log.info("Verificando permisos de roles existentes...");
        
        // Crear copias de las listas para evitar ConcurrentModificationException
        List<Rol> roles = List.copyOf(rolRepository.findAll());
        List<Permission> allPermissions = List.copyOf(permissionRepository.findAll());
        
        // Lista para acumular roles que necesitan ser actualizados
        List<Rol> rolesToUpdate = new ArrayList<>();
        
        for (Rol role : roles) {
            if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
                log.info("Preparando asignación de permisos al rol existente: {}", role.getNombre());
                
                // Crear un nuevo HashSet para evitar problemas de concurrencia
                Set<Permission> permissions = new HashSet<>();
                switch (role.getNombre()) {
                    case "ADMIN":
                        permissions = new HashSet<>(allPermissions);
                        break;
                    case "COORDINATOR":
                        permissions.addAll(getPermissionsByActions(allPermissions, "READ"));
                        permissions.addAll(getPermissionsByResources(allPermissions, "SCENARIOS"));
                        permissions.addAll(getPermissionsByResources(allPermissions, "RESERVATIONS"));
                        permissions.addAll(getPermissionsByActions(allPermissions, "VIEW"));
                        break;
                    case "USER":
                        permissions.addAll(getPermissionsByNames(allPermissions, 
                            "READ_SCENARIOS", "CREATE_RESERVATIONS", "READ_RESERVATIONS", "CANCEL_RESERVATIONS"));
                        break;
                }
                
                // Solo asignar si se encontraron permisos
                if (!permissions.isEmpty()) {
                    role.setPermissions(permissions);
                    rolesToUpdate.add(role);
                }
            }
        }
        
        // Guardar todos los roles actualizados en una sola operación
        if (!rolesToUpdate.isEmpty()) {
            rolRepository.saveAll(rolesToUpdate);
            log.info("Actualizados {} roles con permisos", rolesToUpdate.size());
        }
    }

    private Set<Permission> getPermissionsByActions(List<Permission> permissions, String action) {
        return new HashSet<>(permissions.stream()
            .filter(p -> action.equals(p.getAction()))
            .toList());
    }

    private Set<Permission> getPermissionsByResources(List<Permission> permissions, String resource) {
        return new HashSet<>(permissions.stream()
            .filter(p -> resource.equals(p.getResource()))
            .toList());
    }

    private Set<Permission> getPermissionsByNames(List<Permission> permissions, String... names) {
        Set<String> nameSet = new HashSet<>(Arrays.asList(names));
        return new HashSet<>(permissions.stream()
            .filter(p -> nameSet.contains(p.getName()))
            .toList());
    }
}
