package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.AuthenticationRequest;
import edu.usco.campusbookings.application.dto.request.RegisterRequest;
import edu.usco.campusbookings.application.dto.response.AuthenticationResponse;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var usuario = Usuario.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

        usuario = usuarioService.save(usuario);
        UserDetails userDetails = User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles("USER")
            .build();
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
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
        UserDetails userDetails = User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .roles("USER")
            .build();
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
            .token(jwtToken)
            .email(usuario.getEmail())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .build();
    }
} 