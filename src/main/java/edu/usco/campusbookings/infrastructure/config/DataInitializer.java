package edu.usco.campusbookings.infrastructure.config;

import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.domain.model.TipoEscenario;
import edu.usco.campusbookings.domain.model.Ubicacion;
import edu.usco.campusbookings.domain.model.EstadoReserva;
import edu.usco.campusbookings.domain.model.Escenario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataPermissionRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataRolRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataUsuarioRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.TipoEscenarioJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.UbicacionJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.EstadoReservaJpaRepository;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataEscenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

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
    
    // Repositorios adicionales para inicialización completa
    private final TipoEscenarioJpaRepository tipoEscenarioRepository;
    private final UbicacionJpaRepository ubicacionRepository;
    private final EstadoReservaJpaRepository estadoReservaRepository;
    private final SpringDataEscenarioRepository escenarioRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Iniciando carga de datos por defecto...");
        
        try {
            // Solo cargar datos si no existen
            if (permissionRepository.count() == 0) {
                createDefaultPermissions();
                log.info("Esperando a que se complete la persistencia de permisos...");
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
            long userCount = usuarioRepository.count();
            log.info("Encontrados {} usuarios en la base de datos", userCount);
            if (userCount == 0) {
                createDefaultAdminUser();
            } else {
                log.info("Ya existen usuarios en la base de datos, saltando creación de admin por defecto");
                // Verificar si existe usuario admin
                try {
                    usuarioRepository.findByEmail("admin@usco.edu.co")
                        .ifPresentOrElse(
                            admin -> log.info("Usuario admin existe: {}", admin.getEmail()),
                            () -> log.warn("⚠️  No se encontró usuario admin por defecto. Considerar crearlo manualmente.")
                        );
                } catch (Exception e) {
                    log.error("Error verificando usuario admin: {}", e.getMessage());
                }
            }
            
            // Inicializar datos maestros adicionales
            initializeMasterData();
            
            log.info("Carga de datos por defecto completada");
        } catch (Exception e) {
            log.error("Error durante la inicialización de datos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
                .build(),
            
            // Permisos de Feedback
            Permission.builder()
                .name("CREATE_FEEDBACK")
                .description("Permite crear feedback de escenarios")
                .resource("FEEDBACK")
                .action("CREATE")
                .build(),
            Permission.builder()
                .name("READ_FEEDBACK")
                .description("Permite leer feedback de escenarios")
                .resource("FEEDBACK")
                .action("READ")
                .build(),
            Permission.builder()
                .name("UPDATE_FEEDBACK")
                .description("Permite actualizar feedback existente")
                .resource("FEEDBACK")
                .action("UPDATE")
                .build(),
            Permission.builder()
                .name("DELETE_FEEDBACK")
                .description("Permite eliminar feedback")
                .resource("FEEDBACK")
                .action("DELETE")
                .build(),
            
            // Permisos de Alertas
            Permission.builder()
                .name("CREATE_ALERTS")
                .description("Permite crear alertas de reservas")
                .resource("ALERTS")
                .action("CREATE")
                .build(),
            Permission.builder()
                .name("READ_ALERTS")
                .description("Permite leer alertas de reservas")
                .resource("ALERTS")
                .action("READ")
                .build(),
            Permission.builder()
                .name("UPDATE_ALERTS")
                .description("Permite actualizar alertas existentes")
                .resource("ALERTS")
                .action("UPDATE")
                .build(),
            Permission.builder()
                .name("DELETE_ALERTS")
                .description("Permite eliminar alertas")
                .resource("ALERTS")
                .action("DELETE")
                .build()
        );
        
        // Guardar permisos uno por uno para evitar problemas de detached entity
        for (Permission permission : permissions) {
            permissionRepository.save(permission);
        }
        log.info("Creados {} permisos por defecto", permissions.size());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createDefaultRoles() {
        log.info("Creando roles por defecto...");
        
        // Obtener todos los permisos recién persistidos
        List<Permission> allPermissions = permissionRepository.findAll();
        log.info("Obtenidos {} permisos de la base de datos para asignar a roles", allPermissions.size());
        
        // Crear roles sin permisos primero
        Rol adminRole = Rol.builder()
            .nombre("ADMIN")
            .descripcion("Administrador del sistema con acceso completo")
            .activo(true)
            .build();
        
        Rol coordinatorRole = Rol.builder()
            .nombre("COORDINATOR")
            .descripcion("Coordinador con permisos de gestión de escenarios y reservas")
            .activo(true)
            .build();
        
        Rol userRole = Rol.builder()
            .nombre("USER")
            .descripcion("Usuario básico con permisos de reserva")
            .activo(true)
            .build();
        
        // Guardar roles primero
        adminRole = rolRepository.save(adminRole);
        coordinatorRole = rolRepository.save(coordinatorRole);
        userRole = rolRepository.save(userRole);
        
        // Ahora asignar permisos a los roles ya persistidos
        Set<Permission> adminPermissions = new HashSet<>(allPermissions);
        adminRole.setPermissions(adminPermissions);
        
        Set<Permission> coordinatorPermissions = new HashSet<>();
        coordinatorPermissions.addAll(getPermissionsByActions(allPermissions, "READ"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "SCENARIOS"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "RESERVATIONS"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "FEEDBACK"));
        coordinatorPermissions.addAll(getPermissionsByResources(allPermissions, "ALERTS"));
        coordinatorPermissions.addAll(getPermissionsByActions(allPermissions, "VIEW"));
        coordinatorRole.setPermissions(coordinatorPermissions);
        
        Set<Permission> userPermissions = new HashSet<>();
        userPermissions.addAll(getPermissionsByNames(allPermissions, 
            "READ_SCENARIOS", "CREATE_RESERVATIONS", "READ_RESERVATIONS", "CANCEL_RESERVATIONS",
            "CREATE_FEEDBACK", "READ_FEEDBACK", "UPDATE_FEEDBACK", "DELETE_FEEDBACK"));
        userRole.setPermissions(userPermissions);
        
        // Guardar roles con permisos
        rolRepository.save(adminRole);
        rolRepository.save(coordinatorRole);
        rolRepository.save(userRole);
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
                                Set<Permission> feedbackPerms = getPermissionsByResourcesSafe(permissionsToUse, "FEEDBACK");
                                Set<Permission> alertsPerms = getPermissionsByResourcesSafe(permissionsToUse, "ALERTS");
                                Set<Permission> viewPerms = getPermissionsByActionsSafe(permissionsToUse, "VIEW");
                                
                                permissions.addAll(readPerms);
                                permissions.addAll(scenarioPerms);
                                permissions.addAll(reservationPerms);
                                permissions.addAll(feedbackPerms);
                                permissions.addAll(alertsPerms);
                                permissions.addAll(viewPerms);
                                log.debug("Asignando {} permisos a COORDINATOR", permissions.size());
                                break;
                            case "USER":
                                Set<Permission> userPerms = getPermissionsByNamesSafe(permissionsToUse, 
                                    "READ_SCENARIOS", "CREATE_RESERVATIONS", "READ_RESERVATIONS", "CANCEL_RESERVATIONS",
                                    "CREATE_FEEDBACK", "READ_FEEDBACK", "UPDATE_FEEDBACK", "DELETE_FEEDBACK");
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
            
            // Guardar los roles actualizados uno por uno
            if (!rolesToUpdate.isEmpty()) {
                log.info("Guardando {} roles actualizados...", rolesToUpdate.size());
                for (Rol rol : rolesToUpdate) {
                    rolRepository.save(rol);
                }
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
    
    /**
     * Inicializa datos maestros (tipos, ubicaciones, estados, escenarios)
     */
    private void initializeMasterData() {
        try {
            if (tipoEscenarioRepository.count() == 0) {
                log.info("Creando tipos de escenario...");
                createTipoEscenarios();
            }
            
            if (ubicacionRepository.count() == 0) {
                log.info("Creando ubicaciones...");
                createUbicaciones();
            }
            
            if (estadoReservaRepository.count() == 0) {
                log.info("Creando estados de reserva...");
                createEstadosReserva();
            }
            
            if (escenarioRepository.count() == 0) {
                log.info("Creando escenarios USCO...");
                createEscenariosUSCO();
            }
        } catch (Exception e) {
            log.error("Error inicializando datos maestros: {}", e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createTipoEscenarios() {
        List<TipoEscenario> tipos = Arrays.asList(
                TipoEscenario.builder()
                        .nombre("Deportivo")
                        .descripcion("Espacios destinados a actividades deportivas y recreativas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Auditorio")
                        .descripcion("Espacios para eventos, conferencias y presentaciones")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio/Didáctico")
                        .descripcion("Espacios de aprendizaje práctico y experimentación")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Laboratorio")
                        .descripcion("Laboratorios especializados para investigación y práctica")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Biblioteca")
                        .descripcion("Espacios de estudio, consulta y recursos bibliográficos")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Sala de Cómputo")
                        .descripcion("Salas equipadas con computadores y tecnología")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Restaurante")
                        .descripcion("Espacios de alimentación estudiantil y universitaria")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Cafetería")
                        .descripcion("Espacios de encuentro y alimentación informal")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Administrativo")
                        .descripcion("Espacios destinados a actividades administrativas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Bloque Académico")
                        .descripcion("Conjuntos de aulas para clases y actividades académicas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Edificio Académico")
                        .descripcion("Edificios completos destinados a actividades académicas")
                        .build(),
                TipoEscenario.builder()
                        .nombre("Investigación/Práctica")
                        .descripcion("Espacios destinados a investigación y prácticas especializadas")
                        .build()
        );

        tipoEscenarioRepository.saveAll(tipos);
        log.info("Creados {} tipos de escenario", tipos.size());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createUbicaciones() {
        // Sede Central
        Ubicacion sedeCentral = Ubicacion.builder()
                .nombre("Sede Central")
                .direccion("Avenida Pastrana Borrero - Carrera 1")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Subsede Salud
        Ubicacion subsedeSalud = Ubicacion.builder()
                .nombre("Subsede Salud")
                .direccion("Calle 9 # 14-03")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Neiva Centro (Torre Administrativa)
        Ubicacion sedeNeivaCentro = Ubicacion.builder()
                .nombre("Sede Neiva Centro")
                .direccion("Carrera 5 No. 23-40")
                .ciudad("Neiva")
                .pais("Colombia")
                .build();

        // Sede Garzón
        Ubicacion sedeGarzon = Ubicacion.builder()
                .nombre("Sede Garzón")
                .direccion("Vereda Las Termitas")
                .ciudad("Garzón")
                .pais("Colombia")
                .build();

        // Sede Pitalito
        Ubicacion sedePitalito = Ubicacion.builder()
                .nombre("Sede Pitalito")
                .direccion("Kilómetro 1 vía Vereda El Macal")
                .ciudad("Pitalito")
                .pais("Colombia")
                .build();

        // Sede La Plata
        Ubicacion sedeLaPlata = Ubicacion.builder()
                .nombre("Sede La Plata")
                .direccion("Kilómetro 1 vía a Fátima")
                .ciudad("La Plata")
                .pais("Colombia")
                .build();

        List<Ubicacion> ubicaciones = List.of(sedeCentral, subsedeSalud, sedeNeivaCentro, sedeGarzon, sedePitalito, sedeLaPlata);
        ubicacionRepository.saveAll(ubicaciones);
        log.info("Creadas {} ubicaciones", ubicaciones.size());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createEstadosReserva() {
        EstadoReserva pendiente = EstadoReserva.builder()
                .nombre("PENDIENTE")
                .build();

        EstadoReserva aprobada = EstadoReserva.builder()
                .nombre("APROBADA")
                .build();

        EstadoReserva rechazada = EstadoReserva.builder()
                .nombre("RECHAZADA")
                .build();

        EstadoReserva cancelada = EstadoReserva.builder()
                .nombre("CANCELADA")
                .build();

        List<EstadoReserva> estados = List.of(pendiente, aprobada, rechazada, cancelada);
        estadoReservaRepository.saveAll(estados);
        log.info("Creados {} estados de reserva: PENDIENTE, APROBADA, RECHAZADA, CANCELADA", estados.size());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void createEscenariosUSCO() {
        // Obtener ubicaciones
        Ubicacion sedeCentral = ubicacionRepository.findByNombre("Sede Central").orElse(null);
        Ubicacion subsedeSalud = ubicacionRepository.findByNombre("Subsede Salud").orElse(null);
        Ubicacion sedeNeivaCentro = ubicacionRepository.findByNombre("Sede Neiva Centro").orElse(null);
        Ubicacion sedeGarzon = ubicacionRepository.findByNombre("Sede Garzón").orElse(null);
        Ubicacion sedePitalito = ubicacionRepository.findByNombre("Sede Pitalito").orElse(null);
        Ubicacion sedeLaPlata = ubicacionRepository.findByNombre("Sede La Plata").orElse(null);

        // Obtener tipos de escenario
        TipoEscenario deportivo = tipoEscenarioRepository.findByNombre("Deportivo").orElse(null);
        TipoEscenario auditorio = tipoEscenarioRepository.findByNombre("Auditorio").orElse(null);
        TipoEscenario laboratorioDida = tipoEscenarioRepository.findByNombre("Laboratorio/Didáctico").orElse(null);
        TipoEscenario laboratorio = tipoEscenarioRepository.findByNombre("Laboratorio").orElse(null);
        TipoEscenario biblioteca = tipoEscenarioRepository.findByNombre("Biblioteca").orElse(null);
        TipoEscenario salaComputo = tipoEscenarioRepository.findByNombre("Sala de Cómputo").orElse(null);
        TipoEscenario restaurante = tipoEscenarioRepository.findByNombre("Restaurante").orElse(null);
        TipoEscenario cafeteria = tipoEscenarioRepository.findByNombre("Cafetería").orElse(null);
        TipoEscenario administrativo = tipoEscenarioRepository.findByNombre("Administrativo").orElse(null);
        TipoEscenario bloqueAcademico = tipoEscenarioRepository.findByNombre("Bloque Académico").orElse(null);
        TipoEscenario edificioAcademico = tipoEscenarioRepository.findByNombre("Edificio Académico").orElse(null);
        TipoEscenario investigacionPractica = tipoEscenarioRepository.findByNombre("Investigación/Práctica").orElse(null);

        List<Escenario> escenarios = Arrays.asList(
            // Escenarios basados en el CSV oficial de la USCO

            // Escenarios Deportivos
            Escenario.builder()
                .nombre("Cancha de Microfútbol")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Cancha de concreto 40x20m con graderías; arcos fútbol sala; pared tenis; normativa FIFA.")
                .recursos("Área deportiva, cancha de concreto, graderías, arcos de fútbol sala")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Piscina")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(50)
                .descripcion("Piscina semiolímpica 25x10m; profundidad 1.2-2.1m; seis poyetes; baños y vestiers.")
                .recursos("Área deportiva, piscina semiolímpica, baños, vestiers")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Coliseo César Eduardo Medina Perdomo")
                .tipo(deportivo)
                .ubicacion(sedeCentral)
                .capacidad(500)
                .descripcion("Coliseo cubierto 20.7x31.7m; piso NBA Robbins; tableros vidrio; demarcación baloncesto y voleibol.")
                .recursos("Bloque 21, coliseo cubierto, piso NBA, tableros de vidrio")
                .disponible(true)
                .build(),

            // Auditorios
            Escenario.builder()
                .nombre("Auditorio Olga Tony Vidales")
                .tipo(auditorio)
                .ubicacion(sedeCentral)
                .capacidad(300)
                .descripcion("Auditorio renovado; sistema audiovisual moderno; nuevo techo y electricidad.")
                .recursos("Bloque 02, sistema audiovisual, techo renovado")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Auditorio Facultad Economía y Administración")
                .tipo(auditorio)
                .ubicacion(sedeCentral)
                .capacidad(700)
                .descripcion("Auditorio 860m2 acústica; 260m2 aislamiento; aire 87Tn; proyector láser; sonido e iluminación profesional.")
                .recursos("Bloque 30, acústica profesional, aire acondicionado, proyector láser")
                .disponible(true)
                .build(),

            // Laboratorios
            Escenario.builder()
                .nombre("Centro STEM+")
                .tipo(laboratorioDida)
                .ubicacion(sedeCentral)
                .capacidad(31)
                .descripcion("31 PCs diseño 3D; 20 gafas VR; pantallas táctiles; cámaras 360; IA; metaverso.")
                .recursos("Bloque 30, 31 PCs especializados, 20 gafas VR, pantallas táctiles")
                .disponible(true)
                .build(),

            Escenario.builder()
                .nombre("Laboratorio Biología")
                .tipo(laboratorio)
                .ubicacion(sedeCentral)
                .capacidad(30)
                .descripcion("Dotación 243M COP; espectrofotómetro; purificador agua Tipo I/III.")
                .recursos("Bloque 10, espectrofotómetro, purificador de agua")
                .disponible(true)
                .build(),

            // Bibliotecas
            Escenario.builder()
                .nombre("Biblioteca Central Rafael Cortés Murcia")
                .tipo(biblioteca)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Sala general; hemeroteca; sala virtual Ecopetrol; bases de datos.")
                .recursos("Biblioteca, sala general, hemeroteca, bases de datos")
                .disponible(true)
                .build(),

            // Restaurantes
            Escenario.builder()
                .nombre("Restaurante La Venada")
                .tipo(restaurante)
                .ubicacion(sedeCentral)
                .capacidad(600)
                .descripcion("Servicio subvencionado: 260 desayunos; 600 almuerzos; 340 cenas diarias.")
                .recursos("Bloque 20, cocina industrial, comedor")
                .disponible(true)
                .build(),

            // Bloques Académicos
            Escenario.builder()
                .nombre("Bloque 09 Aulas UNO")
                .tipo(bloqueAcademico)
                .ubicacion(sedeCentral)
                .capacidad(200)
                .descripcion("Conjunto de aulas A-09-XXX; capacidades variables.")
                .recursos("Bloque 09, aulas múltiples")
                .disponible(true)
                .build()
        );

        escenarioRepository.saveAll(escenarios);
        log.info("Creados {} escenarios iniciales de la USCO con datos reales", escenarios.size());
    }
}
