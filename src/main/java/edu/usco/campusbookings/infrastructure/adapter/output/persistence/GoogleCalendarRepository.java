package edu.usco.campusbookings.infrastructure.adapter.output.persistence;

import edu.usco.campusbookings.application.port.output.GoogleCalendarRepositoryPort;
import edu.usco.campusbookings.domain.model.Usuario;
import edu.usco.campusbookings.infrastructure.adapter.output.persistence.jpa.SpringDataUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementación del repositorio para operaciones de Google Calendar
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class GoogleCalendarRepository implements GoogleCalendarRepositoryPort {

    private final SpringDataUsuarioRepository usuarioRepository;

    @Override
    public Usuario updateGoogleCalendarTokens(Long userId, String accessToken, String refreshToken, boolean connected) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setGoogleAccessToken(accessToken);
        usuario.setGoogleRefreshToken(refreshToken);
        usuario.setGoogleCalendarConnected(connected);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);
        log.info("Tokens de Google Calendar actualizados para usuario ID: {}", userId);
        
        return savedUsuario;
    }

    @Override
    public Optional<Usuario> findById(Long userId) {
        return usuarioRepository.findById(userId);
    }

    @Override
    public Usuario disconnectGoogleCalendar(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setGoogleAccessToken(null);
        usuario.setGoogleRefreshToken(null);
        usuario.setGoogleCalendarConnected(false);
        
        Usuario savedUsuario = usuarioRepository.save(usuario);
        log.info("Google Calendar desconectado para usuario ID: {}", userId);
        
        return savedUsuario;
    }
}
