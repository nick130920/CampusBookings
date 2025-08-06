package edu.usco.campusbookings.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para AuditorAwareImpl
 */
class AuditorAwareImplTest {

    private AuditorAwareImpl auditorAware;

    @BeforeEach
    void setUp() {
        auditorAware = new AuditorAwareImpl();
        // Limpiar el contexto de seguridad antes de cada prueba
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // Limpiar el contexto de seguridad después de cada prueba
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentAuditor_WhenNoAuthentication_ReturnsSYSTEM() {
        // Given: No hay autenticación en el contexto
        
        // When
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        
        // Then
        assertTrue(auditor.isPresent());
        assertEquals("SYSTEM", auditor.get());
    }

    @Test
    void getCurrentAuditor_WhenAnonymousUser_ReturnsSYSTEM() {
        // Given: Usuario anónimo
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "anonymousUser", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // When
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        
        // Then
        assertTrue(auditor.isPresent());
        assertEquals("SYSTEM", auditor.get());
    }

    @Test
    void getCurrentAuditor_WhenUserDetails_ReturnsUsername() {
        // Given: Usuario autenticado con UserDetails
        UserDetails userDetails = new User(
            "admin@usco.edu.co",
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // When
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        
        // Then
        assertTrue(auditor.isPresent());
        assertEquals("admin@usco.edu.co", auditor.get());
    }

    @Test
    void getCurrentAuditor_WhenStringPrincipal_ReturnsString() {
        // Given: Principal como String directo
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            "user@usco.edu.co", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // When
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        
        // Then
        assertTrue(auditor.isPresent());
        assertEquals("user@usco.edu.co", auditor.get());
    }

    @Test
    void getCurrentAuditor_WhenUnknownPrincipalType_ReturnsSYSTEM() {
        // Given: Tipo de principal desconocido
        Object unknownPrincipal = new Object();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            unknownPrincipal, null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        // When
        Optional<String> auditor = auditorAware.getCurrentAuditor();
        
        // Then
        assertTrue(auditor.isPresent());
        assertEquals("SYSTEM", auditor.get());
    }
}