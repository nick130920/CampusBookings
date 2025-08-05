package edu.usco.campusbookings.application.port.output;

import edu.usco.campusbookings.domain.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetRepositoryPort {
    
    /**
     * Guarda un token de reset de contraseña
     */
    PasswordResetToken save(PasswordResetToken token);
    
    /**
     * Busca un token por email y código
     */
    Optional<PasswordResetToken> findByEmailAndCode(String email, String code);
    
    /**
     * Busca un token por el token string
     */
    Optional<PasswordResetToken> findByToken(String token);
    
    /**
     * Elimina todos los tokens expirados o usados para un email
     */
    void deleteByEmailAndUsedOrExpired(String email);
    
    /**
     * Cuenta cuántos tokens activos tiene un email (para rate limiting)
     */
    long countActiveTokensByEmail(String email);
}