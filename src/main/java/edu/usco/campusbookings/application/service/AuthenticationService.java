package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.AuthenticationRequest;
import edu.usco.campusbookings.application.dto.request.RegisterRequest;
import edu.usco.campusbookings.application.dto.response.AuthenticationResponse;
import edu.usco.campusbookings.domain.model.Rol;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Buscar rol por defecto "USUARIO"
        Rol rolUsuario = rolService.findByNombre("USUARIO")
            .orElseThrow(() -> new IllegalStateException("Rol USUARIO no encontrado en la base de datos"));
        
        var usuario = Usuario.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .rol(rolUsuario) // Asignar rol por defecto
            .build();

        usuario = usuarioService.save(usuario);
        
        // Crear UserDetails con el rol real del usuario
        String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USUARIO";
        String springRole = "ADMIN".equals(roleName) ? "ADMIN" : "USER";
        
        UserDetails userDetails = User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(springRole)
            .build();
        
        // Crear claims adicionales para el token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", usuario.getId());
        extraClaims.put("email", usuario.getEmail());
        extraClaims.put("nombre", usuario.getNombre());
        extraClaims.put("apellido", usuario.getApellido());
        extraClaims.put("rol", roleName);
        
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .rol(roleName)
            .userId(usuario.getId())
            .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var usuario = usuarioService.findByEmail(request.getEmail());
        
        // Usar el rol real del usuario
        String roleName = usuario.getRol() != null ? usuario.getRol().getNombre() : "USUARIO";
        String springRole = "ADMIN".equals(roleName) ? "ADMIN" : "USER";
        
        UserDetails userDetails = User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles(springRole)
            .build();
        
        // Crear claims adicionales para el token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", usuario.getId());
        extraClaims.put("email", usuario.getEmail());
        extraClaims.put("nombre", usuario.getNombre());
        extraClaims.put("apellido", usuario.getApellido());
        extraClaims.put("rol", roleName);
        
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .rol(roleName)
            .userId(usuario.getId())
            .build();
    }
} 