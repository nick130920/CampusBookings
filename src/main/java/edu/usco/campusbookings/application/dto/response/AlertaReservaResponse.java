package edu.usco.campusbookings.application.dto.response;

import edu.usco.campusbookings.domain.model.AlertaReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaReservaResponse {

    private Long id;
    private Long reservaId;
    private String reservaEscenario;
    private String reservaUsuario;
    private LocalDateTime reservaFechaInicio;
    private LocalDateTime reservaFechaFin;
    private AlertaReserva.TipoAlerta tipo;
    private String tipoDescripcion;
    private LocalDateTime fechaEnvio;
    private AlertaReserva.EstadoAlerta estado;
    private String estadoDescripcion;
    private String mensaje;
    private String canalEnvio;
    private LocalDateTime fechaEnviado;
    private Integer intentosEnvio;
    private String motivoFallo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    /**
     * Determina si la alerta puede ser cancelada
     */
    public boolean puedeSerCancelada() {
        return estado == AlertaReserva.EstadoAlerta.PENDIENTE || 
               estado == AlertaReserva.EstadoAlerta.PROGRAMADO;
    }

    /**
     * Determina si la alerta puede ser reenviada
     */
    public boolean puedeSerReenviada() {
        return estado == AlertaReserva.EstadoAlerta.FALLIDO;
    }

    /**
     * Obtiene el tiempo restante hasta el envío
     */
    public String getTiempoRestante() {
        if (fechaEnvio == null) return "No programado";
        
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(fechaEnvio)) {
            return "Vencido";
        }
        
        long minutos = java.time.Duration.between(ahora, fechaEnvio).toMinutes();
        
        if (minutos < 60) {
            return minutos + " minutos";
        } else if (minutos < 1440) { // 24 horas
            return (minutos / 60) + " horas";
        } else {
            return (minutos / 1440) + " días";
        }
    }
}
