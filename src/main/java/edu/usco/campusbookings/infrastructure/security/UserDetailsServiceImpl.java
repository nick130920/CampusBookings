package edu.usco.campusbookings.infrastructure.security;

import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(email);
        
        // Determinar el rol del usuario
        String roleName = "ROLE_USER"; // Por defecto
        if (usuario.getRol() != null) {
            String userRol = usuario.getRol().getNombre();
            if ("ADMIN".equals(userRol)) {
                roleName = "ROLE_ADMIN";
            } else if ("USUARIO".equals(userRol)) {
                roleName = "ROLE_USER";
            }
        }
        
        return new User(
            usuario.getEmail(),
            usuario.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
} 