package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.request.ForgotPasswordRequest;
import edu.usco.campusbookings.application.dto.request.ResetPasswordRequest;
import edu.usco.campusbookings.application.dto.request.VerifyCodeRequest;
import edu.usco.campusbookings.application.dto.response.VerifyCodeResponse;
import edu.usco.campusbookings.application.exception.UsuarioNotFoundException;
import edu.usco.campusbookings.application.port.input.PasswordResetUseCase;
import edu.usco.campusbookings.application.port.output.EmailServicePort;
import edu.usco.campusbookings.application.port.output.PasswordResetRepositoryPort;
import edu.usco.campusbookings.application.port.output.UsuarioRepositoryPort;
import edu.usco.campusbookings.domain.model.PasswordResetToken;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class PasswordResetService implements PasswordResetUseCase {
    
    private final PasswordResetRepositoryPort passwordResetRepository;
    private final UsuarioRepositoryPort usuarioRepository;
    private final EmailServicePort emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    
    // Configuración
    private static final int CODE_EXPIRY_MINUTES = 15;
    private static final int TOKEN_EXPIRY_MINUTES = 30;
    private static final int MAX_ATTEMPTS_PER_HOUR = 3;
    
    public PasswordResetService(
            PasswordResetRepositoryPort passwordResetRepository,
            UsuarioRepositoryPort usuarioRepository,
            EmailServicePort emailService,
            PasswordEncoder passwordEncoder) {
        this.passwordResetRepository = passwordResetRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public String sendPasswordResetCode(ForgotPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        
        log.info("Iniciando proceso de recuperación de contraseña para email: {}", email);
        
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("No existe una cuenta con este correo electrónico"));
        
        // Rate limiting: verificar intentos recientes
        long activeTokens = passwordResetRepository.countActiveTokensByEmail(email);
        if (activeTokens >= MAX_ATTEMPTS_PER_HOUR) {
            throw new IllegalStateException("Demasiados intentos. Por favor espera antes de solicitar un nuevo código.");
        }
        
        // Limpiar tokens anteriores
        passwordResetRepository.deleteByEmailAndUsedOrExpired(email);
        
        // Generar código de 6 dígitos
        String code = generateSecureCode();
        
        // Crear token de reset
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setCode(code);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES));
        resetToken.setUsed(false);
        resetToken.setAttempts(0);
        
        // Guardar token
        passwordResetRepository.save(resetToken);
        
        // Enviar email con el código
        try {
            emailService.sendPasswordResetEmail(email, usuario.getNombre(), code);
            log.info("Código de recuperación enviado exitosamente a: {}", email);
        } catch (Exception e) {
            log.error("Error al enviar email de recuperación a: {}", email, e);
            throw new RuntimeException("Error al enviar el código. Por favor intenta nuevamente.");
        }
        
        return "Código de verificación enviado a tu correo electrónico";
    }
    
    @Override
    public VerifyCodeResponse verifyPasswordResetCode(VerifyCodeRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        String code = request.getCode();
        
        log.info("Verificando código de recuperación para email: {}", email);
        
        // Buscar token por email y código
        PasswordResetToken resetToken = passwordResetRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido o expirado"));
        
        // Incrementar intentos
        resetToken.incrementAttempts();
        passwordResetRepository.save(resetToken);
        
        // Verificar si el token es válido
        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new IllegalArgumentException("El código ha expirado. Solicita uno nuevo.");
            } else if (resetToken.getUsed()) {
                throw new IllegalArgumentException("Este código ya fue utilizado.");
            } else if (resetToken.getAttempts() >= 3) {
                throw new IllegalArgumentException("Demasiados intentos incorrectos. Solicita un nuevo código.");
            }
        }
        
        // Generar token temporal para el siguiente paso
        String tempToken = UUID.randomUUID().toString();
        resetToken.setToken(tempToken);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)); // Extender expiración
        passwordResetRepository.save(resetToken);
        
        log.info("Código verificado exitosamente para email: {}", email);
        
        return new VerifyCodeResponse(tempToken, "Código verificado correctamente");
    }
    
    @Override
    public String resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        
        log.info("Procesando cambio de contraseña con token: {}", token.substring(0, 8) + "...");
        
        // Buscar token
        PasswordResetToken resetToken = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));
        
        // Verificar que el token es válido
        if (!resetToken.isValid()) {
            if (resetToken.isExpired()) {
                throw new IllegalArgumentException("El token ha expirado. Inicia el proceso nuevamente.");
            } else if (resetToken.getUsed()) {
                throw new IllegalArgumentException("Este token ya fue utilizado.");
            }
        }
        
        // Buscar usuario
        Usuario usuario = usuarioRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado"));
        
        // Cambiar contraseña
        String encodedPassword = passwordEncoder.encode(newPassword);
        usuario.setPassword(encodedPassword);
        usuarioRepository.save(usuario);
        
        // Marcar token como usado
        resetToken.markAsUsed();
        passwordResetRepository.save(resetToken);
        
        log.info("Contraseña cambiada exitosamente para usuario: {}", resetToken.getEmail());
        
        return "Contraseña restablecida exitosamente";
    }
    
    /**
     * Genera un código seguro de 6 dígitos
     */
    private String generateSecureCode() {
        int code = 100000 + secureRandom.nextInt(900000); // 6 dígitos: 100000-999999
        return String.valueOf(code);
    }
}