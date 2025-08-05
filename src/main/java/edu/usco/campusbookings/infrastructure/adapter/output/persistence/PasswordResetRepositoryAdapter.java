package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.PasswordResetRepositoryPort;
import edu.usco.campusbookings.domain.model.PasswordResetToken;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetRepositoryAdapter implements PasswordResetRepositoryPort {
    
    private final PasswordResetTokenRepository repository;
    
    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return repository.save(token);
    }
    
    @Override
    public Optional<PasswordResetToken> findByEmailAndCode(String email, String code) {
        return repository.findByEmailAndCodeAndUsedFalseAndExpiresAtAfter(
                email, code, LocalDateTime.now());
    }
    
    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return repository.findByTokenAndUsedFalseAndExpiresAtAfter(
                token, LocalDateTime.now());
    }
    
    @Override
    public void deleteByEmailAndUsedOrExpired(String email) {
        // Eliminar tokens usados o expirados
        repository.deleteByEmailAndUsedTrue(email);
        repository.deleteByEmailAndExpiresAtBefore(email, LocalDateTime.now());
    }
    
    @Override
    public long countActiveTokensByEmail(String email) {
        return repository.countByEmailAndUsedFalseAndExpiresAtAfterAndCreatedAtAfter(
                email, LocalDateTime.now(), LocalDateTime.now().minusHours(1));
    }
}