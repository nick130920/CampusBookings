package edu.usco.campusbookings.application.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import edu.usco.campusbookings.application.dto.request.GoogleCalendarAuthRequest;
import edu.usco.campusbookings.application.dto.response.GoogleCalendarStatusResponse;
import edu.usco.campusbookings.application.exception.GoogleCalendarException;
import edu.usco.campusbookings.application.port.input.GoogleCalendarUseCase;
import edu.usco.campusbookings.application.port.output.GoogleCalendarRepositoryPort;
import edu.usco.campusbookings.domain.model.Reserva;
import edu.usco.campusbookings.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Servicio para manejar la integración con Google Calendar
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarService implements GoogleCalendarUseCase {

    private final GoogleAuthorizationCodeFlow authFlow;
    private final Calendar.Builder calendarBuilder;
    private final GoogleCalendarRepositoryPort googleCalendarRepositoryPort;
    private final UsuarioService usuarioService;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    @Override
    public GoogleCalendarStatusResponse getAuthorizationUrl() {
        try {
            Usuario currentUser = getCurrentUser();
            if (currentUser.getGoogleCalendarConnected() != null && currentUser.getGoogleCalendarConnected()) {
                return GoogleCalendarStatusResponse.builder()
                        .connected(true)
                        .message("Ya estás conectado a Google Calendar")
                        .build();
            }

            String authUrl = authFlow.newAuthorizationUrl()
                    .setRedirectUri(redirectUri)
                    .build();

            return GoogleCalendarStatusResponse.builder()
                    .connected(false)
                    .authorizationUrl(authUrl)
                    .message("URL de autorización generada")
                    .build();

        } catch (Exception e) {
            log.error("Error generando URL de autorización", e);
            throw new GoogleCalendarException("Error al generar URL de autorización: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GoogleCalendarStatusResponse connectToGoogleCalendar(GoogleCalendarAuthRequest request) {
        try {
            Usuario currentUser = getCurrentUser();
            
            // Intercambiar código por tokens
            GoogleTokenResponse tokenResponse = authFlow.newTokenRequest(request.getAuthorizationCode())
                    .setRedirectUri(redirectUri)
                    .execute();

            // Guardar tokens en la base de datos
            googleCalendarRepositoryPort.updateGoogleCalendarTokens(
                    currentUser.getId(),
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    true
            );

            log.info("Usuario {} conectado exitosamente a Google Calendar", currentUser.getEmail());

            return GoogleCalendarStatusResponse.builder()
                    .connected(true)
                    .message("Conectado exitosamente a Google Calendar")
                    .build();

        } catch (Exception e) {
            log.error("Error conectando a Google Calendar", e);
            throw new GoogleCalendarException("Error al conectar con Google Calendar: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public GoogleCalendarStatusResponse disconnectFromGoogleCalendar() {
        try {
            Usuario currentUser = getCurrentUser();
            
            googleCalendarRepositoryPort.disconnectGoogleCalendar(currentUser.getId());
            
            log.info("Usuario {} desconectado de Google Calendar", currentUser.getEmail());

            return GoogleCalendarStatusResponse.builder()
                    .connected(false)
                    .message("Desconectado exitosamente de Google Calendar")
                    .build();

        } catch (Exception e) {
            log.error("Error desconectando de Google Calendar", e);
            throw new GoogleCalendarException("Error al desconectar de Google Calendar: " + e.getMessage());
        }
    }

    @Override
    public GoogleCalendarStatusResponse getConnectionStatus() {
        Usuario currentUser = getCurrentUser();
        boolean connected = currentUser.getGoogleCalendarConnected() != null && currentUser.getGoogleCalendarConnected();
        
        return GoogleCalendarStatusResponse.builder()
                .connected(connected)
                .message(connected ? "Conectado a Google Calendar" : "No conectado a Google Calendar")
                .build();
    }

    @Override
    public String syncReservationWithCalendar(Reserva reserva) {
        try {
            Usuario usuario = reserva.getUsuario();
            
            if (usuario.getGoogleCalendarConnected() == null || !usuario.getGoogleCalendarConnected()) {
                log.debug("Usuario {} no tiene Google Calendar conectado, saltando sincronización", usuario.getEmail());
                return null;
            }

            Calendar service = getCalendarService(usuario);
            Event event = createEventFromReservation(reserva);

            Event createdEvent;
            if (reserva.getGoogleCalendarEventId() != null) {
                // Actualizar evento existente usando PUT (recomendado por la API)
                createdEvent = service.events().update("primary", reserva.getGoogleCalendarEventId(), event).execute();
                log.info("Evento actualizado en Google Calendar: {}", createdEvent.getId());
            } else {
                // Crear nuevo evento en el calendario primario
                createdEvent = service.events().insert("primary", event)
                        .setSendNotifications(true) // Enviar notificaciones por email
                        .execute();
                log.info("Evento creado en Google Calendar: {}", createdEvent.getId());
            }

            return createdEvent.getId();

        } catch (Exception e) {
            log.error("Error sincronizando reserva con Google Calendar", e);
            throw new GoogleCalendarException("Error al sincronizar con Google Calendar: " + e.getMessage());
        }
    }

    @Override
    public void deleteEventFromCalendar(String eventId) {
        try {
            Usuario currentUser = getCurrentUser();
            
            if (currentUser.getGoogleCalendarConnected() == null || !currentUser.getGoogleCalendarConnected()) {
                log.debug("Usuario {} no tiene Google Calendar conectado, saltando eliminación", currentUser.getEmail());
                return;
            }

            Calendar service = getCalendarService(currentUser);
            service.events().delete("primary", eventId)
                    .setSendNotifications(true) // Notificar a invitados sobre la cancelación
                    .execute();
            
            log.info("Evento eliminado de Google Calendar: {}", eventId);

        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                log.warn("Evento {} ya fue eliminado o no existe en Google Calendar", eventId);
                // No lanzar excepción si el evento ya no existe
                return;
            }
            log.error("Error de Google API eliminando evento: {}", e.getMessage());
            throw new GoogleCalendarException("Error de Google Calendar: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error eliminando evento de Google Calendar", e);
            throw new GoogleCalendarException("Error al eliminar evento de Google Calendar: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void syncAllUserReservations() {
        // Este método se puede implementar más adelante para sincronización masiva
        log.info("Sincronización masiva de reservas no implementada aún");
    }

    private Calendar getCalendarService(Usuario usuario) throws Exception {
        Credential credential = authFlow.loadCredential(usuario.getId().toString());
        
        if (credential == null) {
            // Recrear credential desde los tokens guardados
            credential = authFlow.createAndStoreCredential(
                    new GoogleTokenResponse()
                            .setAccessToken(usuario.getGoogleAccessToken())
                            .setRefreshToken(usuario.getGoogleRefreshToken()),
                    usuario.getId().toString()
            );
        }

        return calendarBuilder.setHttpRequestInitializer(credential).build();
    }

    private Event createEventFromReservation(Reserva reserva) {
        Event event = new Event();
        
        // Título del evento (resumen)
        String summary = String.format("📅 %s - %s", 
                reserva.getEscenario().getNombre(),
                reserva.getEstado().getNombre());
        event.setSummary(summary);
        
        // Descripción detallada
        String description = String.format(
                "🏛️ Reserva de Escenario - USCO\n\n" +
                "👤 Reservado por: %s %s\n" +
                "📍 Escenario: %s\n" +
                "📊 Estado: %s\n" +
                "🆔 ID Reserva: %d\n" +
                "%s\n\n" +
                "Generado por CampusBookings - Universidad Surcolombiana",
                reserva.getUsuario().getNombre(),
                reserva.getUsuario().getApellido(),
                reserva.getEscenario().getNombre(),
                reserva.getEstado().getNombre(),
                reserva.getId(),
                reserva.getObservaciones() != null ? "📝 Observaciones: " + reserva.getObservaciones() : ""
        );
        event.setDescription(description);

        // Ubicación (si el escenario tiene ubicación)
        // Nota: Asumo que ubicacion es un String. Si es un objeto, ajustar según el modelo
        event.setLocation("Universidad Surcolombiana - " + reserva.getEscenario().getNombre());

        // Zona horaria de Colombia (Bogotá)
        String timeZone = "America/Bogota";
        
        // Fecha y hora de inicio
        EventDateTime startDateTime = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        Date.from(reserva.getFechaInicio().atZone(ZoneId.of(timeZone)).toInstant())))
                .setTimeZone(timeZone);
        event.setStart(startDateTime);

        // Fecha y hora de fin
        EventDateTime endDateTime = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(
                        Date.from(reserva.getFechaFin().atZone(ZoneId.of(timeZone)).toInstant())))
                .setTimeZone(timeZone);
        event.setEnd(endDateTime);

        // Configurar como evento ocupado
        event.setTransparency("opaque");
        
        // Configurar visibilidad
        event.setVisibility("default");

        // Agregar recordatorios (15 minutos antes)
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(java.util.Arrays.asList(
                        new com.google.api.services.calendar.model.EventReminder()
                                .setMethod("email")
                                .setMinutes(15),
                        new com.google.api.services.calendar.model.EventReminder()
                                .setMethod("popup")
                                .setMinutes(15)
                ));
        event.setReminders(reminders);

        return event;
    }

    private Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.findByEmail(authentication.getName());
        if (usuario == null) {
            throw new GoogleCalendarException("Usuario no encontrado");
        }
        return usuario;
    }
}
