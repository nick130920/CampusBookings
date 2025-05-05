package edu.usco.campusbookings.application.dto.notification;

import lombok.Data;

@Data
public class ReservaNotification {
    private Long reservaId;
    private String estado;
    private String mensaje;
    private String tipo;
    private String destinatario;
}
