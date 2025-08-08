package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataPermissionRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final SpringDataUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
                // En producción, evitar actualización automática de roles existentes para prevenir errores
                long rolesCount = rolRepository.count();
                log.info("Encontrados {} roles existentes. Saltando actualización automática para evitar errores de concurrencia.", rolesCount);
                
                // Solo actualizar si está específicamente habilitado via variable de entorno
                String forceUpdate = System.getenv("FORCE_ROLE_PERMISSION_UPDATE");
                if ("true".equals(forceUpdate)) {
                    log.warn("FORCE_ROLE_PERMISSION_UPDATE=true detectado. Intentando actualización de roles existentes...");
                    updateExistingRolesWithPermissions();
                } else {
                    log.info("Para forzar actualización de permisos en roles existentes, establecer variable de entorno FORCE_ROLE_PERMISSION_UPDATE=true");
                }
            }
            
            // Crear usuario administrador por defecto si no existe
            if (usuarioRepository.count() == 0) {
                createDefaultAdminUser();
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
        
        try {
            // Obtener datos sin usar copias primero para ver si ese es el problema
            List<Rol> originalRoles = rolRepository.findAll();
            List<Permission> originalPermissions = permissionRepository.findAll();
            
            log.info("Encontrados {} roles y {} permisos en base de datos", 
                    originalRoles.size(), originalPermissions.size());
            
            // Crear listas completamente nuevas con datos copiados
            List<Rol> rolesToProcess = new ArrayList<>();
            List<Permission> permissionsToUse = new ArrayList<>();
            
            // Copiar manualmente los datos para evitar referencias a entidades managed
            for (Permission p : originalPermissions) {
                permissionsToUse.add(p);
            }
            
            for (Rol r : originalRoles) {
                rolesToProcess.add(r);
            }
            
            log.info("Creadas copias de trabajo: {} roles, {} permisos", 
                    rolesToProcess.size(), permissionsToUse.size());
            
            // Lista para acumular roles que necesitan ser actualizados
            List<Rol> rolesToUpdate = new ArrayList<>();
            
            // Procesar cada rol de forma individual y segura
            for (int i = 0; i < rolesToProcess.size(); i++) {
                try {
                    Rol role = rolesToProcess.get(i);
                    log.debug("Procesando rol {} de {}: {}", i + 1, rolesToProcess.size(), role.getNombre());
                    
                    if (role.getPermissions() == null || role.getPermissions().isEmpty()) {
                        log.info("Preparando asignación de permisos al rol existente: {}", role.getNombre());
                        
                        // Crear un nuevo HashSet completamente limpio
                        Set<Permission> permissions = new HashSet<>();
                        
                        switch (role.getNombre()) {
                            case "ADMIN":
                                permissions.addAll(permissionsToUse);
                                log.debug("Asignando {} permisos a ADMIN", permissions.size());
                                break;
                            case "COORDINATOR":
                                Set<Permission> readPerms = getPermissionsByActionsSafe(permissionsToUse, "READ");
                                Set<Permission> scenarioPerms = getPermissionsByResourcesSafe(permissionsToUse, "SCENARIOS");
                                Set<Permission> reservationPerms = getPermissionsByResourcesSafe(permissionsToUse, "RESERVATIONS");
                                Set<Permission> viewPerms = getPermissionsByActionsSafe(permissionsToUse, "VIEW");
                                
                                permissions.addAll(readPerms);
                                permissions.addAll(scenarioPerms);
                                permissions.addAll(reservationPerms);
                                permissions.addAll(viewPerms);
                                log.debug("Asignando {} permisos a COORDINATOR", permissions.size());
                                break;
                            case "USER":
                                Set<Permission> userPerms = getPermissionsByNamesSafe(permissionsToUse, 
                                    "READ_SCENARIOS", "CREATE_RESERVATIONS", "READ_RESERVATIONS", "CANCEL_RESERVATIONS");
                                permissions.addAll(userPerms);
                                log.debug("Asignando {} permisos a USER", permissions.size());
                                break;
                            default:
                                log.warn("Rol desconocido: {}, saltando...", role.getNombre());
                                continue;
                        }
                        
                        // Solo asignar si se encontraron permisos
                        if (!permissions.isEmpty()) {
                            role.setPermissions(permissions);
                            rolesToUpdate.add(role);
                            log.debug("Rol {} agregado para actualización con {} permisos", 
                                    role.getNombre(), permissions.size());
                        } else {
                            log.warn("No se encontraron permisos para el rol: {}", role.getNombre());
                        }
                    } else {
                        log.debug("Rol {} ya tiene {} permisos asignados", 
                                role.getNombre(), role.getPermissions().size());
                    }
                    
                } catch (Exception e) {
                    log.error("Error procesando rol individual: {}", e.getMessage(), e);
                    // Continuar con el siguiente rol
                }
            }
            
            // Guardar todos los roles actualizados en una sola operación
            if (!rolesToUpdate.isEmpty()) {
                log.info("Guardando {} roles actualizados...", rolesToUpdate.size());
                rolRepository.saveAll(rolesToUpdate);
                log.info("Actualizados {} roles con permisos exitosamente", rolesToUpdate.size());
            } else {
                log.info("No hay roles que necesiten actualización de permisos");
            }
            
        } catch (Exception e) {
            log.error("Error en updateExistingRolesWithPermissions: {}", e.getMessage(), e);
            throw e;
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

    // Métodos seguros que usan iteración manual para evitar ConcurrentModificationException
    private Set<Permission> getPermissionsByActionsSafe(List<Permission> permissions, String action) {
        Set<Permission> result = new HashSet<>();
        try {
            for (int i = 0; i < permissions.size(); i++) {
                Permission p = permissions.get(i);
                if (p != null && action.equals(p.getAction())) {
                    result.add(p);
                }
            }
        } catch (Exception e) {
            log.error("Error en getPermissionsByActionsSafe para action '{}': {}", action, e.getMessage());
        }
        return result;
    }

    private Set<Permission> getPermissionsByResourcesSafe(List<Permission> permissions, String resource) {
        Set<Permission> result = new HashSet<>();
        try {
            for (int i = 0; i < permissions.size(); i++) {
                Permission p = permissions.get(i);
                if (p != null && resource.equals(p.getResource())) {
                    result.add(p);
                }
            }
        } catch (Exception e) {
            log.error("Error en getPermissionsByResourcesSafe para resource '{}': {}", resource, e.getMessage());
        }
        return result;
    }

    private Set<Permission> getPermissionsByNamesSafe(List<Permission> permissions, String... names) {
        Set<Permission> result = new HashSet<>();
        Set<String> nameSet = new HashSet<>(Arrays.asList(names));
        try {
            for (int i = 0; i < permissions.size(); i++) {
                Permission p = permissions.get(i);
                if (p != null && nameSet.contains(p.getName())) {
                    result.add(p);
                }
            }
        } catch (Exception e) {
            log.error("Error en getPermissionsByNamesSafe: {}", e.getMessage());
        }
        return result;
    }
    
    @Transactional
    private void createDefaultAdminUser() {
        log.info("Creando usuario administrador por defecto...");
        
        try {
            // Buscar rol ADMIN
            Rol adminRole = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado. Debe crearse primero."));
            
            // Crear usuario administrador por defecto
            Usuario adminUser = Usuario.builder()
                .nombre("Administrador")
                .apellido("Sistema")
                .email("admin@usco.edu.co")
                .password(passwordEncoder.encode("AdminUSCO2024!"))
                .rol(adminRole)
                .build();
            
            usuarioRepository.save(adminUser);
            log.info("Usuario administrador creado exitosamente - Email: admin@usco.edu.co");
            log.warn("⚠️  IMPORTANTE: Cambiar la contraseña por defecto del administrador después del primer login");
            
        } catch (Exception e) {
            log.error("Error creando usuario administrador por defecto: {}", e.getMessage(), e);
            throw e;
        }
    }
}
