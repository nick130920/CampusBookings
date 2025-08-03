package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.AuthenticationRequest;
import edu.usco.campusbookings.application.dto.request.RegisterRequest;
import edu.usco.campusbookings.application.dto.response.AuthenticationResponse;
import edu.usco.campusbookings.application.service.AuthenticationService;
import edu.usco.campusbookings.application.service.UsuarioService;
import edu.usco.campusbookings.application.util.PasswordValidationUtil;
import edu.usco.campusbookings.domain.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de autenticación y autorización")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UsuarioService usuarioService;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<AuthenticationResponse> register(
        @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Autenticar usuario")
    public ResponseEntity<AuthenticationResponse> authenticate(
        @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener información del usuario actual")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        Usuario usuario = usuarioService.findByEmail(email);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", usuario.getId());
        userInfo.put("nombre", usuario.getNombre());
        userInfo.put("apellido", usuario.getApellido());
        userInfo.put("email", usuario.getEmail());
        userInfo.put("rol", usuario.getRol() != null ? usuario.getRol().getNombre() : "USUARIO");
        userInfo.put("isAdmin", usuario.getRol() != null && "ADMIN".equals(usuario.getRol().getNombre()));
        
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Validar token JWT")
    public ResponseEntity<Map<String, Object>> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", authentication != null && authentication.isAuthenticated());
        response.put("username", authentication != null ? authentication.getName() : null);
        response.put("authorities", authentication != null ? authentication.getAuthorities() : null);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-password")
    @Operation(summary = "Validar fortaleza de contraseña")
    public ResponseEntity<Map<String, Object>> validatePassword(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        
        if (password == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("message", "La contraseña es obligatoria");
            return ResponseEntity.badRequest().body(error);
        }
        
        Map<String, Object> validation = PasswordValidationUtil.validatePasswordStrength(password);
        return ResponseEntity.ok(validation);
    }

    @GetMapping("/password-requirements")
    @Operation(summary = "Obtener requisitos de contraseña")
    public ResponseEntity<Map<String, Object>> getPasswordRequirements() {
        Map<String, Object> requirements = new HashMap<>();
        requirements.put("minLength", 8);
        requirements.put("maxLength", 100);
        requirements.put("requireLowercase", true);
        requirements.put("requireUppercase", true);
        requirements.put("requireDigit", true);
        requirements.put("requireSpecialChar", true);
        requirements.put("allowedSpecialChars", "@$!%*?&");
        requirements.put("description", "La contraseña debe contener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        
        return ResponseEntity.ok(requirements);
    }
} 