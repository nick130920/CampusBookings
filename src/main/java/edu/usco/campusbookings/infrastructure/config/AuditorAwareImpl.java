package edu.usco.campusbookings.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación personalizada de AuditorAware para capturar automáticamente
 * el usuario actual del contexto de Spring Security para auditoría.
 * 
 * Este componente se ejecuta automáticamente cuando Spring Data JPA realiza
 * operaciones de auditoría (@CreatedBy, @LastModifiedBy).
 * 
 * @author CampusBookings System
 */
@Component
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Casos donde no hay autenticación válida
            if (authentication == null || !authentication.isAuthenticated()) {
                log.debug("No hay autenticación válida, usando SYSTEM como auditor");
                return Optional.of("SYSTEM");
            }

            // Usuario anónimo
            if ("anonymousUser".equals(authentication.getPrincipal())) {
                log.debug("Usuario anónimo detectado, usando SYSTEM como auditor");
                return Optional.of("SYSTEM");
            }

            // Si el principal es un UserDetails (caso común con JWT)
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String username = userDetails.getUsername();
                log.debug("Auditor capturado desde UserDetails: {}", username);
                return Optional.of(username);
            }

            // Si el principal es un String (email o username directo)
            if (authentication.getPrincipal() instanceof String) {
                String username = (String) authentication.getPrincipal();
                log.debug("Auditor capturado desde String principal: {}", username);
                return Optional.of(username);
            }

            // Caso inesperado: tipo de principal desconocido
            log.warn("Tipo de principal desconocido: {}, usando SYSTEM como auditor", 
                     authentication.getPrincipal().getClass().getName());
            return Optional.of("SYSTEM");

        } catch (Exception e) {
            // En caso de cualquier error, usar SYSTEM para evitar fallos
            log.error("Error al obtener el auditor actual: {}, usando SYSTEM como fallback", e.getMessage());
            return Optional.of("SYSTEM");
        }
    }
}