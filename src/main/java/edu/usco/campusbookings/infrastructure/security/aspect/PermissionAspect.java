package edu.usco.campusbookings.infrastructure.security.aspect;

import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.Permission;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.security.annotation.RequiresPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import edu.usco.campusbookings.application.port.output.ScenarioTypePermissionRepositoryPort;

/**
 * Aspecto que maneja la validación de permisos usando la anotación @RequiresPermission
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final UsuarioService usuarioService;
    private final ScenarioTypePermissionRepositoryPort scenarioTypePermissionRepositoryPort;

    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        log.debug("Verificando permiso: {} - {}", requiresPermission.resource(), requiresPermission.action());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuario no autenticado");
        }

        String userEmail = authentication.getName();
        log.debug("Verificando permisos para usuario: {}", userEmail);

        try {
            Usuario usuario = usuarioService.findByEmailWithPermissions(userEmail);
            
            // Si es ADMIN, permitir todo
            if (usuario.getRol() != null && "ADMIN".equals(usuario.getRol().getNombre())) {
                log.debug("Usuario {} es ADMIN, acceso permitido", userEmail);
                return joinPoint.proceed();
            }

            // Verificar si el usuario tiene el permiso específico
            if (hasPermission(usuario, requiresPermission.resource(), requiresPermission.action(), joinPoint)) {
                log.debug("Usuario {} tiene el permiso requerido", userEmail);
                return joinPoint.proceed();
            } else {
                log.warn("Usuario {} no tiene el permiso requerido: {} - {}", 
                    userEmail, requiresPermission.resource(), requiresPermission.action());
                throw new AccessDeniedException(requiresPermission.message());
            }
            
        } catch (Exception e) {
            log.error("Error verificando permisos para usuario {}: {}", userEmail, e.getMessage());
            throw new AccessDeniedException("Error verificando permisos: " + e.getMessage());
        }
    }

    private boolean hasPermission(Usuario usuario, String resource, String action, ProceedingJoinPoint joinPoint) {
        if (usuario.getRol() == null) {
            return false;
        }

        Rol rol = usuario.getRol();
        if (rol.getPermissions() == null || rol.getPermissions().isEmpty()) {
            return false;
        }

        // Verificar permiso específico
        boolean hasSpecificPermission = rol.getPermissions().stream()
                .anyMatch(permission -> 
                    resource.equals(permission.getResource()) && 
                    action.equals(permission.getAction()));

        // Verificar permiso de MANAGE (que incluye todas las acciones)
        boolean hasManagePermission = rol.getPermissions().stream()
                .anyMatch(permission -> 
                    resource.equals(permission.getResource()) && 
                    "MANAGE".equals(permission.getAction()));

        boolean hasPermission = hasSpecificPermission || hasManagePermission;

        // Reglas adicionales: si es ESCENARIOS y acción UPDATE/CREATE/DELETE, permitir por tipo asignado
        if (!hasPermission && "ESCENARIOS".equals(resource) && ("UPDATE".equals(action) || "CREATE".equals(action) || "DELETE".equals(action))) {
            // Intentar inferir el tipo de escenario a partir de los argumentos
            String tipoInferido = null;
            for (Object arg : joinPoint.getArgs()) {
                if (arg instanceof edu.usco.campusbookings.application.dto.request.EscenarioRequest req) {
                    tipoInferido = req.getTipo();
                    break;
                }
            }
            if (tipoInferido != null) {
                boolean hasTypePermission = scenarioTypePermissionRepositoryPort
                    .existsByUsuarioEmailAndTipoNombreAndAction(usuario.getEmail(), tipoInferido, action);
                if (!hasTypePermission && "UPDATE".equals(action)) {
                    // Si tiene MANAGE por tipo, también permitir
                    hasTypePermission = scenarioTypePermissionRepositoryPort
                        .existsByUsuarioEmailAndTipoNombreAndAction(usuario.getEmail(), tipoInferido, "MANAGE");
                }
                if (hasTypePermission) {
                    log.debug("Usuario {} tiene permiso por tipo '{}' para acción {}", usuario.getEmail(), tipoInferido, action);
                    return true;
                }
            }
        }
        
        log.debug("Usuario {} - Rol: {} - Permiso {}-{}: {}", 
            usuario.getEmail(), rol.getNombre(), resource, action, hasPermission);
            
        return hasPermission;
    }
}

