package edu.usco.campusbookings.infrastructure.security;

import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Cargando detalles del usuario para email: {}", email);
        
        try {
            Usuario usuario = usuarioService.findByEmail(email);
            
            if (usuario == null) {
                log.warn("Usuario no encontrado: {}", email);
                throw new UsernameNotFoundException("Usuario no encontrado: " + email);
            }
            
            // Determinar el rol del usuario - CORREGIDO para usar roles actuales
            String roleName = "ROLE_USER"; // Por defecto
            if (usuario.getRol() != null) {
                String userRol = usuario.getRol().getNombre();
                if ("ADMIN".equals(userRol)) {
                    roleName = "ROLE_ADMIN";
                } else if ("USER".equals(userRol)) { 
                    roleName = "ROLE_USER";
                } else if ("COORDINATOR".equals(userRol)) {
                    roleName = "ROLE_COORDINATOR";
                }
            }
            
            log.info("Usuario cargado exitosamente: {} con rol: {}", email, roleName);
            
            return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
            );
            
        } catch (Exception e) {
            log.error("Error cargando usuario {}: {}", email, e.getMessage(), e);
            if (e instanceof UsernameNotFoundException) {
                throw e;
            }
            throw new UsernameNotFoundException("Error cargando usuario: " + email, e);
        }
    }
} 