package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.request.GoogleCalendarAuthRequest;
import edu.usco.campusbookings.application.dto.response.GoogleCalendarStatusResponse;
import edu.usco.campusbookings.application.dto.response.GoogleCalendarSyncResponse;
import edu.usco.campusbookings.application.port.input.GoogleCalendarUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la integración con Google Calendar
 */
@Slf4j
@RestController
@RequestMapping("/api/google-calendar")
@RequiredArgsConstructor
@Tag(name = "Google Calendar", description = "Endpoints para integración con Google Calendar")
public class GoogleCalendarController {

    private final GoogleCalendarUseCase googleCalendarUseCase;

    @GetMapping("/authorization-url")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener URL de autorización de Google Calendar")
    public ResponseEntity<GoogleCalendarStatusResponse> getAuthorizationUrl() {
        log.info("Solicitud de URL de autorización de Google Calendar");
        GoogleCalendarStatusResponse response = googleCalendarUseCase.getAuthorizationUrl();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/connect")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Conectar cuenta con Google Calendar")
    public ResponseEntity<GoogleCalendarStatusResponse> connectToGoogleCalendar(
            @RequestBody GoogleCalendarAuthRequest request) {
        log.info("Solicitud de conexión a Google Calendar");
        GoogleCalendarStatusResponse response = googleCalendarUseCase.connectToGoogleCalendar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/disconnect")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Desconectar cuenta de Google Calendar")
    public ResponseEntity<GoogleCalendarStatusResponse> disconnectFromGoogleCalendar() {
        log.info("Solicitud de desconexión de Google Calendar");
        GoogleCalendarStatusResponse response = googleCalendarUseCase.disconnectFromGoogleCalendar();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener estado de conexión con Google Calendar")
    public ResponseEntity<GoogleCalendarStatusResponse> getConnectionStatus() {
        GoogleCalendarStatusResponse response = googleCalendarUseCase.getConnectionStatus();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Sincronizar todas las reservas con Google Calendar", 
               description = "Sincroniza todas las reservas aprobadas del usuario que no estén ya sincronizadas con Google Calendar")
    public ResponseEntity<GoogleCalendarSyncResponse> syncAllReservations() {
        try {
            log.info("Solicitud de sincronización masiva con Google Calendar");
            GoogleCalendarSyncResponse response = googleCalendarUseCase.syncAllUserReservations();
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Error durante la sincronización masiva", e);
            
            GoogleCalendarSyncResponse response = GoogleCalendarSyncResponse.builder()
                    .success(false)
                    .connected(false)
                    .message("Error durante la sincronización: " + e.getMessage())
                    .totalReservas(0)
                    .reservasSincronizadas(0)
                    .errores(1)
                    .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
