package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.ForgotPasswordRequest;
import edu.usco.campusbookings.application.dto.request.ResetPasswordRequest;
import edu.usco.campusbookings.application.dto.request.VerifyCodeRequest;
import edu.usco.campusbookings.application.dto.response.VerifyCodeResponse;
import edu.usco.campusbookings.application.port.input.PasswordResetUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/forgot-password")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PasswordResetController {
    
    private final PasswordResetUseCase passwordResetUseCase;
    
    /**
     * Envía un código de verificación al email del usuario
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendPasswordResetCode(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            log.info("POST /api/auth/forgot-password/send-code - Email: {}", request.getEmail());
            
            String message = passwordResetUseCase.sendPasswordResetCode(request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en send-code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (IllegalStateException e) {
            log.warn("Rate limiting en send-code: {}", e.getMessage());
            return ResponseEntity.status(429).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error interno en send-code", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor. Por favor intenta nuevamente."
            ));
        }
    }
    
    /**
     * Verifica el código de verificación y retorna un token temporal
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyPasswordResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            log.info("POST /api/auth/forgot-password/verify-code - Email: {}", request.getEmail());
            
            VerifyCodeResponse response = passwordResetUseCase.verifyPasswordResetCode(request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", response.getToken(),
                    "message", response.getMessage()
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en verify-code: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error interno en verify-code", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor. Por favor intenta nuevamente."
            ));
        }
    }
    
    /**
     * Cambia la contraseña del usuario usando el token temporal
     */
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            log.info("POST /api/auth/forgot-password/reset - Token: {}...", 
                    request.getToken().length() > 8 ? request.getToken().substring(0, 8) : "corto");
            
            String message = passwordResetUseCase.resetPassword(request);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
            ));
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación en reset: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Error interno en reset", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Error interno del servidor. Por favor intenta nuevamente."
            ));
        }
    }
}