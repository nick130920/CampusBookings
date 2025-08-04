package edu.usco.campusbookings.infrastructure.adapter.input.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    
    // Inyectar el ObjectMapper configurado por Spring Boot (incluye JSR310)
    private final ObjectMapper objectMapper;
    
    // Mapa para mantener las sesiones activas: userId -> WebSocketSession
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // Mapa para mantener las sesiones de administradores
    private final Map<Long, WebSocketSession> adminSessions = new ConcurrentHashMap<>();

    // Constructor para inyección del ObjectMapper configurado
    public NotificationWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        logger.info("🔌 Nueva conexión WebSocket establecida: {}", session.getId());
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage textMessage) {
            try {
                String payload = textMessage.getPayload();
                logger.info("📩 Mensaje recibido: {}", payload);
                
                JsonNode jsonNode = objectMapper.readTree(payload);
                String type = jsonNode.get("type").asText();
                
                switch (type) {
                    case "CONNECT":
                        handleConnect(session, jsonNode);
                        break;
                    case "DISCONNECT":
                        handleDisconnect(session, jsonNode);
                        break;
                    default:
                        logger.warn("⚠️ Tipo de mensaje desconocido: {}", type);
                }
                
            } catch (Exception e) {
                logger.error("❌ Error procesando mensaje WebSocket: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        logger.error("❌ Error de transporte WebSocket para sesión {}: {}", session.getId(), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        logger.info("🔌 Conexión WebSocket cerrada: {} - Status: {}", session.getId(), closeStatus);
        
        // Remover la sesión de todos los mapas
        removeSessionFromMaps(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Manejar conexión de cliente
     */
    private void handleConnect(WebSocketSession session, JsonNode jsonNode) {
        try {
            Long userId = jsonNode.get("userId").asLong();
            boolean isAdmin = jsonNode.get("isAdmin").asBoolean();
            
            logger.info("👤 Usuario {} conectándose - Admin: {}", userId, isAdmin);
            
            // Agregar a la sesión de usuarios
            userSessions.put(userId, session);
            
            // Si es admin, también agregarlo a sesiones de admin
            if (isAdmin) {
                adminSessions.put(userId, session);
                logger.info("👑 Administrador {} agregado a sesiones admin", userId);
            }
            
            // Enviar confirmación de conexión
            sendMessage(session, createConnectionResponse(true, "Conectado exitosamente"));
            
            logger.info("✅ Usuario {} conectado exitosamente", userId);
            
        } catch (Exception e) {
            logger.error("❌ Error en conexión: {}", e.getMessage(), e);
            sendMessage(session, createConnectionResponse(false, "Error en conexión: " + e.getMessage()));
        }
    }

    /**
     * Manejar desconexión de cliente
     */
    private void handleDisconnect(WebSocketSession session, JsonNode jsonNode) {
        try {
            Long userId = jsonNode.get("userId").asLong();
            logger.info("👋 Usuario {} desconectándose", userId);
            
            removeSessionFromMaps(session);
            
        } catch (Exception e) {
            logger.error("❌ Error en desconexión: {}", e.getMessage(), e);
        }
    }

    /**
     * Remover sesión de todos los mapas
     */
    private void removeSessionFromMaps(WebSocketSession session) {
        userSessions.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
        adminSessions.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
    }

    /**
     * Enviar notificación a un usuario específico
     */
    public void sendNotificationToUser(Long userId, Object notification) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendMessage(session, notification);
            logger.info("📤 Notificación enviada al usuario {}", userId);
        } else {
            logger.warn("⚠️ Usuario {} no está conectado o sesión cerrada", userId);
        }
    }

    /**
     * Enviar notificación a todos los administradores
     */
    public void sendNotificationToAllAdmins(Object notification) {
        int sentCount = 0;
        
        for (Map.Entry<Long, WebSocketSession> entry : adminSessions.entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                sendMessage(session, notification);
                sentCount++;
            }
        }
        
        logger.info("📤 Notificación enviada a {} administradores", sentCount);
    }

    /**
     * Enviar mensaje a una sesión específica
     */
    private void sendMessage(WebSocketSession session, Object message) {
        try {
            if (session.isOpen()) {
                String jsonMessage = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(jsonMessage));
            }
        } catch (IOException e) {
            logger.error("❌ Error enviando mensaje WebSocket: {}", e.getMessage(), e);
        }
    }

    /**
     * Crear respuesta de conexión
     */
    private Object createConnectionResponse(boolean success, String message) {
        return Map.of(
            "type", "CONNECTION_RESPONSE",
            "success", success,
            "message", message,
            "timestamp", java.time.Instant.now().toString()
        );
    }

    /**
     * Obtener número de usuarios conectados
     */
    public int getConnectedUsersCount() {
        return userSessions.size();
    }

    /**
     * Obtener número de administradores conectados
     */
    public int getConnectedAdminsCount() {
        return adminSessions.size();
    }
}