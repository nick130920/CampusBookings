package edu.usco.campusbookings.infrastructure.security;

import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

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
            
            // Obtener rol del usuario
            String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USER";
            String springRole = "ADMIN".equals(roleName) ? "ADMIN" : "USER";
            
            log.info("Usuario cargado exitosamente: {} con rol: {}", email, roleName);
            
            return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(springRole)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
                
        } catch (Exception e) {
            log.error("Error cargando usuario {}: {}", email, e.getMessage(), e);
            throw new UsernameNotFoundException("Error cargando usuario: " + email, e);
        }
    }
}
