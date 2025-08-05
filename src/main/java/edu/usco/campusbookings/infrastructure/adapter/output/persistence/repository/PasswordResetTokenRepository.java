package edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository;

import edu.usco.campusbookings.domain.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Busca un token por email y código que no esté usado y no haya expirado
     */
    Optional<PasswordResetToken> findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
            String email, String code, LocalDateTime now);
    
    /**
     * Busca un token por el token string que no esté usado y no haya expirado
     */
    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiresAtAfter(
            String token, LocalDateTime now);
    
    /**
     * Elimina tokens usados por email
     */
    @Modifying
    @Transactional
    void deleteByEmailAndUsedTrue(String email);
    
    /**
     * Elimina tokens expirados por email
     */
    @Modifying
    @Transactional
    void deleteByEmailAndExpiresAtBefore(String email, LocalDateTime expiryTime);
    
    /**
     * Cuenta tokens activos por email en la última hora (para rate limiting)
     */
    long countByEmailAndUsedFalseAndExpiresAtAfterAndCreatedAtAfter(
            String email, LocalDateTime notExpiredAfter, LocalDateTime createdAfter);
    
    /**
     * Limpieza automática de tokens expirados (para tarea programada)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :now OR p.used = true")
    void deleteExpiredAndUsedTokens(@Param("now") LocalDateTime now);
}