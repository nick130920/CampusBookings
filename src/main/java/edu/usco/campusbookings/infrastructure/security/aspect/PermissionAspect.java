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

/**
 * Aspecto que maneja la validación de permisos usando la anotación @RequiresPermission
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final UsuarioService usuarioService;

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
            Usuario usuario = usuarioService.findByEmail(userEmail);
            
            // Si es ADMIN, permitir todo
            if (usuario.getRol() != null && "ADMIN".equals(usuario.getRol().getNombre())) {
                log.debug("Usuario {} es ADMIN, acceso permitido", userEmail);
                return joinPoint.proceed();
            }

            // Verificar si el usuario tiene el permiso específico
            if (hasPermission(usuario, requiresPermission.resource(), requiresPermission.action())) {
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

    private boolean hasPermission(Usuario usuario, String resource, String action) {
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
        
        log.debug("Usuario {} - Rol: {} - Permiso {}-{}: {}", 
            usuario.getEmail(), rol.getNombre(), resource, action, hasPermission);
            
        return hasPermission;
    }
}

