package edu.usco.campusbookings.application.service;

import edu.usco.campusbookings.application.dto.notification.ReservaNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendReservaNotification(ReservaNotification notification, String topic) {
        messagingTemplate.convertAndSend(topic, notification);
    }
}
