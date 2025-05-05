package edu.usco.campusbookings.infrastructure.adapter.input.controller;

import edu.usco.campusbookings.application.dto.notification.ReservaNotification;
import edu.usco.campusbookings.application.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/notifications")
    public void sendNotification(@Payload ReservaNotification notification, 
                               SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getSessionAttributes().get("username").toString();
        notification.setDestinatario(username);
        
        // Enviar notificación a los administradores
        messagingTemplate.convertAndSendToUser("admin", "/queue/notifications", notification);
        
        // Enviar notificación al usuario
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }
}
